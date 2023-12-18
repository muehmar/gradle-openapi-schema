## Nullability

With version 3.0.x of the OpenAPI specification one can declare a property to be nullable:

```
type: string
nullable: true
```

This plugin supports all possible combination of required/optional and nullable properties for serialisation (and
deserialisation) and validation. Required properties which are nullable as well as optional properties which are not
nullable are wrapped into a `java.util.Optional`. Optional properties which are nullable are wrapped into a
special `Tristate` class to properly model all three states (value present, null or absent).

| Required/Optional | Nullability  | Getter return type |
|-------------------|--------------|:-------------------|
| Required          | Not Nullable | T                  |
| Required          | Nullable     | Optional\<T>       |
| Optional          | Not Nullable | Optional\<T>       |
| Optional          | Nullable     | Tristate\<T>       |

### Tristate class

The special `Tristate` class is used for optional properties which are nullable. The `Tristate` class offers a compiler
enforced chain of methods to handle all possible cases:

```
  String result = dto.getOptionalNullableProperty()
    .onValue(val -> "Value: " + val)
    .onNull(() -> "Property was null")
    .onAbsent(() -> "Property was absent");
```

The `onValue` method accepts a `Function` as argument, which gets the value as input. The `onNull` and `onAbsent`
methods accepts a `Supplier` which gets executed in case the property was null or absent.
