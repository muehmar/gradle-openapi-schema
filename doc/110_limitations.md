## Limitations

* The keyword `not` is not supported.
* Multi-Types in v3.1.0 are not supported, i.e. the list in type can contain only one type and optionally the `null`
  type ([Issue 132](https://github.com/muehmar/gradle-openapi-schema/issues/132)).
* For `allOf`, `anyOf` and `oneOf` compositions, properties with the same name but different types or constraints are
  currently not supported. The generator will throw an exception in this
  case ([Issue 133](https://github.com/muehmar/gradle-openapi-schema/issues/133)).
* Only object types are supported with compositions (`allOf`, `anyOf`,
  `oneOf`) ([Issue 265](https://github.com/muehmar/gradle-openapi-schema/issues/265)).
* Conversions for mappings are not supported when the additional-property value type is itself a `Map` whose value type
  requires a conversion (a nested map with a mapped value type). Conversions for other additional-property types are
  supported (see [#307](https://github.com/muehmar/gradle-openapi-schema/issues/307)).
