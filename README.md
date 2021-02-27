# Gradle OpenApi Schema Codegen

This is a gradle plugin to generate Java code given an openapi 3.x specification. Unlike other codegen tools, this
focuses only on the `#/component/schema` section. It generates immutable classes and special builder classes to support
a safe way creating instances. The data classes supports JSON conversions via jackson.

* Immutable Java classes.
* Special builder pattern for safe creation of instances.
* JSON support with jackson.
* Customization of the code generation

The implementation is based on the
[swagger-parser](https://github.com/swagger-api/swagger-parser)
project.

## Usage

Add the plugin the plugin section in your `build.gradle`:

```
plugins {
    id 'com.github.muehmar.openapischema' version '0.1.0'
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
```

| Key | Data Type | Default | Description | 
| --- | --- | --- | --- | 
| sourceSet | String | main | Source set to which the generated classes should be added. |
| inputSpec | String | None | The OpenApi 3.x specification location. |
| outputDir | String | $buildDir/generated/openapi | The location in which the generated sources should be stored. |
| packageName | String | ${project.group}.${project.name}.api.model | Name of the package for the generated classes. |
| suffix | String | None | Suffix which gets appended to each generated class. The classes are unchanged if no suffix is provided. |
| jsonSupport | String | jackson | Used json support library. Possible values are `jackson` or `none`. |

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

The plugin allows also use custom classes for specific properties in the OpenApi specification. The properties must be
of type `string` and the format is a custom name which can be referenced in the plugin to use the custom class. For
example the spec

```
  properties:
    accountName:
      type: string
      format: username
```

and

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

## Change Log

* 0.1.0 - Initial release
