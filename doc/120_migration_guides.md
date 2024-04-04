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
  * An empty schema is nullable per default, i.e. `nullable: false` has no effect
* The nullable keyword for array items and additional properties are supported now and will lead to changes in the
  DTO, e.g. return type of getters are wrapped into Optional.

### Configuration change
The special builder pattern is renamed from SafeBuilder to StagedBuilder. Instead of just a single flag to enable or 
disable the generation
```
enableSafeBuilder = true
```
there is a new configuration block with the enabled flag:
```
stagedBuilder {
    enabled = true
}
```

## Migrating from v1.x to 2.x
* The factory method for the builder was renamed from `newBuilder()` to `builder()`.
* The constructor of the DTO's contains now also a map for the additional parameters. Using the builder is recommended
  instead of the constructor.
* DTO's support now every combination of compositions and properties. The fold method in a DTO is therefore renamed
  to either foldOneOf or foldAnyOf, depending on the used composition.
* The static factory methods `fromXY` for creating a composed DTO are removed as well as the `withXY` methods for anyOf
  compositions. These DTO's are now created with the Builder too.
* The constructor for free form DTO's (i.e. Map DTO's) is now package private and thus intentionally not accessible by
  client code. There exists a factory method `fromProperties` now.
