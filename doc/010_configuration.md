## Configuration

Add an `openApiGenerator` block into your `build.gradle` file:

```
openApiGenerator {
   schemas {
       apiV1 {
            inputSpec = "$projectDir/src/main/resources/openapi-v1.yml"
       }
   }
}
```

or a full example:

```
openApiGenerator {
    sourceSet = "main"
    outputDir = project.layout.buildDirectory.dir("generated/openapi")
    suffix = "Dto"
    jsonSupport = "jackson"
    enableValidation = true
    
    schemas {    
    
        // Custom name for this schema
        apiV1 {         
            inputSpec = "$projectDir/src/main/resources/openapi-v1.yml"
            packageName = "${project.group}.${project.name}.api.v1.model"
            validationApi = "jakarta-3.0"
            builderMethodPrefix = "set"
            
            warnings {
                failOnWarnings = true
            }

            // This would overwrite any global configuration
            enumDescriptionExtraction {
                enabled = true
                prefixMatcher = "`__ENUM__`:"
                failOnIncompleteDescriptions = true
            }

            // Additional format type mapping
            formatTypeMapping {
                formatType = "username"
                classType = "com.package.UserName"
                conversion.fromCustomType = "getValue"
                conversion.toCustomType = "UserName#fromString"
            }

            // Additional format type mapping
            formatTypeMapping {
                formatType = "password"
                classType = "com.package.Password"
            }

            // Additional class mapping
            classMapping {
                fromClass = "List"
                toClass = "java.util.ArrayList"
            }
            
            // Additional mapping removing 'ApiV1' from the generated classname
            constantSchemaNameMapping {
                constant = "ApiV1"
                replacement = ""
            }
            
            getterSuffixes {
                requiredSuffix = "Req"
                requiredNullableSuffix = "Opt"
                optionalSuffix = "Opt"
                optionalNullableSuffix = "Tristate"                
            }
            
            validationMethods {
                getterSuffix = "Raw"
                modifier = "public"
                deprecatedAnnotation = true
            }
        }
        
        // Custom name for this schema
        apiV2 {         
            inputSpec = "$projectDir/src/main/resources/openapi-v2.yml"
            packageName = "${project.group}.${project.name}.api.v2.model"
            
            // No specific config for enum description extraction
            // or mappings. Will inherit the global configuration
        }
    }
    
    // Global configuration for enum description extraction, 
    // used in case no specific configuration is present
    enumDescriptionExtraction {
        enabled = true
        prefixMatcher = "`__ENUM__`:"
        failOnIncompleteDescriptions = true
    }

    // Global format type mapping which gets applied to each schema
    formatTypeMapping {
        formatType = "username"
        classType = "com.package.UserName"
    }

    // Global format type mapping which gets applied to each schema
    formatTypeMapping {
        formatType = "password"
        classType = "com.package.Password"
    }

    // Global class mapping which gets applied to each schema
    classMapping {
        fromClass = "List"
        toClass = "java.util.ArrayList"
    }
    
    // Global schema name mapping which removes any '.' from the schema name for the classnames
    constantSchemaNameMapping {
        constant = "."
        replacement = ""
    }
    
    getterSuffixes {
        // global config goes here
    }
    
    validationMethods {
       // global config goes here
    }
    
    stagedBuilder {
        enabled = true
    }
}
```

Add in the `schemas` block for each specification a new block with custom name (`apiV1` and `apiV2` in the example
above) and configure the generation with the following attributes for each schema:

Some options are configurable globally, that means they can be configured on the root level (see the example above)
which applies automatically to all configured specifications. The globally configured options can be overridden for each
specification if necessary.

| Key                 | Configurable globally | Data Type                    | Default                                                | Description                                                                                                                                                                                                                                                                          |
|---------------------|:----------------------|------------------------------|--------------------------------------------------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| sourceSet           | &check;               | String                       | main                                                   | Source set to which the generated classes should be added.                                                                                                                                                                                                                           |
| inputSpec           | &cross;               | String                       |                                                        | The OpenApi 3.x specification location.                                                                                                                                                                                                                                              |
| outputDir           | &check;               | String / Provider[Directory] | project.layout.buildDirectory.dir("generated/openapi") | The location in which the generated sources should be stored. Can either be set as String or as Provider[Directory], which is the result of calling `project.layout.buildDirectory.dir("directory/inside/the/build/directory")`.                                                     |
| resolveInputSpecs   | &cross;               | boolean                      | true                                                   | Input specifications are resolved for task input calculation for gradle. This requires parsing the specification to identify remote specifications. This can be disabled if needed, see [Incremental build and remote specifications](#incremental-build-and-remote-specifications). |
| packageName         | &cross;               | String                       | ${project.group}.${project.name}.api.model             | Name of the package for the generated classes.                                                                                                                                                                                                                                       |
| suffix              | &check;               | String                       |                                                        | Suffix which gets appended to each generated class. The classes are unchanged if no suffix is provided.                                                                                                                                                                              |
| jsonSupport         | &check;               | String                       | jackson                                                | Used json support library. Possible values are `jackson` or `none`.                                                                                                                                                                                                                  |
| enableValidation    | &check;               | Boolean                      | false                                                  | Enables the generation of annotations for bean validation. Select with `validationApi` the used packages.                                                                                                                                                                            |
| validationApi       | &check;               | String                       | jakarta-2                                              | Defines the used annotations (either from `javax.*` or `jakarta.*` package). Possible values are `jakarta-2` and `jakarta-3`. Use for Java Bean validation 2.0 or Jakarta Bean validation `jakarata-2` and for Jakarta Bean validation 3.0 `jakarta-3`.                              |
| builderMethodPrefix | &check;               | String                       |                                                        | Prefix for the setter method-name of builders. The default empty string leads to setter method-names equally to the corresponding fieldname.                                                                                                                                         |
| excludeSchemas      | &cross;               | List[String]                 | []                                                     | Excludes the given schemas from generation. This can be used in case unsupported features are used, e.g. URL-references or unsupported compositions.                                                                                                                                 |

The plugin creates for each schema a task named `generate{NAME}Model` where `{NAME}` is replaced by the used name for
the schema, in the example above a task `generateApiV1Model` and a task `generateApiV2Model` would get created. The
tasks are automatically registered as dependency of the corresponding java-compile task.

### Staged Builder

The plugin generates a staged builder for each DTO, see [Staged Builder](doc/060_staged_builder.md) for more
information. The staged builder can be configured globally and / or for each schema separately.

Currently, the only option is to enable or disable the staged builder while the staged builder is enabled by default.

```
stagedBuilder {
    enabled = true
}
```

### Class Mappings

The plugin allows one to map specific standard java classes, used in the DTO to custom types. The mapping is not applied
to generated DTO classes itself, this only includes the java class used for properties in the DTO. The following example
would use the custom List implementation `com.package.CustomList`
for lists instead of
`java.util.List`. The config-property
`toClass` should be the fully qualified classname to properly generate import-statements. The
`conversion.fromCustomType` and
`conversion.toCustomType` are used in the DTO to convert from and to the custom type,
see [Conversion for Mappings](#conversions-for-mappings).

```
classMapping {
    fromClass = "List"
    toClass = "com.package.CustomList"
    conversion.fromCustomType = "asList"
    conversion.toCustomType = "CustomList#fromList"
}

```

Repeat this block for each class mapping.

### Format Type Mappings

The plugin also allows using custom classes for specific properties in the OpenApi specification. The properties must be
of type `string` and the format is a custom name which can be referenced in the plugin configuration to use the custom
class. For example the spec

```
  properties:
    userName:
      type: string
      format: username
```

and a formatTypeMapping block in the configuration

```
formatTypeMapping {
    formatType = "username"
    classType = "com.package.UserName"
    conversion.fromCustomType = "getValue"
    conversion.toCustomType = "UserName#fromString"
}
```

will use the class `com.package.UserName` for the property `userName`. The config-property `classType` should be the
fully qualified classname to properly generate import-statements. The `conversion.fromCustomType` and
`conversion.toCustomType` are used in the DTO to convert from and to the custom type,
see [Conversion for Mappings](#conversions-for-mappings).

Repeat this block for each format type mapping.

### Conversions for mappings

The conversion can be defined in one of the following ways:

| Type            | Description                                                                                                                                                                                                                                                                                          | Example                                                                                                                     |
|-----------------|:-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------------------------------|
| instance method | If a class provides a method to convert to the other type which can be called directly.                                                                                                                                                                                                              | `conversion.fromCustomType = "getValue"`                                                                                    |
| static method   | The static factory method can either be a static method of the custom type itself or a static method of any other factory class. A fully qualified classname is necessary for arbitrary factory class to generate proper import statements.<br/> The method name an the class is separated by a `#`. | `conversion.toCustomType = "UserName#fromString"`<br/> `conversion.toCustomType = "com.package.UserNameFactory#fromString"` |

Format type mappings and class mappings could also be used without conversion. In this case, the custom types are
directly used in the DTO with the following consequences:

* The custom type is serialized by Jackson, therefore one needs to configure Jackson to properly serialize and
  deserialize the custom type.
* Automatic validation will most likely not be possible, as the standard validation frameworks like hibernate can only
  validate the standard java types. The plugin does not generate any validation annotations for custom types without
  mapping.

The plugin provides warnings for defined mappings without conversion to be able to ensure one does not encounter one of
the mentioned issues with mappings without conversions.

### Schema Name Mappings

The schema name defines the generated classname of the DTO's. Constant mappings can be configured to adjust the
generated classname. For example a dot in the schema name is no legal Java identifier and is therefore escaped with an
underscore. If this is not desired, a constant mapping can be configured to remove the underscore (or any other
character or string):

```
// Removes the points from the schema for generating classnames
constantSchemaNameMapping {
    constant = "."
    replacement = ""
}
```

Multiple configured constant mappings are applied in the order they are configured.

### Enum description extraction

Enables and configures the extraction of a description for enums from the openapi specification. The
`enumDescriptionExtraction` block is optional.

```
enumDescriptionExtraction {
    enabled = true
    prefixMatcher = "`__ENUM__`:"
    failOnIncompleteDescriptions = true
}
```

| Key                          | Data Type | Default | Description                                                                                                                    |
|------------------------------|-----------|:--------|:-------------------------------------------------------------------------------------------------------------------------------|
| enabled                      | Boolean   | false   | Enables the extraction of descriptions for enum from the openapi specification.                                                |
| prefixMatcher                | String    |         | The prefix which matches the start of the description for the enums.                                                           |
| failOnIncompleteDescriptions | Boolean   | false   | Either no description or a description for each members of an enum must be present if set, otherwise the generation will fail. |

### Getter suffixes

This generator differentiates between 4 different properties (see chapter [Nullability](#Nullability)):

* Required
* Required and nullable
* Optional
* Optional and nullable

It is possible to customize the suffixes of these getters:

```
getterSuffixes {
    requiredSuffix = ""
    requiredNullableSuffix = "Opt"
    optionalSuffix = "Opt"
    optionalNullableSuffix = "Tristate"                
}
```

| Key                    | Data Type | Default  | Description                                                             |
|------------------------|-----------|:---------|:------------------------------------------------------------------------|
| requiredSuffix         | String    |          | Suffix added to the getter methods for required properties              |
| requiredNullableSuffix | String    | Opt      | Suffix added to the getter methods for required and nullable properties |
| optionalSuffix         | String    | Opt      | Suffix added to the getter methods for optional properties              |
| optionalNullableSuffix | String    | Tristate | Suffix added to the getter methods for optional and nullable properties |

### Validation Methods

This generator creates classes where `null` is not used, either not as return value or as argument. Nullable or optional
properties are wrapped for example with `java.util.Optional`. Frameworks for serialisation or validation require to
operate with nullable objects. The current supported framework for serialisation (Jackson) is able to work with private
methods which are generated by the plugin. The reference implementation for bean validation (hibernate) is also able to
work with private methods, but other frameworks like Spring (although may using hibernate) require to have public
methods for validation.

Therefore, the generator allows to customize the generation of validation methods. It allows to change the access
modifier of validations methods. Additionally, a deprecated annotation can be added to each validation method, to point
out that these methods should not be used in the code manually by the programmer but automatically by frameworks. For
getters of properties used for validation, a suffix can be configured to avoid the clash with the standard methods which
return wrapped objects instead of nullable objects.

The following is an example to configure the generator to generate public validation methods and marked as deprecated
which can be used together with the validation in Spring.

```
validationMethods {
    modifier = "public"
    deprecatedAnnotation = true
}
```

| Key                  | Data Type | Default | Description                                                                                          |
|----------------------|-----------|:--------|:-----------------------------------------------------------------------------------------------------|
| getterSuffix         | String    | Raw     | Suffix which is added to properties of getters which are only used for validation                    |
| modifier             | String    | private | Modifier for validation methods. Can be one of `public`, `protected`, `package-private` or `private` |
| deprecatedAnnotation | boolean   | false   | Determines if the validation methods should be annotated with deprecated.                            |

See the Spring-Example ([build.gradle](spring-example/build.gradle)) which makes use of this configuration.

### Warnings

[Warnings](#warnings) can be configured within a `warnings` block:

```
warnings {
    disableWarnings = false
    failOnWarnings = true
    failOnUnsupportedValidation = true
}
```

| Key                         | Data Type | Default                   | Description                                                                                                      |
|-----------------------------|-----------|:--------------------------|:-----------------------------------------------------------------------------------------------------------------|
| disableWarnings             | boolean   | false                     | Disables the generation of the warnings, i.e. emits no warnings in the gradle output                             |
| failOnWarnings              | boolean   | false                     | Global setting to fail on warnings. Will be used as default for every warning type if not configured explicitly. |
| failOnUnsupportedValidation | boolean   | value of `failOnWarnings` | Fail on unsupported validations. Uses `failOnWarnings` if omitted.                                               |
