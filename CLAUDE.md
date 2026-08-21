# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## What this is

A Gradle plugin (`com.github.muehmar.openapischema`) that generates immutable Java
DTOs from the `#/components/schemas` section of an OpenAPI 3.0.x / 3.1.0
specification: immutable classes, staged builders, Jackson 2.x/3.x (de)serialization,
optional XML, and Bean Validation (javax 2.x / Jakarta 2.x/3.x) including
object-level constraints.

## Commands

Build via the Gradle wrapper (`./gradlew`). Requires JDK 17.

This repository is a Gradle **composite build**: the root build owns no projects
and aggregates three included builds — `build-logic` (convention plugins),
`plugin-build` (`plugin`, `java-snapshot`) and `example-build` (the example
projects and the `mapper` test helpers). Task paths are therefore prefixed with
`-p plugin-build` or `-p example-build`; `./gradlew build` at the root still
builds everything.

- **Build the plugin:** `./gradlew -p plugin-build :plugin:build`
- **Plugin unit/snapshot tests:** `./gradlew -p plugin-build :plugin:test`
- **A single test class:** `./gradlew -p plugin-build :plugin:test --tests "*ObjectPojoGeneratorTest"`
- **Update snapshots** after an intended generator-output change:
  `./gradlew -p plugin-build :plugin:test -PupdateSnapshot=<ClassName>` (or `-PupdateSnapshot=all`).
  Prefer this over hand-editing `*.snap` files, though hand-editing works for a
  known one-line change.
- **Formatting:** Spotless with `googleJavaFormat`. `spotlessApply` runs
  automatically before `compileJava`, so formatting is applied on every build; no
  separate step is normally needed.

The example/consumer modules mirror CI (see `.github/workflows/gradle.yml`):

- `./gradlew -p example-build :example:test` — end-to-end tests (Jackson 3)
- `./gradlew -p example-build :example:jackson208Test` / `:example:jackson219Test` —
  same test sources against pinned Jackson 2.x versions
- `./gradlew -p example-build :example-jakarta-3:test`
- `./gradlew -p example-build :springboot{2,3,4}-example:integrationTest`

## Testing

Testing has two layers with different purposes:

- **Plugin tests (`plugin-build/plugin/src/test`)** — fast, run against local source, and are the
  primary way to verify generator changes. Most are **snapshot tests**: a generator
  is invoked against a hand-built model and its rendered output is compared to a
  committed `*.snap` file under a sibling `__snapshots__/` directory
  (java-snapshot-testing). Marked with the shared `@SnapshotTest` fixture (defined in
  the `java-snapshot` module in `plugin-build`) and asserted via `expect.toMatchSnapshot(...)`. When a
  change intentionally alters generated output, regenerate with
  `-PupdateSnapshot=<ClassName>` and review the `*.snap` diff as part of the change —
  the snapshot *is* the assertion. Test models are built with helpers like
  `TestJavaPojoMembers`, `JavaPojos`, and `TestPojoSettings`.
- **Example/consumer tests (all under `example-build/`: `example/`,
  `example-jakarta-3/`, `springboot*-example/`)**
  — end-to-end: they apply the plugin, generate code from real OpenAPI specs, and
  then compile and exercise it. This is the only layer that catches
  compilation/runtime problems in the generated code. It runs against the plugin
  built from local source (see below). The same example test
  sources run across multiple Jackson versions via `jvm-test-suite` (`test` =
  Jackson 3, `jackson208Test`, `jackson219Test`), so tests must stay
  Jackson-generation-agnostic (use `MapperFactory.jsonMapper()` and
  `ValidationUtil.validate(...)`).

A bug fix typically touches both layers: update/extend the relevant snapshot
test(s) in `plugin-build/plugin/`, and add an example issue-reproduction test
(below) that proves the generated code behaves correctly at runtime.

### Running example tests against local plugin changes

The example modules build the plugin **from source**: `example-build/settings.gradle`
declares `includeBuild('../plugin-build')` inside `pluginManagement`, so Gradle
substitutes the local `plugin` project for the `com.github.muehmar.openapischema`
plugin marker. Editing `plugin-build/plugin/` and running an example test picks the
change up directly — no publishing step and no version bump:

```
./gradlew -p example-build :example:test --tests "*IssueNNNTest"
```

This is what makes it possible to add a spec, its tests and the plugin fix in the
same commit.

## Adding an issue-reproduction test

The repo convention is to first commit a **failing** test that asserts the desired
behavior, then fix the plugin.

- Spec: `example-build/example/src/main/resources/issues/openapi-issue-NNN.yml` — a **single**
  spec per issue; cover variants with additional schemas in that spec rather than
  a second spec file.
- Register `'NNN'` in the `issueNumbers` list in `example-build/example/build.gradle`; DTOs are
  generated into package `com.github.muehmar.gradle.openapi.issues.issueNNN` for
  the Jackson 2 and 3 suites and the noJson/noValidation source sets.
- Per-schema config overrides go in a **single** `"${jacksonMajorVersion}IssueNNN { }"`
  block inside the loop (mappings, warnings, etc.), but those do **not** reach the
  noJson/noValidation variants — if a spec needs an override there, wire it manually
  outside `issueNumbers`. Custom classes used as mapping targets live in
  `example-build/example/src/main/java/.../issues/issueNNN/` and may reference generated DTOs of
  the same package.
- The example project sets `failOnWarnings = true` (incl. `UNSUPPORTED_VALIDATION`
  and `MISSING_MAPPING_CONVERSION`): any mapping needs a conversion, and
  unsupported-validation specs will fail generation.
- Tests: `example-build/example/src/test/java/.../issues/issueNNN/IssueNNNTest.java`; an issue
  covering several aspects may split them into multiple `IssueNNN<Aspect>Test`
  classes in the same `issueNNN` package. Use `ValidationUtil.validate(...)` for
  bean-validation assertions and `MapperFactory.jsonMapper()`
  (Jackson-generation-agnostic) for JSON.

## Architecture

All plugin paths below are relative to
`plugin-build/plugin/src/main/java/com/github/muehmar/gradle/openapi/`.

The pipeline turns an OpenAPI spec into `.java` files in five stages:

```
DSL (openApiGenerator{schemas{...}})
  → PojoSettings + GenerateSchemasTask         dsl/, settings/, task/
  → generic spec model (Pojo, PojoMember)      generator/mapper/ → generator/model/
  → Java model (JavaPojo, JavaPojoMember)      generator/java/model/
  → composed Generator<A,B> chains render code generator/java/generator/
  → GeneratedFile written to source set        writer/
```

1. **Entry / task wiring** — `OpenApiSchemaGenerator.java` is the `Plugin<Project>`.
   In `afterEvaluate`, per configured schema it registers a `GenerateSchemasTask`
   (`task/`), wires it as a `dependsOn` of `compile<SourceSet>Java`, and adds the
   output dir to the source set. The task's `runTask()` orchestrates map → generate
   → write → warnings.

2. **DSL → settings** — `openApiGenerator { schemas { ... } }` binds to
   `dsl/OpenApiSchemaExtension` (project-wide defaults + a container of
   `dsl/SingleSchemaExtension`). Each schema block is merged with the common
   defaults, then `SingleSchemaExtension.toPojoSettings(...)` converts it into the
   immutable `generator/settings/PojoSettings` — the single DSL→settings boundary.
   `PojoSettings` carries no Gradle types (it is a serializable `@Input`) and is
   threaded as the `B` type through every generator.

3. **Mapping (spec → generic model)** — `generator/mapper/SpecificationMapperImpl`
   runs a worklist loop over `MapContext`, transitively following `$ref` across
   files and calling `PojoSchema.mapToPojo()`, then hands off to
   `resolver/MapResultResolverImpl` to resolve references and post-process into a
   final list of `Pojo`. `generator/model/` is the language-agnostic model: `Pojo`
   (a `fold(onObject,onArray,onEnum)` visitor over `ObjectPojo`/`ArrayPojo`/`EnumPojo`),
   `PojoMember`, `constraints/Constraints`, and `schema/` wrappers for each
   swagger-parser schema kind.

4. **Java model + rendering** — `JavaPojo.wrap(...)` re-wraps the generic model into
   a Java view (`generator/java/model/`), applying configured type mappings.
   `JavaPojoGenerator` dispatches to `ObjectPojoGenerator` / `EnumGenerator` /
   `ArrayPojoGenerator`, each composed from many small sub-generators under
   `generator/java/generator/pojo/**` and cross-cutting `generator/shared/**`
   (jackson, validation, builders, getters, wither, etc.).

5. **Writing** — generators return `writer/GeneratedFile(Path, content)`, written by
   `writer/BaseDirFileWriter`.

### The `Generator<A,B>` abstraction

Code rendering uses the external `io.github.muehmar.codegenerator` library.
`Generator<A,B>` is a pure functional interface `Writer generate(A data, B settings,
Writer writer)`, where `Writer` is an immutable string builder that also tracks
imports via `ref(...)`. Generators compose declaratively: `.append(other)` chains
output, `.append(gen, f)` adapts the input type, `.filter(predicate)` conditionally
suppresses output, plus layout helpers. A whole file is one big `append`-chain of
tiny generators (see `ObjectPojoGenerator.content()`).
`UniqueItemsValidationMethodGenerator` is a minimal example: build a `MethodGen`,
`.append` the needed import ref, `.filter` so it only emits for array members with a
uniqueItems constraint.

### Design conventions

- **Rich domain models, not anemic data holders.** Types in `generator/model/`
  encapsulate their own logic and decisions rather than exposing raw fields for
  callers to branch on. `Pojo` carries its `fold` visitor; small value objects like
  `Necessity`, `Nullability`, and `Constraints` answer questions with intent-named
  methods (e.g. `isRequiredAndNotNullable()`, `isOptionalAndNullable()`). When
  adding behavior, prefer a method on the relevant model/value object over an
  `instanceof`/field check at the call site — mirror the surrounding code.
- **Generators are pure and composable.** Keep new rendering logic as a small
  `Generator`/`MethodGen` that plugs into an existing `append`-chain, and gate it
  with `.filter(...)` rather than conditionals scattered in the caller.
- **Snapshots are the contract for generated output.** Any change to rendered code
  will shift one or more `*.snap` files; search `plugin-build/plugin/src/test` for the old
  rendered fragment (not just the obviously-named test) and regenerate all matches.

## Changelog

`doc/130_change_log.md`. Add new entries under a `next` heading (the concrete
version is decided at release time), matching the existing `[#NNN](url) - description`
format.
