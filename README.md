[![Build Status](https://github.com/muehmar/gradle-openapi-schema/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/muehmar/gradle-openapi-schema/actions/workflows/gradle.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/muehmar/gradle-openapi-schema/blob/master/LICENSE)

# Gradle OpenApi Schema Codegen

This is a gradle plugin to generate Java code given an openapi 3.x specification. Unlike other codegen tools, this
focuses only on the `#/component/schema` section. It generates immutable classes and special builder classes to support
a safe way creating instances. The data classes support JSON conversions via jackson.

* Immutable Java classes.
* Special builder pattern for safe creation of instances.
* JSON support with jackson.
* Customization of the code generation

The implementation is based on the
[swagger-parser](https://github.com/swagger-api/swagger-parser)
project.

## Usage

Add the plugin section in your `build.gradle`:

```
plugins {
    id 'com.github.muehmar.openapischema' version '0.2.1'
}
```

## Configuration

Add a `generateApiSchemas` block into your `build.gradle` file.

```
generateApiSchemas {
    sourceSet = 'main'
    inputSpec = "$projectDir/src/main/resources/public/openapi/openapi.yml"
    outputDir = "$buildDir/generated/openapi"
    packageName = "${project.group}.${project.name}.api.model"
    suffix = "Dto"
    jsonSupport = "jackson"
    
    classMappings {
        customList {
            fromClass = "List"
            toClass = "CustomList"
            imports = "com.package.CustomList"
        }
    }
    
    formatTypeMappings {
        name {
            formatType = "username"
            classType = "UserName"
            imports = "com.package.UserName"
        }
        pass {
            formatType = "password"
            classType = "Password"
            imports = "com.package.Password"
        }
    }
}

compileJava.dependsOn tasks.generateApiSchemas
```

| Key | Data Type | Default | Description | 
| --- | --- | --- | --- | 
| sourceSet | String | main | Source set to which the generated classes should be added. |
| inputSpec | String | None | The OpenApi 3.x specification location. |
| outputDir | String | $buildDir/generated/openapi | The location in which the generated sources should be stored. |
| packageName | String | ${project.group}.${project.name}.api.model | Name of the package for the generated classes. |
| suffix | String | None | Suffix which gets appended to each generated class. The classes are unchanged if no suffix is provided. |
| jsonSupport | String | jackson | Used json support library. Possible values are `jackson` or `none`. |
| enableSafeBuilder | Boolean | true | Enables creating the safe builder. |

The plugin creates a task named `generateApiSchemas`. The dependency of the compile task of the corresponding source
must be set manually like in the example above.

### Class Mappings

The plugin allows one to map specific classes to custom types. The following example would use the custom List
implementation `com.package.CustomList` for lists instead of `java.util.List`.

```
classMappings {
    customList {
        fromClass = "List"
        toClass = "CustomList"
        imports = "com.package.CustomList"
    }
}

```

### Format Type Mappings

The plugin also allows using custom classes for specific properties in the OpenApi specification. The properties must be
of type `string` and the format is a custom name which can be referenced in the plugin configuration to use the custom
class. For example the spec

```
  properties:
    accountName:
      type: string
      format: username
```

and a formatTypeMapping block in the configuration

```
formatTypeMappings {
    name {
        formatType = "username"
        classType = "UserName"
        imports = "com.package.UserName"
    }
}
```

will use the class `com.package.UserName` for the property `accountName`.

## Safe Builder

The 'Safe Builder' is an extended builder pattern which enforces one to create valid instances, i.e. every required
property in a class will be set.

This is done by creating a single builder class for each required property, with a single method setting the
corresponding property and returning the next builder for the next property. The `build`
method will only be present after each required property is set.

For example, given the schema:

```
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

```
  User.newBuilder()
    .setName("Dexter")
    .setCity("Miami")
    .andAllOptionals()
    .setAge(39)
    .build();
```

This does not seem to be very different from the normal builder pattern at a first glance but calling `newBuilder()`
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

## Change Log

* 0.2.1 Fix the setter name for booleans
* 0.2.0
    * Support incremental build
    * Add the 'Safe Builder' pattern
    * Extend the supported types/formats
    * Make the JSON support optional
* 0.1.0 - Initial release
