## Migrating from v3.x to 4.x

### Breaking Changes

* The parameters generation was deprecated in v3.3.0 and is now removed in v4.x. You either have to hardcode themselves
  or use the official OpenAPI Generator to generate the API as well. There is a section in the documentation describing
  how to integrate the official OpenAPI Generator with this
  plugin: [Integration with official OpenAPI generator](095_official_openapi_generator_integration.md).
* Version 4.x of the plugin requires Java 11 as minimum JDK version.
* 'partial-time' is removed as supported format for string types and replaced by 'time' from RFC 3339.
* Since the string format 'date-time' is based on RFC 3339, the timezone is mandatory and is represented as
  ZonedDateTime in Java instead of LocalDateTime. If you don't care about the timezone, you can still use LocalDateTime
  with a class mapping:
    ```groovy
    classMapping {
        fromClass = "ZonedDateTime"
        toClass = "java.time.LocalDateTime"
    }
    ```
* Map structures as additional properties of objects will now result in a separate DTO class instead of a Map<String,
  Object>.
* The DSL concerning the configuration of the validation is restructured. Some options are moved into a new `validation`
  block:
    ```groovy
    openApiGenerator {
        enableValidation = true
        validationApi = "jakarta-3.0"
        nonStrictOneOfValidation = false
        validationMethods {
            getterSuffix = "Raw"
            modifier = "private"
            deprecatedAnnotation = false
        }
    }
    ```
  becomes
    ```groovy
    openApiGenerator {
        validation {
            enabled = true
            validationApi = "jakarta-3.0"
            nonStrictOneOfValidation = false

            validationMethods {
                getterSuffix = "Raw"
                modifier = "private"
                deprecatedAnnotation = false
            }
        }
    }
    ```
  The structure of the `validation` is the same if globally configured or configured per specification.

## Migrating from v2.x to 3.x

### Breaking Changes

* A discriminator in an anyOf composition was ignored in versions before v3.x. but is not supported and will lead to
  different code for the DTO (the code is similar to a oneOf composition).
* The getter and wither method for Array-DTO's are renamed from `getValue()` to `getItems()` and `withValue()` to
  `withItems()`.
* The staged builder for allOf compositions is refactored. It is either possible to use only setters of single
  properties or only setters for DTO's of the composition.
* The validation of optional and not nullable properties is implemented in v3.x. This changes the constructor of the
  DTO's, i.e. a companion flag is added. But the constructor itself is not recommended to be used to construct a DTO.
  The staged builder is not affected by this change, but handles the construction of the DTO's correctly.
* Stage classes of the staged builder are moved into an inner static class of the builder. Direct usage of these stage
  classes is not recommended but if used, it must be referenced via the new inner static class.
* Some changes are made concerning the nullable keyword to be consistent with the clarification in the specification
  v3.0.3. This includes
    * `nullable: true` for enums will no longer have an effect, i.e. the enum will not be nullable
        * This could be turned off since v3.8.0 of the plugin via `allowNullableForEnums = true` in the configuration
    * An empty schema is nullable per default, i.e. `nullable: false` has no effect
* The nullable keyword for array items and additional properties are supported now and will lead to changes in the DTO,
  e.g. return type of getters are wrapped into Optional.

### Configuration change

The special builder pattern is renamed from SafeBuilder to StagedBuilder. Instead of just a single flag to enable or
disable the generation

```groovy
enableSafeBuilder = true
```

there is a new configuration block with the enabled flag:

```groovy
stagedBuilder {
    enabled = true
}
```

## Migrating from v1.x to 2.x

* The factory method for the builder was renamed from `newBuilder()` to `builder()`.
* The constructor of the DTO's contains now also a map for the additional parameters. Using the builder is recommended
  instead of the constructor.
* DTO's support now every combination of compositions and properties. The fold method in a DTO is therefore renamed to
  either foldOneOf or foldAnyOf, depending on the used composition.
* The static factory methods `fromXY` for creating a composed DTO are removed as well as the `withXY` methods for anyOf
  compositions. These DTO's are now created with the Builder too.
* The constructor for free form DTO's (i.e. Map DTO's) is now package private and thus intentionally not accessible by
  client code. There exists a factory method `fromProperties` now.
