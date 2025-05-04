## Parameters

NOTE: This feature is deprecated and will be removed in the next major version 4.x.

The OpenAPI supports parameters in the `#/components/parameters` section. The plugin will generate for each parameter a
class which contains the constraints of the parameter. For example the specification

```yaml
components:
  parameters:
    limitParam:
      in: query
      name: limit
      required: false
      schema:
        type: integer
        minimum: 1
        maximum: 50
        default: 20
      description: The numbers of items to return.
```

will create the following class

```java
public final class LimitParam {
    private LimitParam() {
    }

    public static final Integer MIN = 1;
    public static final Integer MAX = 50;
    public static final Integer DEFAULT = 20;
    public static final String DEFAULT_STR = "20";

    public static boolean exceedLimits(Integer val) {
        return val < MIN || MAX < val;
    }
}
```

The method `exceedLimits` will contain the conditions depending on the presence of the `minimum` and `maximum`
constraint. In the case both are missing, the method will simply return `false`.

A default number is also rendered as string which may be used as default value in Spring annotations for parameters.

### Supported schemas

Currently, the following schemas are supported:

* `integer`
    * minimum
    * maximum
    * default
