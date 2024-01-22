## Change Log
* 2.6.1 - Fix anyOf builder stage with only optional properties of full-buider (issue `#238`)
* 2.6.0 - Support making nested allOf properties required (issue `#234`)
* 2.5.1 - Fix determination of enum origin for discriminator (issue `#231`)
* 2.5.0
    * Support enum as discriminator (issue `#219`)
    * Improve error validation message for oneOf compositions with mappings (issue `#224`)
    * Improve allOf builder stages (issue `#210`)
* 2.4.1 - Fix missing deprecated annotation for oneOf compositions (issue `#218`)
* 2.4.0
    * Support making nested optional properties required with compositions (issue `#209`)
    * Inherit implicit `type: object` for schemas with only required properties (issue `#208`)
    * Prevent the generation of jackson helper utilities when jackson is disabled (issue `#205`)
    * Support the configuration of more options globally (issue `#204` and `#184`)
    * Add factory method for empty arrays (issue `#188`)
    * Overload setter for output directory to support using `project.layout.buildDirectory` (issue `#194`)
    * Fix builder for additional property with same name as normal property (issue `#185`)
* 2.3.1 - Fix invalid single properties in alOf stages of builder (issue `#192`)
* 2.3.0
    * Support making optional properties required with compositions (issue `#179`)
    * Fix missing required additional properties in allOf builder stages (issue `#195`)
    * Fix missing required additional properties stages in case no normal properties are defined (issue `#193`)
    * Fix missing oneOf builder stage in case the first subschema contains no properties (issue `#191`)
    * Fix nested anyOf or oneOf schema definitions (issue `#190`)
    * Fix staged builder for allOf composition with empty subschema (issue `#182`)
* 2.2.0
    * Support full validation for compositions (issue `#139`)
    * Fix escaping for discriminator property name (issue `#153`)
    * Enhance validation message for invalid compositions (issue `#151` and `#152`)
    * Avoid runtime exception for unsupported validation of custom types (issue `#155`)
    * Add warnings for unsupported validation of custom types (issue `#156`)
    * Fix validation of constraints of array items (issue `#174`)
    * Prevent serialisation of additional properties when framework validation methods are public (issue `#173`)
    * Add wither-methods for allOf properties (issue `#180`)
* 2.1.1 - Fix code generation for disabled validation and compositions (issue `#167`)
* 2.1.0
    * Support deviation of oneOf discriminator defined in a common parent schema (issue `#136`)
    * Add `toOptional` method in `Tristate` class (issue `#140`)
    * Add methods to get a component from a oneOf or anyOf composition  (issue `#143`)
* 2.0.3 - Fix code generation for disabled validation and compositions (issue `#167`)
* 2.0.2 - Fix inlining of simple non-object schemas (issue `#134`)
* 2.0.1 - Fix serialisation of composed DTO's (issue `#130`)
* 2.0.0
    * Support all combinations for compositions, properties and additionalProperties (issues `#76`, `#99`, `#100`)
    * Support mapping of schema names to adjust the DTO classnames (issue `#123`)
    * Fix validation of oneOf and anyOf composition (issue `#126`)
    * Fix correct escaping for special characters in property names (issue `#122`)
    * Add factory name for builder with DTO name for static import (issue `#117`)
    * Add 'full' builder which enforces to set also all optional properties (issue `#111`)
    * Quote strings in toString method (issue `#98`)
    * Validate required properties in map schemas and create getters for them (issue `#106`)
* 1.1.3 - Fix format type mapping for enums (issue `#113`)
* 1.1.2 - Fix validation of primitive data types of arrays and maps (issue `#103`)
* 1.1.1 - Fix enum reference in composed pojos (issue `#101`)
* 1.1.0
    * Support OpenAPI spec version 3.1.0 (issue `#60`)
    * Add JavaDoc explanation for deprecated validation methods (issue `#57`)
    * Fix with methods for nullable properties (issue `#70`)
    * Support validation of `multipleOf` constraint (issue `#64`)
    * Support validation of `uniqueItems` constraint (issue `#64`)
    * Support `readOnly` and `writeOnly` keywords (issue `#68`)
    * Remove unused imports in DTO's (issue `#9`)
    * Support root map schemas (issue `#80`)
    * Validate property count constraint for map-properties (issue `#84`)
    * Fix equals, hashCode and toString method for Java-array properties (issue `#83`)
    * Remove empty java-doc tags for empty description (issue `#88`)
    * Add toString method for freeform schemas (issue `#91`)
* 1.0.1
    * Fix issue with property name 'other' (issue `#71`)
    * Fix java keywords as property names and special characters for properties and classes (issue `#72`)
* 1.0.0 - Add support for `anyOf` and `oneOf` (issues `#6` and `#7`)
* 0.22.1 - Fix DecimalMin and DecimalMax imports (issue `#54`)
* 0.22.0
    * Support Free-Form objects (issue `#41`)
    * Support `minProperties` and `maxProperties` constraints (issue `#44`)
    * Support Jakarta Bean Validation 3.0 (issue `#48`)
* 0.21.2 - Fix non Java-String parameters (issue `#38`)
* 0.21.1 - Fix constraints generation for number schemas (issue `#34`)
* 0.21.0
    * Support numeric parameters (issue `#28`)
    * Support string parameters (issue `#29`)
    * Fix exclusiveMaximum and exclusiveMinimum for integer types (issue `#30`)
* 0.20.0
    * Proper release failed, don't use it
* 0.19.0
    * Ignore wrong format for integer or numeric schemas (issue `#25`)
    * Generate simple classes for parameters and their constraints (issue `#24`)
* 0.18.1 - Fix failing excluded external references (issue `#22`)
* 0.18.0
    * Support remote references (issue `#18`)
    * Add possibility to exclude specific schemas from generation (issue `#19`)
* 0.17.0
    * Support customizable builder method prefix (issue `#8`)
    * Improve type mapping configuration (issue `#12`)
* 0.16.0
    * Support for nullability (issue `#3`)
    * Improve exception for enum conversion (issue `#4`)
* 0.15.1 - Support inline object definitions
* 0.15.0 - Support multiple specifications (breaking change in DSL)
* 0.14.1 - Fix issue `#1`
* 0.14.0 - Simplify the format- and class-mapping configuration
* 0.13.2 - Support `allOf` for array items
* 0.13.1 - Quote prefixMatcher to allow special characters
* 0.13.0
    * Add extraction of enum description
    * Fix javadoc rendering
* 0.12.0 - Improve adding optional properties also for the standard Builder
* 0.11.0 - Unreleased (gradle plugin portal problems)
* 0.10.0 - Improve adding optional properties with 'Safe Builder'
* 0.9.1 - Escape patterns for Java
* 0.9.0
    * Create top level enums for root enum definitions
    * Convert enum fields to ASCII java names
    * Fix Java-Bean validation issues
        * Do not use primitive java types to allow checking `@NotNull`
        * Use Java-Bean getter for Booleans (`get` prefix instead of `is`)
* 0.8.0
    * Add support for non-object/non-array schema definitions
    * Convert enums to uppercase snakecase
* 0.7.0 - Add support for `allOf` combinator
* 0.6.0 - Support Java Bean Validation
* 0.5.0
    * Add support for inline object definitions for array items
    * Add support for properties without a type
    * Improve support for maps
* 0.4.0 - Support for inline object definitions
* 0.3.0
    * Add support for enums
    * Fix incremental build
* 0.2.1 - Fix the setter name for booleans
* 0.2.0
    * Support incremental build
    * Add the 'Safe Builder' pattern
    * Extend the supported types/formats
    * Make the JSON support optional
* 0.1.0 - Initial release
