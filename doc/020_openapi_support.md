## Supported OpenAPI versions

This plugin supported the generation of v3.0.x and v3.1.0

## OpenAPI v3.0.x vs v3.1.0

The version 3.1.0 of the OpenAPI specification is not backwards compatible with 3.0.x, i.e. has some breaking changes.
The most obvious change (in the schema section) is the specification of the type, in 3.0.x it is a single property,
whereas in 3.1.0 the type is an array. This plugin does currently not support multiple types with one exception: the
`null` type.

The following in v3.0.x:

```yaml
type: string
nullable: true
```

is equivalent to in v3.1.0:

```yaml
type:
  - string
  - null
```

Any other combination of types is currently not supported.