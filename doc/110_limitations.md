## Limitations

* The keyword `not` is not supported.
* Multi-Types in v3.1.0 are not supported, i.e. the list in type can contain only one type and optionally the `null`
  type.
* For `allOf`, `anyOf` and `oneOf` compositions, properties with the same name but different types or constraints are
  currently not supported. The generator will throw an exception in this case.
