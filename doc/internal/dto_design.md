## DTO Design

Internal documentation of the code generator — this is *not* part of the user-facing documentation under `doc/`.
It describes the concepts behind the structure of the generated DTO's: where serialisation happens, where validation
happens and which getters are part of the public API of a generated class.

The user-facing counterparts are [Compositions](../040_compositions.md) (how to *use* compositions) and
[Validation](../070_validation.md) (*which* constraints are supported).

### Overview

| DTO kind | Serialisation      | Property constraints                      | Composition constraint                    | Flat-field getters |
|----------|--------------------|-------------------------------------------|-------------------------------------------|--------------------|
| simple   | own getters        | own annotations                           | –                                         | public             |
| `allOf`  | own flat getters   | none on itself, delegated to member DTO's | implicit (must be valid against all)      | public             |
| `oneOf`  | own flat getters   | none on itself, delegated to member DTO's | valid against exactly one member schema   | package-private    |
| `anyOf`  | own flat getters   | none on itself, delegated to member DTO's | valid against at least one member schema  | package-private    |

The three concepts behind this table are described below.

### 1. Composed DTO's are flat

A DTO for an `allOf`, `oneOf` or `anyOf` composition does **not** hold references to the DTO's of its member schemas.
It declares the union of all member properties as its own fields, and reconstructs a member DTO on demand via a private
`as<Member>Dto()` conversion method. Properties which do not belong to the target schema are passed on as additional
properties, so no data is lost.

This single decision explains the rest of the table: the composed DTO owns the data, so it serialises itself; but it
cannot judge the member schemas on its own, so it delegates validation to the DTO's it converts itself into.

### 2. Serialisation is never delegated

The composed DTO renders its own flat fields. The resulting JSON has no wrapper or envelope, so a `oneOf` DTO
serialises exactly like the matching member schema would, and deserialisation goes through the generated builder.

The `...OneOfContainerDto` / `...AnyOfContainerDto` classes play no role here — they are annotation-free helper
classes used only as input for the staged builder.

### 3. Validation is delegated

A composed DTO carries no property constraints of its own. Instead it validates by converting itself into its member
DTO's, which do carry the constraints, and checking those.

How the result is enforced differs per composition kind:

* `allOf` — the member DTO's are validated via `@Valid`, which is a plain conjunction: all of them must be valid.
* `oneOf` / `anyOf` — the member constraints must *not* be lifted onto the composition, as a required property of one
  schema would wrongly reject an object valid against another. Instead the number of matching member schemas is
  counted and constrained: exactly one for `oneOf`, at least one for `anyOf`. A discriminator adds the requirement that
  the matching schema is the one the discriminator points to.

Independently of the validation annotations, every DTO also generates an `isValid()` method which re-implements its
constraints in plain Java. It exists because selecting the matching member schema must work **without** a validation
API on the classpath — decomposition and JSON round-tripping depend on it. Note that the annotations and `isValid()`
are therefore two parallel implementations of the same constraints and have to be kept in sync by the generator.

### 4. Getter visibility follows the composition kind

For a simple or an `allOf` DTO every property is unconditionally part of the object, so the getters are public and
withers are generated. An `allOf` DTO additionally exposes its member DTO's directly.

For a `oneOf` / `anyOf` DTO the getters of the flat fields are package-private and no withers are generated. At the
level of the composition a property has no well-defined meaning: it belongs to *whichever* member schema happens to
match, and the object may match none. The public API is therefore the decomposition itself — the `foldOneOf(...)` /
`foldAnyOf(...)` methods and the `get<Member>Dto()` accessors — and properties become public again on the member DTO
one obtains from it.

In nested compositions the visibility follows the composition kind at each level, not the nesting depth.

### Details

The above describes the concepts. To inspect the concrete generated shape — the exact method names, annotations and
their placement — the generated sources of the example project are the best reference. They are written to
`example-build/example/build/generated/openapi` when running the example tests.

* [OneOf validation](../../example-build/example/src/test/java/com/github/muehmar/gradle/openapi/oneof/ValidationTest.java)
* [OneOf serialisation](../../example-build/example/src/test/java/com/github/muehmar/gradle/openapi/oneof/SerialisationTest.java)
* [AllOf fields](../../example-build/example/src/test/java/com/github/muehmar/gradle/openapi/allof/AllOfDtoFieldTest.java)

The relevant generator code lives in `generator/java/generator/pojo/composition/` (conversion, fold and validation
methods) and `generator/java/generator/pojo/getter/definition/GetterGroupsDefinition` (getter visibility per member
type).
