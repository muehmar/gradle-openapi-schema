# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Gradle plugin that generates Java code from OpenAPI 3.0.x/3.1.0 specifications. Unlike standard OpenAPI generators, this focuses specifically on the `#/components/schemas` section, generating immutable POJOs with staged builders, Jackson JSON support, and comprehensive validation.

The plugin (`com.github.muehmar.openapischema`) processes OpenAPI schemas and generates type-safe Java classes with:
- Immutable data classes with staged builder pattern
- JSON/XML serialization via Jackson
- Support for compositions (allOf, anyOf, oneOf)
- Bean validation (javax/jakarta)
- Additional property handling

## Development Commands

### Building
```bash
./gradlew build                    # Build all modules
./gradlew plugin:build             # Build only the plugin module
./gradlew clean build              # Clean build from scratch
```

### Testing
```bash
./gradlew test                     # Run unit tests for all modules
./gradlew plugin:test              # Run plugin unit tests only
./gradlew integrationTest          # Run integration tests
./gradlew testAll                  # Run all tests (unit + integration)
```

**Snapshot Testing**: The project uses snapshot testing (java-snapshot-testing library) extensively. To update snapshots when expected output changes:
```bash
./gradlew test -DupdateSnapshot=true
```

### Code Quality
```bash
./gradlew spotlessCheck            # Check code formatting
./gradlew spotlessApply            # Auto-format code
./gradlew check                    # Run all verification tasks
```

### Plugin Development
```bash
./gradlew plugin:publishToMavenLocal   # Publish plugin to local Maven repo for testing
```

### Running Examples
The repository includes example projects that use the plugin:
```bash
./gradlew example:build            # Standard example
./gradlew example-jakarta-3:build  # Jakarta validation 3 example
./gradlew spring2-example:bootRun  # Run Spring Boot 2 example
./gradlew spring3-example:bootRun  # Run Spring Boot 3 example
```

## Architecture

### Module Structure

- **plugin/** - Core Gradle plugin implementation
- **example/** - Example project using the plugin (javax validation)
- **example-jakarta-3/** - Example with Jakarta validation 3.x
- **spring2-example/** - Spring Boot 2 integration example
- **spring3-example/** - Spring Boot 3 integration example
- **java-snapshot/** - Snapshot testing utilities (test fixtures)
- **buildSrc/** - Custom Gradle plugins for Java version configuration

### Plugin Entry Point

`OpenApiSchemaGenerator` (plugin/src/main/java/com/github/muehmar/gradle/openapi/OpenApiSchemaGenerator.java) implements the Gradle `Plugin` interface and:
1. Creates the `openApiGenerator` DSL extension
2. Sets up code generation tasks dynamically based on configured schemas
3. Each schema config creates a `generate<Name>Model` task (e.g., `generateApiV1Model`)
4. These tasks run before the corresponding `compileJava` task
5. Generated sources are added to the appropriate source set

### Code Generation Pipeline

The generation process flows through these key packages:

1. **task/** - `GenerateSchemasTask` orchestrates the generation process
2. **mapper/** - Maps OpenAPI specification to internal model
   - `SpecificationMapper` reads OpenAPI YAML/JSON files
   - Resolves `$ref` references across specification files
   - Creates `Pojo` model objects representing schemas
3. **generator/model/** - Internal representation of schemas
   - `Pojo` - Core model for generated POJOs
   - `pojo/` - Properties, compositions, constraints
   - `type/` - Type system (primitives, arrays, objects, etc.)
4. **generator/java/** - Java code generation
   - `JavaPojoGenerator` - Main generator entry point
   - `generator/java/generator/pojo/` - POJO class generation
   - `generator/java/generator/pojo/builder/` - Builder generation
   - `generator/java/generator/pojo/stagedbuilder/` - Staged builder pattern
   - `generator/java/generator/pojo/getter/` - Getter methods
   - Uses `io.github.muehmar:code-generator` library for template-based generation

### Key Generation Components

- **Staged Builder**: Generates type-safe builder interfaces where required properties become sequential method calls (see `generator/java/generator/pojo/stagedbuilder/`)
- **Composition Handling**: Special generators for allOf/anyOf/oneOf (`generator/java/generator/pojo/composition/`)
- **Validation**: Generates validation logic for constraints defined in schemas (`generator/java/generator/pojo/validation/`)
- **Additional Properties**: Handles dynamic properties via Map (`generator/java/generator/pojo/getter/additionalproperties/`)

## Testing Approach

### Unit Tests
- Located in `plugin/src/test/java/`
- Mirror the package structure of main code
- Use JUnit 5 + Mockito
- **Snapshot tests** for generated code - compare output against committed snapshots

### Integration Tests
- `plugin/src/test/java/com/github/muehmar/gradle/openapi/integration/`
- Use `TemporaryProject` helper to create real Gradle projects
- Test the full plugin lifecycle: parse spec → generate code → compile

### Running Specific Tests
```bash
# Run a single test class
./gradlew plugin:test --tests "ClassName"

# Run tests matching a pattern
./gradlew plugin:test --tests "*Validation*"

# Run with more detailed output
./gradlew plugin:test --info
```

## Configuration DSL

The plugin is configured via the `openApiGenerator` extension in `build.gradle`:

```groovy
openApiGenerator {
    schemas {
        apiV1 {  // Name becomes part of task name: generateApiV1Model
            inputSpec = "$projectDir/src/main/resources/openapi-v1.yml"
            packageName = "com.example.api.v1"
            // ... other settings
        }
    }
}
```

Configuration classes are in `plugin/src/main/java/com/github/muehmar/gradle/openapi/dsl/`.

## Important Implementation Details

- The plugin uses **swagger-parser** library to parse OpenAPI specifications
- Code generation leverages the **io.github.muehmar:code-generator** library (a custom template engine)
- Generated code uses **Lombok** (`@Value`, `@Builder.Default`) internally in the plugin
- The `writer/` package handles file output, writing generated classes to the configured output directory
- Incremental build support tracks specification file changes to avoid unnecessary regeneration

## Documentation

Extensive user-facing documentation is in the `doc/` directory. Key files:
- `010_configuration.md` - All plugin configuration options
- `040_compositions.md` - How allOf/anyOf/oneOf are handled
- `060_staged_builder.md` - Staged builder pattern explanation
- `070_validation.md` - Validation features
