## Compositions
The OpenAPI specification supports the composition of schemas via `oneOf`, `anyOf` and `allOf` keyword. This plugin supports
all three keywords.

Validation is supported for all three compositions.

### AllOf
With `allOf`, the plugin will generate a DTO with all properties of the specified schemas. Consider the following
specification:

```
components:
  schemas:
    User:
      required:
        - username
      properties:
        username:
          type: string
    Admin:
      required:
        - adminname
      properties:
        adminname:
          type: string

    AdminAndUser:
      allOf:
        - $ref: '#/components/schemas/Admin'
        - $ref: '#/components/schemas/User'
```

This will generate the three DTO's, `User`, `Admin` and `AdminAndUser`. The `AdminAndUser` will contain both
properties of the `User` and the `Admin`, i.e. the `username` property and `adminname` property. It's also possible to
retrieve an instance of `User` or `Admin` from the `AdminAndUser` DTO.

### AnyOf and OneOf
The usage of `anyOf` and `oneOf` will generate special classes used to represent this composition.

```
components:
  schemas:
    User:
      required:
        - username
      properties:
        username:
          type: string
    Admin:
      required:
        - adminname
      properties:
        adminname:
          type: string

    AdminOrUser:
      oneOf:
        - $ref: '#/components/schemas/Admin'
        - $ref: '#/components/schemas/User'

    AdminAndOrUser:
      anyOf:
        - $ref: '#/components/schemas/Admin'
        - $ref: '#/components/schemas/User'
```
The plugin will generate the following DTO's:
* `UserDto`: Simple DTO for the User schema
* `AdminDto`: Simple DTO for the Admin schema
* `AdminOrUserDto`: DTO for the `oneOf` composition
* `AdminAndOrUserDto`: DTO for the `anyOf` composition

#### Construction
The generated composition class will contain factory methods to create an instance. An instance can be created
from a DTO of the composition, i.e. from the `UserDto` or the `AdminDto`.
```
  public static AdminOrUserDto fromAdmin(AdminDto adminDto);
  
  public static AdminOrUserDto fromUser(UserDto adminDto);
```

As a `anyOf` composition can contain multiple DTO's, there exist-wither methods to add
more DTO's after instantiation:

```
  public AdminAndOrUserDto withAdmin(AdminDto adminDto);
  
  public AdminAndOrUserDto withUser(UserDto adminDto);
```


#### Decomposing `oneOf`
Two fold method exists to decompose a `oneOf` DTO:
```
  public <T> T fold(Function<AdminDto, T> onAdminDto, Function<UserDto, T> onUserDto);
  
  public <T> T fold(Function<AdminDto, T> onAdminDto, Function<UserDto, T> onUserDto, Supplier<T> onInvalid);
```

Both method accepts mapping functions for each schema, in the example case one function for the `AdminDto` and one
function for the `UserDto`. The corresponding function gets executed and its result is returnred.
I.e. if the `AdminOrUserDto` is valid against the Admin schema, the function `onAdminDto` gets executed with the
`AdminDto` as argument and the result is returned. The same applies analogously if the `AdminOrUserDto` is valid against
the User schema.

The second method has a Java-Supplier as third argument. This supplier gets called in case the DTO is not valid against
exactly one schema. The first method which has no supplier will throw an Exception in this case, this method can be used in case the
DTO is either manually or automatically validated before the decomposition.

#### Decomposing `anyOf`
There is a single fold method can be used to decompose an `anyOf` DTO:
```
  public <T> List<T> fold(Function<AdminDto, T> onAdminDto, Function<UserDto, T> onUserDto);
```
This method is similar to the fold method of the `oneOf` composition only that it returns a list as multiple mapping
functions can be called. In case the DTO is valid against no schema, it will simply return and empty list.

### Examples
#### AnyOf
* [OpenAPI spec](example/src/main/resources/openapi-anyof.yml)
* [Object creation and serialisation](example/src/test/java/com/github/muehmar/gradle/openapi/anyof/TestSerialisation.java)
* [Deserialisation and decomposition](example/src/test/java/com/github/muehmar/gradle/openapi/anyof/TestDeserialisation.java)
* [Validation](example/src/test/java/com/github/muehmar/gradle/openapi/anyof/TestValidation.java)

#### OneOf
* [OpenAPI spec](example/src/main/resources/openapi-oneof.yml)
* [Object creation and serialisation](example/src/test/java/com/github/muehmar/gradle/openapi/oneof/TestSerialisation.java)
* [Deserialisation and decomposition](example/src/test/java/com/github/muehmar/gradle/openapi/oneof/TestDeserialisation.java)
* [Validation](example/src/test/java/com/github/muehmar/gradle/openapi/oneof/TestValidation.java)
* [Object creation and serialisation with discriminator](example/src/test/java/com/github/muehmar/gradle/openapi/oneof/TestDiscriminatorSerialisation.java)
* [Deserialisation and decomposition with discriminator](example/src/test/java/com/github/muehmar/gradle/openapi/oneof/TestDiscriminatorDeserialisation.java)
