## Compositions
The OpenAPI specification supports the composition of schemas via `oneOf`, `anyOf` and `allOf` keyword. The plugin
supports the generation of DTO's for these compositions as well as automatic validation.

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
* `AdminOrUserOneOfContainerDto`: Container for the `oneOf` composition used in the builder of the actual DTO
* `AdminAndOrUserDto`: DTO for the `anyOf` composition
* `AdminAndOrUserAnyOfContainerDto`: Container for the `anyOf` composition used in the builder of the actual DTO

#### Discriminator and anyOf
This plugin supports anyOf compositions with discriminator. The assumption is that anyOf with discriminator is 
semantically equivalent to oneOf but with the possibility to be valid against multiple schemas at the same time.

Therefore, the generated code concerning the API of the DTO's of an anyOf with discriminator is mostly the same as for 
oneOf compositions but the validation will not fail in case the DTO is valid against multiple schemas (which would be
the case for a oneOf composition).

#### Supported discriminator types
The discriminator must either be a plain string without any format
```
properties
  type:
    type: string
```

or an enum, which is defined in a parent schema (inline definitions are not supported), see 
[openapi enum discriminator](../example/src/main/resources/openapi-oneof-enum-discriminator.yml).

#### Construction
The generated container can be used to create an instance of the DTO. The container class contains factory methods to
instantiate a container from one of the DTO's, i.e. from the `UserDto` or the `AdminDto` in the example above:
```
  // Factory methods in the class AdminOrUserOneOfContainerDto
  public static AdminOrUserOneOfContainerDto fromAdmin(AdminDto adminDto);
  
  public static AdminOrUserOneOfContainerDto fromUser(UserDto adminDto);
  
  // Factory methods in the class AdminAndOrUserAnyOfContainerDto
  public static AdminAndOrUserAnyOfContainerDto fromAdmin(AdminDto adminDto);
  
  public static AdminAndOrUserAnyOfContainerDto fromUser(UserDto adminDto);
```

As a `anyOf` composition can contain multiple DTO's, there exist-wither methods to add
more DTO's after instantiation, as well as a merge method to merge two containers if necessary.

```
  public AdminAndOrUserAnyOfContainerDto withAdmin(AdminDto adminDto);
  
  public AdminAndOrUserAnyOfContainerDto withUser(UserDto adminDto);
  
  public AdminAndOrUserAnyOfContainerDto merge(AdminAndOrUserAnyOfContainerDto other);
```

This can be used in the staged builder to create an instance of the DTO:
```
  private AdminOrUserDto createDto(UserDefinedInput input) {
    return AdminOrUserDto.fullAdminOrUserBuilder()
      .setAnyOfContainer(createComposition(input))
      .build();
  }

  // Create the container depending on the input
  private AdminAndOrUserAnyOfContainerDto createComposition(UserDefinedInput input) {
    if(shouldBeAdmin(input)) {
      final AdminDto adminDto = createAdminDto(input);
      return AdminAndOrUserAnyOfContainerDto.fromAdmin(adminDto);
    } else {
      final UserDto userDto = createUserDto(input);
      return AdminAndOrUserAnyOfContainerDto.fromUser(userDto);
    }
  }
  
  // Create the container from the input in a more functional way the class supports it
  private AdminAndOrUserAnyOfContainerDto createComposition(UserDefinedInput input) {
    return input.fold(
      adminInput -> {
        final AdminDto adminDto = createAdminDto(adminInput);
        return AdminAndOrUserAnyOfContainerDto.fromAdmin(adminDto);
      },
      userInput -> {
        final UserDto userDto = createUserDto(userInput);
        return AdminAndOrUserAnyOfContainerDto.fromUser(userDto);
      }
    );
  }
```

#### Decomposing `oneOf` or `anyOf` with discriminator
Two fold-methods exists to decompose a `oneOf` DTO or an `anyof` DTO with discriminator:
```
  public <T> T fold(Function<AdminDto, T> onAdminDto, Function<UserDto, T> onUserDto);
  
  public <T> T fold(Function<AdminDto, T> onAdminDto, Function<UserDto, T> onUserDto, Supplier<T> onInvalid);
```

Both method accepts mapping functions for each schema, in the example case one function for the `AdminDto` and one
function for the `UserDto`. The corresponding function gets executed and its result is returned.
I.e. if the `AdminOrUserDto` is valid against the Admin schema (or the discriminator points to this schema), the 
function `onAdminDto` gets executed with the `AdminDto` as argument and the result is returned. The same applies 
analogously if the `AdminOrUserDto` is valid against the User schema (or the discriminator points to it).

The second method has a Java-Supplier as third argument. This supplier gets called in case the DTO the composition is
valid (i.e. the DTO is not against exactly one schema in oneOf compositions). This supplier won't be called in case the
DTO is manually or automatically validated by a framework. The first method which has no supplier will throw an 
Exception in this case, this method can be used in case the DTO is either manually or automatically validated before 
the decomposition.

#### Decomposing `anyOf` without discriminator
There is a single fold method can be used to decompose an `anyOf` DTO:
```
  public <T> List<T> fold(Function<AdminDto, T> onAdminDto, Function<UserDto, T> onUserDto);
```
This method is similar to the fold method of the `oneOf` composition or `anyOf` composition with discriminator only 
that it returns a list as multiple mapping functions can be called. In case the DTO is valid against no schema, it 
will simply return an empty list.

### Examples
#### AnyOf
* [OpenAPI spec](../example/src/main/resources/openapi-anyof.yml)
* [Object creation and serialisation](../example/src/test/java/com/github/muehmar/gradle/openapi/anyof/SerialisationTest.java)
* [Deserialisation and decomposition](../example/src/test/java/com/github/muehmar/gradle/openapi/anyof/DeserialisationTest.java)
* [Validation](../example/src/test/java/com/github/muehmar/gradle/openapi/anyof/ValidationTest.java)
* [Object creation and serialisation with discriminator](../example/src/test/java/com/github/muehmar/gradle/openapi/anyof/DiscriminatorSerialisationTest.java)
* [Deserialisation and decomposition with discriminator](../example/src/test/java/com/github/muehmar/gradle/openapi/anyof/DiscriminatorDeserialisationTest.java)

#### OneOf
* [OpenAPI spec](../example/src/main/resources/openapi-oneof.yml)
* [Object creation and serialisation](../example/src/test/java/com/github/muehmar/gradle/openapi/oneof/SerialisationTest.java)
* [Deserialisation and decomposition](../example/src/test/java/com/github/muehmar/gradle/openapi/oneof/DeserialisationTest.java)
* [Validation](../example/src/test/java/com/github/muehmar/gradle/openapi/oneof/ValidationTest.java)
* [Object creation and serialisation with discriminator](../example/src/test/java/com/github/muehmar/gradle/openapi/oneof/DiscriminatorSerialisationTest.java)
* [Deserialisation and decomposition with discriminator](../example/src/test/java/com/github/muehmar/gradle/openapi/oneof/DiscriminatorDeserialisationTest.java)
