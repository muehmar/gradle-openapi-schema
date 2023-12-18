## Migration Guide from v1.x to 2.x
* The factory method for the builder was renamed from `newBuilder()` to `builder()`.
* The constructor of the DTO's contains now also a map for the additional parameters. Using the builder is recommended
  instead of the constructor.
* DTO's support now every combination of compositions and properties. The fold method in a DTO is therefore renamed
  to either foldOneOf or foldAnyOf, depending on the used composition.
* The static factory methods `fromXY` for creating a composed DTO are removed as well as the `withXY` methods for anyOf
  compositions. These DTO's are now created with the Builder too.
* The constructor for free form DTO's (i.e. Map DTO's) is now package private and thus intentionally not accessible by
  client code. There exists a factory method `fromProperties` now.
