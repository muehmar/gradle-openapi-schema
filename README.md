[![Build Status](https://github.com/muehmar/gradle-openapi-schema/actions/workflows/gradle.yml/badge.svg?branch=master)](https://github.com/muehmar/gradle-openapi-schema/actions/workflows/gradle.yml)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://github.com/muehmar/gradle-openapi-schema/blob/master/LICENSE)

# Gradle OpenApi Schema Codegen

This is a gradle plugin to generate Java code given an openapi 3.0.x or 3.1.0 specification. Unlike other codegen tools,
this focuses mainly on the `#/component/schema` section. It generates immutable classes and a staged builder to support
a safe way creating instances. The data classes support JSON conversions via jackson.

This plugin has three main advantages over using the standard OpenAPI Generator for schemas:

* Extended support of schema specifications: The generator can create classes for almost every possible schema
  definition
* Extended validation: The generated code can be validated automatically against every constraint one can define in the
  specification
* Improved compile-time safety: The generated classes do reflect more property attributes to improve the compile-time
  safety

The main features are:

* Immutable Java classes
* Staged builder pattern for compile-time-safe creation of instances
* JSON deserializing and serializing support with jackson
* Basic XML deserializing and serializing support with jackson
* Customization of the code generation
* Support for Java Bean Validation 2.x and Jakarta Bean Validation 2.x / 3.x
* Additional validation of object level constraints
* Extraction of description for enums
* Supports processing multiple specifications
* Support compositions (`allOf`, `anyOf`, `oneOf`)
* Customization of DTO classnames
* Easy integration with the official OpenAPI Generator

The implementation is based on the
[swagger-parser](https://github.com/swagger-api/swagger-parser)
project.

## Usage

Add the plugin section in your `build.gradle`:

```groovy
plugins {
    id 'com.github.muehmar.openapischema' version '3.6.1'
}
```

and configure the generation:

```groovy
openApiGenerator {
    schemas {
        apiV1 {
            inputSpec = "$projectDir/src/main/resources/openapi-v1.yml"
        }
    }
}
```

## Documentation

1. [Configuration](doc/010_configuration.md)
2. [Supported OpenAPI versions](doc/020_openapi_support.md)
3. [Warnings](doc/030_warnings.md)
4. [Compositions](doc/040_compositions.md)
5. [Nullability](doc/050_nullability.md)
6. [Staged Builder](doc/060_staged_builder.md)
7. [Validation](doc/070_validation.md)
8. [Extraction of enum-description](doc/080_extraction_of_enum_description.md)
9. [Parameters](doc/090_parameters.md)
10. [XML Support](doc/091_xml_support.md)
10. [Integration with OpenAPI Generator](doc/095_official_openapi_generator_integration.md)
11. [Incremental Build](doc/100_incremental_build.md)
12. [Limitations](doc/110_limitations.md)
13. [Migration Guides](doc/120_migration_guides.md)
14. [Change Log](doc/130_change_log.md)

## Credits

* @eikek for the famous `PList`
