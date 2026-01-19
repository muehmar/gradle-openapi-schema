## Change Log

* 4.0.1
    * [#372](https://github.com/muehmar/gradle-openapi-schema/issues/372) - Fix Jackson 3 deprecation warnings
* 4.0.0
    * [#366](https://github.com/muehmar/gradle-openapi-schema/issues/366) - Support Jackson 3
    * [#361](https://github.com/muehmar/gradle-openapi-schema/issues/361) - Group validation options in DSL
    * [#364](https://github.com/muehmar/gradle-openapi-schema/issues/364) - Fix TypeMapping for additional properties
    * [#340](https://github.com/muehmar/gradle-openapi-schema/issues/340) - Use Set instead of List for arrays with
      unique items
    * [#336](https://github.com/muehmar/gradle-openapi-schema/issues/336) - Remove 'partial-time' as format for
      partial-time from RFC 3339
    * [#308](https://github.com/muehmar/gradle-openapi-schema/issues/308) - Improve Map additional properties
    * [#307](https://github.com/muehmar/gradle-openapi-schema/issues/307) - Support conversions for mappings of Map
      classes as additional properties
    * [#301](https://github.com/muehmar/gradle-openapi-schema/issues/301) - Set minimum JDK version to 11
    * [#299](https://github.com/muehmar/gradle-openapi-schema/issues/299) - Remove parameters generation
    * [#292](https://github.com/muehmar/gradle-openapi-schema/issues/292) - Support for nullable map values
    * [#270](https://github.com/muehmar/gradle-openapi-schema/issues/270) - Use correct Java Type for format date-time
* 3.8.0
    * [#351](https://github.com/muehmar/gradle-openapi-schema/issues/351) - Properly support null values for enums
    * [#352](https://github.com/muehmar/gradle-openapi-schema/issues/352) - Add config option for nullable enums for
      OpenAPI spec v3.0.x
* 3.7.0
    * [#343](https://github.com/muehmar/gradle-openapi-schema/issues/343) - Fix resolving external references with
      different directories
    * [#341](https://github.com/muehmar/gradle-openapi-schema/issues/341) - Support `$ref` as root schema
    * [#339](https://github.com/muehmar/gradle-openapi-schema/issues/339) - Add convenience factory methods for single
      property objects
* 3.6.1
    * [#335](https://github.com/muehmar/gradle-openapi-schema/issues/335) - Support format 'time' for LocalTime
* 3.6.0
    * [#326](https://github.com/muehmar/gradle-openapi-schema/issues/326) - Support attributes in XML serialisation
    * [#325](https://github.com/muehmar/gradle-openapi-schema/issues/325) - Support XML property names
    * [#328](https://github.com/muehmar/gradle-openapi-schema/issues/328) - Support wrapped and name for list
      serialisation in XML
    * [#330](https://github.com/muehmar/gradle-openapi-schema/issues/330) - User original schema name for root element
      in XML as default
* 3.5.0
    * [#322](https://github.com/muehmar/gradle-openapi-schema/issues/322) - Add basic XML support
* 3.4.0
    * [#315](https://github.com/muehmar/gradle-openapi-schema/issues/315) - Emit a warning for DTO mappings without a
      conversion
    * [#312](https://github.com/muehmar/gradle-openapi-schema/issues/312) - Provide option to confirm warning for
      missing conversions
    * [#310](https://github.com/muehmar/gradle-openapi-schema/issues/310) - Prevent property name clashes for generated
      code
    * [#309](https://github.com/muehmar/gradle-openapi-schema/issues/309) - Throw an exception for unsupported
      conversion mappings for map additional properties
* 3.3.1
    * [#304](https://github.com/muehmar/gradle-openapi-schema/issues/304) - Fix parameter classes deperecation
* 3.3.0
    * [#291](https://github.com/muehmar/gradle-openapi-schema/issues/291) - Option to replace a generated DTO with a
      custom type
    * [#279](https://github.com/muehmar/gradle-openapi-schema/issues/279) - Remove old DTO's after changes
    * [#285](https://github.com/muehmar/gradle-openapi-schema/issues/285) - Option for non-strict oneOf validation
    * [#298](https://github.com/muehmar/gradle-openapi-schema/issues/298) - Deprecate parameters generation
* 3.2.0
    * [#160](https://github.com/muehmar/gradle-openapi-schema/issues/160) - Support for conversions of type and class
      mappings
    * [#278](https://github.com/muehmar/gradle-openapi-schema/issues/278) - Fix serialisation of multiple nested
      required and nullable property in anyOf or oneOf compositions
    * [#286](https://github.com/muehmar/gradle-openapi-schema/issues/286) - Fix mapping for map keys
* 3.1.9
    * [#287](https://github.com/muehmar/gradle-openapi-schema/issues/287) - Fix inlining of container types, e.g. array
      items and map values
* 3.1.6
    * [#287](https://github.com/muehmar/gradle-openapi-schema/issues/287) - Fix inlining of container types, e.g. array
      items and map values
* 3.1.5
    * [#272](https://github.com/muehmar/gradle-openapi-schema/issues/272) - Fix serialisation of required not nullable
      properties with special naming pattern
* 3.1.4
    * [#280](https://github.com/muehmar/gradle-openapi-schema/issues/280) - Fix generation of inline enum definitions
      for item types of arrays
* 3.1.3
    * [#275](https://github.com/muehmar/gradle-openapi-schema/issues/275) - Fix unresolved array items
* 3.1.2
    * [#263](https://github.com/muehmar/gradle-openapi-schema/issues/263) - Support for making referenced property
      nullable
    * [#264](https://github.com/muehmar/gradle-openapi-schema/issues/264) - Improve error message in case of unsupported
      schemas concerning compositions
* 3.1.1
    * [#259](https://github.com/muehmar/gradle-openapi-schema/issues/259) - Fix compile error for large integer
      constraints
* 3.1.0
    * [#229](https://github.com/muehmar/gradle-openapi-schema/issues/229) - Support referenced enum as discriminator
* 3.0.0
    * [#150](https://github.com/muehmar/gradle-openapi-schema/issues/150) - Support discriminator with anyOf
      compositions
    * [#187](https://github.com/muehmar/gradle-openapi-schema/issues/187) - Rename value to items in array classes
    * [#227](https://github.com/muehmar/gradle-openapi-schema/issues/227) - Improve allOf builder stages for duplicated
      properties
    * [#244](https://github.com/muehmar/gradle-openapi-schema/issues/244) - Allow settings nested allOf properties in
      staged builder
    * [#142](https://github.com/muehmar/gradle-openapi-schema/issues/142) - Validate optional and not nullable
      properties
    * [#149](https://github.com/muehmar/gradle-openapi-schema/issues/149) - Move builder stages into inner static
      classes to improve code completion in IDE's
    * [#159](https://github.com/muehmar/gradle-openapi-schema/issues/159) - Proper support for nullable keyword
    * [#248](https://github.com/muehmar/gradle-openapi-schema/issues/248) - Support nullable keyword in root type
      definitions
    * [#158](https://github.com/muehmar/gradle-openapi-schema/issues/158) - Support for nullable array items and
      additional properties
    * [#237](https://github.com/muehmar/gradle-openapi-schema/issues/237) - Rename SafeBuilder to StagedBuilder
* 2.6.1
    * [#238](https://github.com/muehmar/gradle-openapi-schema/issues/238) - Fix anyOf builder stage with only optional
      properties of full-buider
* 2.6.0
    * [#234](https://github.com/muehmar/gradle-openapi-schema/issues/234) - Support making nested allOf properties
      required
* 2.5.1
    * [#231](https://github.com/muehmar/gradle-openapi-schema/issues/231) - Fix determination of enum origin for
      discriminator
* 2.5.0
    * [#219](https://github.com/muehmar/gradle-openapi-schema/issues/219) - Support enum as discriminator
    * [#224](https://github.com/muehmar/gradle-openapi-schema/issues/224) - Improve error validation message for oneOf
      compositions with mappings
    * [#210](https://github.com/muehmar/gradle-openapi-schema/issues/210) - Improve allOf builder stages
* 2.4.1
    * [#218](https://github.com/muehmar/gradle-openapi-schema/issues/218) - Fix missing deprecated annotation for oneOf
      compositions
* 2.4.0
    * [#209](https://github.com/muehmar/gradle-openapi-schema/issues/209) - Support making nested optional properties
      required with compositions
    * [#208](https://github.com/muehmar/gradle-openapi-schema/issues/208) - Inherit implicit `type: object` for schemas
      with only required properties
    * [#205](https://github.com/muehmar/gradle-openapi-schema/issues/205) - Prevent the generation of jackson helper
      utilities when jackson is disabled
    * [#204](https://github.com/muehmar/gradle-openapi-schema/issues/204) - Support the configuration of more options
      globally
    * [#184](https://github.com/muehmar/gradle-openapi-schema/issues/184) - Support the configuration of more options
      globally
    * [#188](https://github.com/muehmar/gradle-openapi-schema/issues/188) - Add factory method for empty arrays
    * [#194](https://github.com/muehmar/gradle-openapi-schema/issues/194) - Overload setter for output directory to
      support using `project.layout.buildDirectory`
    * [#185](https://github.com/muehmar/gradle-openapi-schema/issues/185) - Fix builder for additional property with
      same name as normal property
* 2.3.1
    * [#192](https://github.com/muehmar/gradle-openapi-schema/issues/192) - Fix invalid single properties in alOf stages
      of builder
* 2.3.0
    * [#179](https://github.com/muehmar/gradle-openapi-schema/issues/179) - Support making optional properties required
      with compositions
    * [#195](https://github.com/muehmar/gradle-openapi-schema/issues/195) - Fix missing required additional properties
      in allOf builder stages
    * [#193](https://github.com/muehmar/gradle-openapi-schema/issues/193) - Fix missing required additional properties
      stages in case no normal properties are defined
    * [#191](https://github.com/muehmar/gradle-openapi-schema/issues/191) - Fix missing oneOf builder stage in case the
      first subschema contains no properties
    * [#190](https://github.com/muehmar/gradle-openapi-schema/issues/190) - Fix nested anyOf or oneOf schema definitions
    * [#182](https://github.com/muehmar/gradle-openapi-schema/issues/182) - Fix staged builder for allOf composition
      with empty subschema
* 2.2.0
    * [#139](https://github.com/muehmar/gradle-openapi-schema/issues/139) - Support full validation for compositions
    * [#153](https://github.com/muehmar/gradle-openapi-schema/issues/153) - Fix escaping for discriminator property name
    * [#151](https://github.com/muehmar/gradle-openapi-schema/issues/151) - Enhance validation message for invalid
      compositions
    * [#152](https://github.com/muehmar/gradle-openapi-schema/issues/152) - Enhance validation message for invalid
      compositions
    * [#155](https://github.com/muehmar/gradle-openapi-schema/issues/155) - Avoid runtime exception for unsupported
      validation of custom types
    * [#156](https://github.com/muehmar/gradle-openapi-schema/issues/156) - Add warnings for unsupported validation of
      custom types
    * [#174](https://github.com/muehmar/gradle-openapi-schema/issues/174) - Fix validation of constraints of array items
    * [#173](https://github.com/muehmar/gradle-openapi-schema/issues/173) - Prevent serialisation of additional
      properties when framework validation methods are public
    * [#180](https://github.com/muehmar/gradle-openapi-schema/issues/180) - Add wither-methods for allOf properties
* 2.1.1
    * [#167](https://github.com/muehmar/gradle-openapi-schema/issues/167) - Fix code generation for disabled validation
      and compositions
* 2.1.0
    * [#136](https://github.com/muehmar/gradle-openapi-schema/issues/136) - Support deviation of oneOf discriminator
      defined in a common parent schema
    * [#140](https://github.com/muehmar/gradle-openapi-schema/issues/140) - Add `toOptional` method in `Tristate` class
    * [#143](https://github.com/muehmar/gradle-openapi-schema/issues/143) - Add methods to get a component from a oneOf
      or anyOf composition
* 2.0.3
    * [#167](https://github.com/muehmar/gradle-openapi-schema/issues/167) - Fix code generation for disabled validation
      and compositions
* 2.0.2
    * [#134](https://github.com/muehmar/gradle-openapi-schema/issues/134) - Fix inlining of simple non-object schemas
* 2.0.1
    * [#130](https://github.com/muehmar/gradle-openapi-schema/issues/130) - Fix serialisation of composed DTO's
* 2.0.0
    * [#76](https://github.com/muehmar/gradle-openapi-schema/issues/76) - Support all combinations for compositions,
      properties and additionalProperties
    * [#99](https://github.com/muehmar/gradle-openapi-schema/issues/99) - Support all combinations for compositions,
      properties and additionalProperties
    * [#100](https://github.com/muehmar/gradle-openapi-schema/issues/100) - Support all combinations for compositions,
      properties and additionalProperties
    * [#123](https://github.com/muehmar/gradle-openapi-schema/issues/123) - Support mapping of schema names to adjust
      the DTO classnames
    * [#126](https://github.com/muehmar/gradle-openapi-schema/issues/126) - Fix validation of oneOf and anyOf
      composition
    * [#122](https://github.com/muehmar/gradle-openapi-schema/issues/122) - Fix correct escaping for special characters
      in property names
    * [#117](https://github.com/muehmar/gradle-openapi-schema/issues/117) - Add factory name for builder with DTO name
      for static import
    * [#111](https://github.com/muehmar/gradle-openapi-schema/issues/111) - Add 'full' builder which enforces to set
      also all optional properties
    * [#98](https://github.com/muehmar/gradle-openapi-schema/issues/98) - Quote strings in toString method
    * [#106](https://github.com/muehmar/gradle-openapi-schema/issues/106) - Validate required properties in map schemas
      and create getters for them
* 1.1.3
    * [#113](https://github.com/muehmar/gradle-openapi-schema/issues/113) - Fix format type mapping for enums
* 1.1.2
    * [#103](https://github.com/muehmar/gradle-openapi-schema/issues/103) - Fix validation of primitive data types of
      arrays and maps
* 1.1.1
    * [#101](https://github.com/muehmar/gradle-openapi-schema/issues/101) - Fix enum reference in composed pojos
* 1.1.0
    * [#60](https://github.com/muehmar/gradle-openapi-schema/issues/60) - Support OpenAPI spec version 3.1.0
    * [#57](https://github.com/muehmar/gradle-openapi-schema/issues/57) - Add JavaDoc explanation for deprecated
      validation methods
    * [#70](https://github.com/muehmar/gradle-openapi-schema/issues/70) - Fix with methods for nullable properties
    * [#64](https://github.com/muehmar/gradle-openapi-schema/issues/64) - Support validation of `multipleOf` constraint
    * [#64](https://github.com/muehmar/gradle-openapi-schema/issues/64) - Support validation of `uniqueItems` constraint
    * [#68](https://github.com/muehmar/gradle-openapi-schema/issues/68) - Support `readOnly` and `writeOnly` keywords
    * [#9](https://github.com/muehmar/gradle-openapi-schema/issues/9) - Remove unused imports in DTO's
    * [#80](https://github.com/muehmar/gradle-openapi-schema/issues/80) - Support root map schemas
    * [#84](https://github.com/muehmar/gradle-openapi-schema/issues/84) - Validate property count constraint for
      map-properties
    * [#83](https://github.com/muehmar/gradle-openapi-schema/issues/83) - Fix equals, hashCode and toString method for
      Java-array properties
    * [#88](https://github.com/muehmar/gradle-openapi-schema/issues/88) - Remove empty java-doc tags for empty
      description
    * [#91](https://github.com/muehmar/gradle-openapi-schema/issues/91) - Add toString method for freeform schemas
* 1.0.1
    * [#71](https://github.com/muehmar/gradle-openapi-schema/issues/71) - Fix issue with property name 'other'
    * [#72](https://github.com/muehmar/gradle-openapi-schema/issues/72) - Fix java keywords as property names and
      special characters for properties and classes
* 1.0.0
    * [#6](https://github.com/muehmar/gradle-openapi-schema/issues/6) - Add support for `anyOf` and `oneOf`
    * [#7](https://github.com/muehmar/gradle-openapi-schema/issues/7) - Add support for `anyOf` and `oneOf`
* 0.22.1
    * [#54](https://github.com/muehmar/gradle-openapi-schema/issues/54) - Fix DecimalMin and DecimalMax imports
* 0.22.0
    * [#41](https://github.com/muehmar/gradle-openapi-schema/issues/41) - Support Free-Form objects
    * [#44](https://github.com/muehmar/gradle-openapi-schema/issues/44) - Support `minProperties` and `maxProperties`
      constraints
    * [#48](https://github.com/muehmar/gradle-openapi-schema/issues/48) - Support Jakarta Bean Validation 3.0
* 0.21.2
    * [#38](https://github.com/muehmar/gradle-openapi-schema/issues/38) - Fix non Java-String parameters
* 0.21.1
    * [#34](https://github.com/muehmar/gradle-openapi-schema/issues/34) - Fix constraints generation for number schemas
* 0.21.0
    * [#28](https://github.com/muehmar/gradle-openapi-schema/issues/28) - Support numeric parameters
    * [#29](https://github.com/muehmar/gradle-openapi-schema/issues/29) - Support string parameters
    * [#30](https://github.com/muehmar/gradle-openapi-schema/issues/30) - Fix exclusiveMaximum and exclusiveMinimum for
      integer types
* 0.20.0
    * Proper release failed, don't use it
* 0.19.0
    * [#25](https://github.com/muehmar/gradle-openapi-schema/issues/25) - Ignore wrong format for integer or numeric
      schemas
    * [#24](https://github.com/muehmar/gradle-openapi-schema/issues/24) - Generate simple classes for parameters and
      their constraints
* 0.18.1
    * [#22](https://github.com/muehmar/gradle-openapi-schema/issues/22) - Fix failing excluded external references
* 0.18.0
    * [#18](https://github.com/muehmar/gradle-openapi-schema/issues/18) - Support remote references
    * [#19](https://github.com/muehmar/gradle-openapi-schema/issues/19) - Add possibility to exclude specific schemas
      from generation
* 0.17.0
    * [#8](https://github.com/muehmar/gradle-openapi-schema/issues/8) - Support customizable builder method prefix
    * [#12](https://github.com/muehmar/gradle-openapi-schema/issues/12) - Improve type mapping configuration
* 0.16.0
    * [#3](https://github.com/muehmar/gradle-openapi-schema/issues/3) - Support for nullability
    * [#4](https://github.com/muehmar/gradle-openapi-schema/issues/4) - Improve exception for enum conversion
* 0.15.1
    * Support inline object definitions
* 0.15.0
    * Support multiple specifications (breaking change in DSL)
* 0.14.1
    * [#1](https://github.com/muehmar/gradle-openapi-schema/issues/1) - Fix starting lowercase enum schema generation
* 0.14.0
    * Simplify the format- and class-mapping configuration
* 0.13.2
    * Support `allOf` for array items
* 0.13.1
    * Quote prefixMatcher to allow special characters
* 0.13.0
    * Add extraction of enum description
    * Fix javadoc rendering
* 0.12.0
    * Improve adding optional properties also for the standard Builder
* 0.11.0
    * Unreleased (gradle plugin portal problems)
* 0.10.0
    * Improve adding optional properties with 'Staged Builder'
* 0.9.1
    * Escape patterns for Java
* 0.9.0
    * Create top level enums for root enum definitions
    * Convert enum fields to ASCII java names
    * Fix Java-Bean validation issues
        * Do not use primitive java types to allow checking `@NotNull`
        * Use Java-Bean getter for Booleans (`get` prefix instead of `is`)
* 0.8.0
    * Add support for non-object/non-array schema definitions
    * Convert enums to uppercase snakecase
* 0.7.0
    * Add support for `allOf` combinator
* 0.6.0
    * Support Java Bean Validation
* 0.5.0
    * Add support for inline object definitions for array items
    * Add support for properties without a type
    * Improve support for maps
* 0.4.0
    * Support for inline object definitions
* 0.3.0
    * Add support for enums
    * Fix incremental build
* 0.2.1
    * Fix the setter name for booleans
* 0.2.0
    * Support incremental build
    * Add the 'Staged Builder' pattern
    * Extend the supported types/formats
    * Make the JSON support optional
* 0.1.0
    * Initial release
