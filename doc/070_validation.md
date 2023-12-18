## Validation

The generation of annotations for validation can be enabled by setting `enableValidation` to `true`. It requires at
least version 2.0 of the java/jakarta validation api as dependency. It supports object graph validation via the `@Valid`
annotation.

| Validation API          | Supported versions |
|-------------------------|--------------------|
| Java Bean Validation    | 2.0                |
| Jakarta Bean Validation | 2.0 and 3.0        |

### Type specific constraints

The following type specific constraints are supported:

| Type / Format                       | Keyword                                                       | Annotation                             | Remark                                                                |
|-------------------------------------|---------------------------------------------------------------|----------------------------------------|-----------------------------------------------------------------------|
| number / double<br/>number / float  | minimum<br/>exclusiveMinimum<br/>maximum<br/>exclusiveMaximum | `@DecimaleMin`<br/>`@DecimalMax`       |                                                                       |
| integer / int32<br/>integer / int64 | minimum<br/>exclusiveMinimum<br/>maximum<br/>exclusiveMaximum | `@Min`<br/>`@Max`                      |                                                                       |
| string                              | minLength<br/>maxLength                                       | `@Size`                                |                                                                       |
| string                              | pattern                                                       | `@Pattern`                             |                                                                       |
| string / email                      | -                                                             | `@Email`                               |                                                                       |
| array                               | minItems<br/>maxItems                                         | `@Size`                                |                                                                       |
| integer / number                    | multipleOf                                                    | Special validation method is generated | Validation for number types might be unreliable due to numeric errors |


### Required properties
The validation of required properties is supported through the `@NotNull` annotation. Required properties marked
as `nullable: true` (in v3.0.x) or with the additional type `null` (in v3.1.0) are also supported for validation.

### Object level validation

The following keywords are supported:

* `minProperties` and `maxProperties` for object types
* `uniqueItems` for array types
* `additionalProperties = false` gets validated

The plugin generates a method which returns the number of present properties of an object which is annotated with the
constraints (if present).

### Composition
The validation of composed objects with `anyOf`, `oneOf` and `allOf` are supported. While the `allOf` objects simply
inherit all properties of the specified schemas, the validation will simply be performed like for any other object
schema.

For `anyOf` and `oneOf` compositions, the created DTO contains specific annotated methods only for validation. With
these methods, it can be validated that the object is valid against exactly one schema (`oneOf`) or is valid against at
least one schema (`anyOf`).

REMARK: Currently, an edge case is not supported: While validating against how many schemas the current object is valid
against, only the presence of the required properties are considered (and possible absence of all other properties in case
no additional properties are allowed via `additionalProperties = false`). This means, if all required properties of two
(or more) schemas are present for a `oneOf` composition, but a property of one schema is not valid, the object should be
considered as valid but it will result in an invalid object as the required properties of two schemas are present.

### Examples
* [OpenAPI spec](example/src/main/resources/openapi-validation.yml)
* [Directory with Tests](example/src/test/java/com/github/muehmar/gradle/openapi/validation)

Samples with Tests for compositions can be found here:
* [OneOf Validation](example/src/test/java/com/github/muehmar/gradle/openapi/oneof/TestValidation.java)
* [AnyOf Validation](example/src/test/java/com/github/muehmar/gradle/openapi/anyof/TestValidation.java)

## Keywords `readOnly` and `writeOnly`
These keywords for properties are supported. If used, three different DTO's for the same schema are generated:

* Normal DTO containing all properties
* Response DTO containing general and `readOnly` properties
* Request DTO containing general and `writeOnly` properties.

The DTO's are named accordingly, i.e. the normal DTO is named like normal DTO's, the response DTO is suffixed with
`Response` and the request DTO is suffixed with `Request`. This suffix is added before any configured general suffix,
i.e. if the suffix `Dto` is configured and a schema `Example` contains properties marked as `readOnly` or `writeOnly`,
then the following DTO's are generated:

* ExampleDto
* ExampleResponseDto
* ExampleRequestDto
