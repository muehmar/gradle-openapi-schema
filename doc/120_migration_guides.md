## Migrating from v4.x to 5.x

### Breaking Changes

* [#414](https://github.com/muehmar/gradle-openapi-schema/issues/414) - **The property path of constraint violations
  gains the validation-method getter suffix, whose default changed from `Raw` to `_`.** Every property now carries its
  constraints on a dedicated validation getter instead of on the public getter where that happened to be possible, so
  a violation for the property `firstname` is reported for the path `firstname_` rather than `firstname`. This affects
  all constrained properties, not only some shapes: previously the suffix was omitted whenever the api getter itself
  had a distinguishing suffix, which by default is the case for optional and nullable properties (`Opt`/`Tristate`).
  Nested paths gain the suffix on every segment referring to a constrained property, e.g.
  `address_.street_`. Only the segments of computed assertion methods are unchanged, e.g.
  `validAgainstNoOneOfSchema` or `additionalProperties_`.

  Code asserting on violation paths has to be adapted. The suffix is configurable, so the previous default can be
  restored with:
  ```groovy
  validation {
      validationMethods {
          getterSuffix = "Raw"
      }
  }
  ```
  Note this only restores the *suffix*, not the previous shape-dependent omission of it: paths of optional and
  nullable properties keep the suffix in any case.

* [#412](https://github.com/muehmar/gradle-openapi-schema/issues/412) - The all-args constructor of a generated DTO is
  package-private instead of `public`. It takes the companion flags of required-nullable and optional-not-nullable
  properties as positional `boolean` arguments, hence it allowed constructing a DTO with a value and its flag
  contradicting each other - a state no intended construction path can reach and which yields inconsistent validation
  results. Code outside the generated package which constructed DTOs directly has to use the (staged) builder instead.

* [#396](https://github.com/muehmar/gradle-openapi-schema/issues/396) - A `type: number` property which declares no
  format, or a format which is not one of `float`/`double`, is mapped to `Double` instead of `Float`. JSON numbers are
  double-precision, hence a round-trip of such a property silently lost precision. Declare `format: float` explicitly
  to keep `Float`. This also changes the underlying type a `formatTypeMapping` for such a format has to convert from.

* [#394](https://github.com/muehmar/gradle-openapi-schema/issues/394) - The getter of a required additional property
  with a nullable value schema returns `Optional<T>` instead of the raw type. Additionally, `required` means only that
  the key is present, i.e. a present but `null` value is valid now and no `@NotNull` constraint is generated for such a
  property.

### Changes in Runtime Behaviour

* [#415](https://github.com/muehmar/gradle-openapi-schema/issues/415) - Every property is serialized through a
  dedicated JSON getter, which for `date-time` properties carries `@JsonFormat(shape = JsonFormat.Shape.STRING)`.
  Previously a required not-nullable `date-time` property without a type mapping was serialized through the public
  getter, which lacked this annotation: with an `ObjectMapper` that writes dates as timestamps (Jackson 2's default
  `WRITE_DATES_AS_TIMESTAMPS=true`) it was written as a numeric timestamp, while the optional and nullable shapes of
  the same format were already written as ISO-8601 strings. Such a property is now written as an ISO-8601 string as
  well, uniform for all shapes. This is only observable with an `ObjectMapper` that serializes dates as timestamps;
  with `WRITE_DATES_AS_TIMESTAMPS` disabled (the default in Jackson 3) the output is unchanged.

* [#266](https://github.com/muehmar/gradle-openapi-schema/issues/266) - Enum properties are represented internally as
  strings. The generated api (getters, setters, withers) still uses the generated enum or a mapped custom type, but the
  runtime behaviour changes in the following ways:
    * An enum value outside the range of the defined constants no longer throws an exception during deserialisation.
      The value is accepted and reported as a constraint violation when the DTO is validated.
    * Constraint violations for enum properties reference the string-typed validation getter, hence their property
      path gains the validation-method getter suffix as described for
      [#414](https://github.com/muehmar/gradle-openapi-schema/issues/414) above.
    * If validation is disabled or the DTO is not validated, an out-of-range value surfaces as an
      `IllegalArgumentException` when the enum-typed getter is accessed, instead of failing already at
      deserialisation. Validate DTOs before accessing enum-typed getters.
    * In oneOf/anyOf compositions, the additional-properties type check now runs against the JSON-level string value
      of enum properties. Documents that were previously rejected with "Not all additional properties are instances of
      String" — although valid per the specification — now validate successfully.
    * Two subschemas of a composition defining the same property as a container (array or map) of an inline enum are
      now rejected with an error, as each subschema defines its own nested enum class. This was already the case for a
      property defined as a plain inline enum; define the enum once as a root schema and reference it instead.
    * A property used as discriminator which is mapped to another type *without* a conversion is now rejected with an
      error, as there is no way to convert the discriminator value defined in the specification into the mapped type
      (this generated uncompilable code before). Either define a conversion for the mapping or remove the mapping.

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
* Since Jackson 3 is supported now, `jsonSupport = "jackson"` as well as `xmlSupport = "jackson"` is removed from
  the options. The properties have the following options now (as you can see in the following table):

  | Property     | Options                     | 
    |:-------------|:----------------------------|
  | jsonSupport  | jackson-2, jackson-3, none  |
  | xmlSupport   | jackson-2, jackson-3, none  |

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
