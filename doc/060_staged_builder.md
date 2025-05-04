## Staged Builder

The 'Staged Builder' is an extended builder pattern which enforces one to create valid instances, i.e. every required
property in a class will be set. A DTO contains two factory methods to create a builder:

* `builder()`
* `userDtoBuilder()` Factory method with the classname, can be used to statically import the method

This is done by creating a single builder class (stage) for each required property, with a single method setting the
corresponding property and returning the next builder stage for the next property. The `build`
method will only be present after each required property is set.

For example, given the schema:

```yaml
components:
  schemas:
    User:
      required:
        - name
        - city
      properties:
        name:
          type: string
        city:
          type: string
        age:
          type: integer
```

will lead to a builder which can be used like the following:

```java
  User user = User.builder()
        .setName("Dexter")
        .setCity("Miami")
        .andAllOptionals()
        .setAge(39)
        .build();
```

This does not seem to be very different from the normal builder pattern at a first glance but calling `builder()`
will return a class which has only a single method `setName()`, i.e. the compiler enforces one to set the name. The
returned class after setting the name has again one single method `setCity()`. As the property `city` is the last
required property in this example the returned class for `setCity()` offers three methods:

* `build()` As all required properties are set at that time, building the instance is allowed here.
* `andOptionals()` Returns the normal builder allowing one to set certain optional properties before creating the
  instance. This method returns just the normal builder populated with all required properties.
* `andAllOptionals()` Enforces one to set all optional properties in the same way as it is done for the required
  properties. The `build()` method will only be available after all optional properties have been set. This method is
  used in the example above, i.e. the compiler enforces one to set the `age` property too.

Setting all required properties in a class could theoretically also be achieved with a constructor with all required
properties as arguments, but the pattern used here is safer in terms of refactoring, i.e. adding or removing properties,
changing the required properties or changing the order of the properties.

When using `andAllOptionals()` or `andOptinoals()` after all required properties are set, the builder provides
overloaded methods to add the optional properties. The property can be set directly or wrapped in an `Optional`. In the
example above, the builder provides methods with the following signature:

```java
public Builder setAge(Integer age);

public Builder setAge(Optional<Integer> age);
```

Note that the prefix of the methods is customizable, see the `Configuration` section.

### Building `allOf` composition

The builder enforces to set all `allOf` compositions correctly. For each allOf subschema, the builder enforces to set
the corresponding DTO. In case a subschema contains no compositions, it will provide also single setters for each
property as alternative to the singel DTO setter. For example, given the following schema:

```yaml
    BaseOrder:
      required:
        - orderNumber
        - title
      properties:
        orderNumber:
          type: integer
        title:
          type: string

    Invoice:
      allOf:
        - $ref: '#/components/schemas/BaseOrder'
        - type: object
          required:
            - paid
          properties:
            remark:
              type: string
            paid:
              type: boolean
```

The builder offers to set an instance of BaseOrderDto:

```java
BaseOrderDto baseOrderDto = BaseOrderDto.builder()
        .setOrderNumber(123)
        .setTitle("Invoice")
        .build();

InvoiceDto invoiceDto = InvoiceDto.builder()
        .setBaseOrder(baseOrderDto)
        .setPaid(true)
        .setRemark("Remark")
        .build();
```

or set the properties directly:

```java
InvoiceDto invoiceDto = InvoiceDto.builder()
        .setOrderNumber(123)
        .setTitle("Invoice")
        .setPaid(true)
        .setRemark("Remark")
        .build();
```

### Building `oneOf` composition

If a DTO contains a `oneOf` composition, the builder will enforce one to set exactly one of the DTO's. The builder
offers one to set one of the DTO's directly or set an extra 'container' instance for the composition which can be
created in case the schema which is used is not known at compile time (which will most probably be the normal case). The
container instance has the name of the DTO containing the composition followed by `OneOfContainer` and the configured
suffix (e.g. DTO).

Given the schema:

```yml
    User:
      required:
        - id
        - username
      properties:
        id:
          type: string
        username:
          type: string
    Admin:
      required:
        - id
        - adminname
      properties:
        id:
          type: string
        adminname:
          type: string

    AdminOrUser:
      oneOf:
        - $ref: '#/components/schemas/Admin'
        - $ref: '#/components/schemas/User'
```

An instance of AdminOrUserDto can be created like the following:

```java
AdminDto adminDto = AdminDto.builder()
        .setId("123")
        .setAdminname("admin")
        .build()

AdminOrUserDto dto = AdminOrUserDto.builder()
        .setAdmin(adminDto)
        .build();
```

Or with the 'container' instance `AdminOrUserOneOfContainerDto`, which offers from methods to either create an instance
from an AdminDto or an UserDto:

```
AdminOrUserOneOfContainerDto container;
if(conditionForAdmin) {
  container = AdminOrUserOneOfContainerDto.fromAdmin(adminDto);
} else {
  container = AdminOrUserOneOfContainerDto.fromUser(userDto);
}

AdminOrUserDto dto = AdminOrUserDto.builder()
  .setOneOfContainer(container)
  .build();
```

### Building `anyOf` composition

If a DTO contains a `oneOf` composition, the builder will enforce one to set at least one of the DTO's. Building a
`anyOf` composition is similar to building a `oneOf` composition, but the builder allows one to set more than one
instance of the defined DTO's. This could either be done by setting the DTO's directly or by using the also a container.

Given the schema:

```yml
    User:
      required:
        - id
        - username
      properties:
        id:
          type: string
        username:
          type: string
    Admin:
      required:
        - id
        - adminname
      properties:
        id:
          type: string
        adminname:
          type: string

    AdminOrUser:
      anyOf:
        - $ref: '#/components/schemas/Admin'
        - $ref: '#/components/schemas/User'
```

An instance of AdminOrUserDto can be created like the following:

```java
AdminDto adminDto = AdminDto.builder()
        .setId("123")
        .setAdminname("admin")
        .build();

UserDto userDto = UserDto.builder()
        .setId("123")
        .setUsername("user")
        .build();

AdminOrUserDto dto = AdminOrUserDto.builder()
        .setAdmin(adminDto)
        .setUser(userDto)
        .build();
```

Or with the 'container' instance `AdminOrUserAnyOfContainerDto`, which offers `from` methods to either create an
instance from an AdminDto or an UserDto, `with` methods to set another instance or a `merge` method to merge two
containers.

```
AdminOrUserAnyOfContainerDto container;

if(conditionForAdmin) {
  AdminOrUserAnyOfContainer adminContainer = AdminOrUserAnyOfContainerDto.fromAdmin(adminDto);
  container = container == null ? adminContainer : container.merge(adminContainer); 
} 

if(conditionForUser) {
  AdminOrUserAnyOfContainer userContainer = AdminOrUserAnyOfContainerDto.fromUser(userDto);
  container = container == null ? userContainer : container.merge(userContainer); 
}

if(container == null) {
  throw new IllegalArgumentException("No container created");
}

AdminOrUserDto dto = AdminOrUserDto.builder()
  .setAnyOfContainer(container)
  .build();
```

### Setting additional properties

If the schema allows additional properties, the builder will offer two methods to set these properties at the end. If no
specific type is defined, the two methods have to following signature:

```java
public Builder setAdditionalProperties(Map<String, Object> additionalProperties);

public Builder addAdditionalProperty(String key, Object value);
```

The methods can be called multiple times which makes sense in the case single properties are set with the
`addAdditionalProperty` method, but `setAdditionalProperties` will replace any previously set properties.

### Full builder

There exists also a 'full' builder, which enforces one to set all properties. This builder is equivalent to the standard
builder but after all required properties are set, there is no option to build the instance or set only a subset of the
optional properties. It is like calling `andOptionals()` after all required properties are set but without the need to
explicitly call it. The full builder will be used in case either one of the methods

* `fullBuilder()`
* `fullUserDtoBuilder()`

is called.
