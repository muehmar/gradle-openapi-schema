# Code review findings — master (v4.0.1)

Review date: 2026-07-13, master @ `6a00f437`. Scope: DTO generation bugs and documentation
claims on released master (the example projects consume plugin 4.0.1 from the plugin portal,
so all runtime demonstrations below run against the released version).

Cross-checked against all open GitHub issues (#342, #311, #266, #265, #258, #228, #223,
#133, #132, #61) and recent closed ones — none of the findings below is already filed.
Also cross-checked against `code-review-findings-266.md` on branch
`266-improve-deserialisation-of-enums`: of its "already on master" section, bugs 7–9 are
confirmed on master (filed below as issues K and L), bug 10 (discriminator value vs. mapped
enum member) could NOT be reproduced on master — both discriminator shapes generate
compiling code because the oneOf container consistently uses the internal enum type; that
defect is introduced by the branch only. Branch findings 1–6 are branch-only as documented.

## Demonstrating tests

All findings are filed as GitHub issues #374–#397 (2026-07-13); the letters below map as
A=#374, B=#375, C=#376, D=#377, E=#378, F=#379, G=#380, H=#381, I=#384, J=#383, K=#385,
L=#386, M=#387, N=#382, O=#388, P=#389, Q=#390, R=#391, S=#392, T=#393, U=#394, V=#395,
W=#396, X=#397.

Red tests asserting the DESIRED behavior were added to the example project under
`example/src/test/java/com/github/muehmar/gradle/openapi/issues/` with packages/specs
matching the real issue numbers. One additional red test lives in
`plugin/src/test/java/com/github/muehmar/gradle/openapi/dsl/OpenApiSchemaExtensionTest.java`
(finding I). Findings whose buggy output does not even compile cannot have example tests
(the example build would break); their reproductions are described inline.

Verified red (all fail on 4.0.1 for exactly the documented reason):

| # | Test | Result today |
|---|---|---|
| A | `Issue374Test.validate_when_optionalUniqueItemsArrayAbsent_then_noViolations` | `ValidationException` (NPE) |
| B | `Issue375Test.validate_when_nestedPetViolatesMinLength_then_violation` (+ integer variant) | 0 violations |
| C | `Issue376Test.validate_when_intValueBelowFractionalMinimum_then_violation` | 0 violations |
| D | `Issue377Test.validate_when_binaryDataLongerThanMaxLength_then_violation` | 0 violations |
| E | `Issue378Test.getDtos_when_jsonValidAgainstBothAnyOfSchemas_then_bothDtosPresent` | `getUserDto()` empty |
| F | `Issue379Test.foldOneOf_when_jsonValidAgainstBothOneOfSchemas_then_onInvalidCalled` | folds to first match |
| G | `Issue380Test.validate/foldOneOf_when_discriminatorValueIsFirstMappedAlias_*` | violation + `onInvalid` |
| H | `Issue381Test.getAmount_when_decimalFormatMappedToCustomType_then_returnsCustomDecimal` | returns `Float` |
| J | `Issue383Test.getRefKind_when_enumFormatMappedToCustomType_then_returnsCustomKind` | returns `KindDto` |
| I | `OpenApiSchemaExtensionTest.getSchemaExtensions_when_commonDtoMapping_then_propagatesToSchemas` | 0 mappings |

---

## [#374] Issue A — Bean validation throws ValidationException (NPE) for absent arrays with uniqueItems

**Suggested title:** `@AssertTrue` uniqueItems method NPEs when the array property is null/absent

**Severity: HIGH** — every DTO with an optional `uniqueItems` array becomes un-validatable.

The generated `@AssertTrue` method has no null guard
(`UniqueItemsValidationMethodGenerator.java:25-30`, contrast with
`MultipleOfValidationMethodGenerator.java:81-99` which guards correctly):

```java
@AssertTrue(message = "tags does not contain unique items")
private boolean hasTagsUniqueItems() {
  return new HashSet<>(tags).size() == tags.size();
}
```

Spec:

```yaml
UniqueItemsHolder:
  type: object
  required: [id]
  properties:
    id: { type: string }
    tags:
      type: array
      items: { type: string }
      uniqueItems: true
```

For a perfectly valid DTO without `tags`, `Validator.validate(dto)` throws
`javax.validation.ValidationException: HV000090 … caused by NullPointerException` instead of
returning zero violations. Required arrays missing from JSON hit the same crash (the user
should get a `@NotNull` violation). The generated `isValid()` methods are null-guarded, so
the two validation surfaces disagree.

Additional observation for the fix: since #340 maps `uniqueItems: true` arrays to
`Set<T>`, `new HashSet<>(set).size() == set.size()` is always true — the check is
simultaneously crash-prone for null and dead for non-null values.

Test: `issues/issue374`, spec `openapi-issue-374.yml`.

---

## [#375] Issue B — Bean-validation annotations stop at container nesting depth 1

**Suggested title:** Constraints and @Valid on second-level container elements are silently dropped from bean validation

**Severity: MEDIUM-HIGH** — frameworks relying on annotations (e.g. Spring `@Valid` request
bodies) silently under-validate; the generated `isValid()` finds the violations, so the two
surfaces disagree.

`JavaTypeGenerators.createDeepAnnotatedParameterizedClassName`
(`JavaTypeGenerators.java:25-38`) annotates only the immediate type parameter;
`ParameterizedClassName.java:50-73` renders deeper generic levels without any annotation
hook. The no-framework validator recurses fully (`PropertyValue.nestedPropertyValue()`).

Spec:

```yaml
PetMatrix:
  type: object
  properties:
    pets:
      type: array
      items:
        type: array
        items: { $ref: '#/components/schemas/Pet' }
    matrix:
      type: array
      items:
        type: array
        items: { type: integer, format: int32, maximum: 100 }
Pet:
  type: object
  required: [name]
  properties:
    name: { type: string, minLength: 2 }
```

Generated: `private List<@NotNull @Valid List<PetDto>> getPetsRaw()` — no `@Valid` on the
`PetDto` level, no `@Max` on the inner integer level. A matrix containing a Pet with a
1-character name (or the value 101) produces **0 violations** from bean validation while
`isValid()` correctly returns false.

Test: `issues/issue375`, spec `openapi-issue-375.yml`.

---

## [#376] Issue C — Fractional numeric bounds on integer properties are truncated instead of rounded to the valid range

**Suggested title:** minimum/maximum with fractional values on integer types generate wrong @Min/@Max

**Severity: LOW-MEDIUM**

`ConstraintsMapper.getMin/getMax` (`ConstraintsMapper.java:85-111`) use
`BigDecimal::longValue` (truncation toward zero):

- `minimum: 5.5` → `@Min(5)` — the invalid value **5** passes (should be `@Min(6)` = ceil).
- OpenAPI 3.1 `exclusiveMaximum: 100.5` → `@Max(99)` — the valid value **100** is rejected
  (should be `@Max(100)`); same for the 3.0 boolean form.
- `maximum: -5.5` → `@Max(-5)` admits -5 (should be `@Max(-6)` = floor).

The generated `isValid()` uses the same truncated literals, so both surfaces are wrong.

Test: `issues/issue376`, spec `openapi-issue-376.yml` (`minimum: 5.5`, value 5 → 0
violations today).

---

## [#377] Issue D — minLength/maxLength on binary (byte[]) properties not annotated although @Size supports arrays

**Suggested title:** Size constraints on `format: binary` properties are dropped from bean validation

**Severity: LOW-MEDIUM**

`JavaConstraints.java:25-40` has no entry for byte arrays, so the `SIZE` constraint is
classified unsupported and the `@Size` annotation is dropped — although Bean Validation's
`@Size` explicitly supports arrays including `byte[]`. Meanwhile
`ConstraintConditions.sizeAccessorForProperty` (`ConstraintConditions.java:114-119`)
special-cases arrays → `length`, so the generated `isValid()` **does** enforce the bounds.
The emitted `UNSUPPORTED_VALIDATION` warning is therefore also factually wrong: it claims
"no annotations **or code** is generated for validation" while validation code is generated.

```yaml
BinaryHolder:
  type: object
  properties:
    data: { type: string, format: binary, minLength: 2, maxLength: 4 }
```

A 10-byte value yields 0 violations from `Validator.validate()` but `isValid() == false`.

Test: `issues/issue377`, spec `openapi-issue-377.yml` (this schema is wired outside
`issueNumbers` in `example/build.gradle` because the warning is configured to fail the
example build and must be disabled for this one schema).

---

## [#378] Issue E — anyOf: getXxxDto() returns empty for non-first schemas when the instance matches several schemas

**Suggested title:** anyOf getter returns Optional.empty for an instance valid against multiple schemas

**Severity: MEDIUM** — matching several schemas is exactly what anyOf permits; the generated
JavaDoc ("Returns {@link UserDto} … in case it is valid against the schema User … empty
otherwise") is violated.

`OneOfAnyOfDtoGetterGenerator.java:105-113` emits
`.stream().findFirst().flatMap(Function.identity())` over the `foldAnyOf` result —
`findFirst()` picks the first *valid* branch instead of the branch belonging to the
requested DTO:

```java
public Optional<UserDto> getUserDto() {
  return this.<Optional<UserDto>>foldAnyOf(ignore -> Optional.empty(), Optional::of)
      .stream().findFirst().flatMap(Function.identity());
}
```

For `{"adminname":"admin","username":"user"}` against `anyOf: [Admin, User]`,
`getAdminDto()` is present but `getUserDto()` returns empty (the fold list is
`[Optional.empty, Optional.of(user)]`).

Test: `issues/issue378`, spec `openapi-issue-378.yml`.

---

## [#379] Issue F — oneOf without discriminator: foldOneOf folds to the first match instead of onInvalid for double matches

**Suggested title:** foldOneOf with onInvalid ignores "valid against exactly one" for oneOf without discriminator

**Severity: MEDIUM**

`FoldMethodGenerator.singleResultFoldConditionAndContent()`
(`FoldMethodGenerator.java:157-174`) emits only `if (isValidAgainstXDto())` chains without a
`getOneOfValidCount() == 1` guard. The generated JavaDoc (and `doc/040_compositions.md`)
says the `onInvalid` supplier "gets called in case this instance is not valid against
exactly one of the defined oneOf schemas".

For a payload valid against both schemas (invalid per oneOf; bean validation correctly
rejects it), `foldOneOf(onAdmin, onUser, onInvalid)` returns `onAdmin` and `getAdminDto()`
is present while `getUserDto()` is empty.

Test: `issues/issue379`, spec `openapi-issue-379.yml`.

---

## [#380] Issue G — Discriminator mapping: multiple keys for the same schema are silently reduced to one arbitrary key

**Suggested title:** Discriminator mapping with several payload values per schema drops all but one alias

**Severity: MEDIUM**

`JavaDiscriminator.getStringValueForSchemaName` (`JavaDiscriminator.java:46-52`) reduces
the schema→keys relation with `findFirst()`; all consumers inherit this
(`DiscriminatorValidationMethodGenerator.java:75-81`, `FoldMethodGenerator.java:225-237`,
`InvalidCompositionDtoGetterGenerator.java:202-205`). OpenAPI explicitly allows several
payload values mapping to the same schema:

```yaml
discriminator:
  propertyName: type
  mapping:
    adm: '#/components/schemas/Admin'
    administrator: '#/components/schemas/Admin'
    usr: '#/components/schemas/User'
```

Generated code contains only `case "administrator"` (which alias survives is
HashMap-order-dependent, i.e. arbitrary). A spec-valid payload `{"type":"adm", …}` fails
bean validation and `foldOneOf` calls `onInvalid`.

Test: `issues/issue380`, spec `openapi-issue-380.yml`.

---

## [#381] Issue H — formatTypeMapping for integer/number matches the normalized format instead of the declared one

**Suggested title:** Numeric formatTypeMapping applied to the wrong properties; custom numeric formats unmappable

**Severity: MEDIUM**

`IntegerSchema.java:65-66` / `NumberSchema.java:65-66` collapse any unknown or missing
format to `int32` / `float` and discard the declared format string; the mapping lookup
(`JavaIntegerType.java:47-52`, `JavaNumericType.java:47-52`) then matches against the
normalized value. `StringType` deliberately keeps `formatString` and matches on it — the
numeric types don't.

Consequences (all verified):
- A mapping for `formatType = "decimal"` is silently ignored for
  `{ type: number, format: decimal }` — custom numeric formats cannot be mapped at all.
- A mapping for `formatType = "float"` **is** applied to `format: decimal` (and to
  format-less `type: number`) properties — mappings apply to properties that declare a
  different format.
- Same for `int32` vs. e.g. `format: timestamp` on integers.

This is distinct from #311 (missing unused-mapping warning): here a mapping is *wrongly
applied*, not merely unused.

Test: `issues/issue381`, spec `openapi-issue-381.yml` + `formatTypeMapping` for
`decimal` → `CustomDecimal` in `example/build.gradle`; `PaymentDto.getAmount()` returns
`Float` today.

---

## [#384] Issue I — Global dtoMapping block is accepted but silently ignored

**Suggested title:** dtoMapping configured globally has no effect

**Severity: MEDIUM-HIGH** (documented as "configurable globally" in
`doc/010_configuration.md:181`; silent no-op)

`OpenApiSchemaExtension.dtoMapping(...)` (`OpenApiSchemaExtension.java:112-116`) accepts a
root-level `dtoMapping { }` block, but the merge chain in `getSchemaExtensions()`
(`OpenApiSchemaExtension.java:220-236`) calls `withCommonClassMappings`,
`withCommonFormatTypeMappings` etc. and never `withCommonDtoMappings` —
`SingleSchemaExtension.withCommonDtoMappings` (`SingleSchemaExtension.java:287`) and
`getCommonDtoMappings()` (`OpenApiSchemaExtension.java:168`) are dead code. A user
configuring a global dtoMapping gets no error, no warning and no mapping.

Red test:
`OpenApiSchemaExtensionTest.getSchemaExtensions_when_commonDtoMapping_then_propagatesToSchemas`
(plugin module — an example-project test is not practical because a global mapping in
`example/build.gradle` would leak into the noJson/noValidation source-set variants whose
generated code cannot reference the custom type).

---

## [#383] Issue J — formatTypeMapping ignored for referenced ($ref) enum schemas

**Suggested title:** formatTypeMapping not applied to $ref'd enum schemas (works inline and for $ref'd strings)

**Severity: LOW-MEDIUM** — follow-up to #113, which fixed the inline case.

`EnumSchema.mapToPojo` (`EnumSchema.java:52-56`) — the path taken for component-level /
`$ref`'d enum schemas — discards `delegate.getFormat()`, whereas the inline path
(`mapToMemberType`, lines 59-83) carries the format into `EnumType.format`, and
`StringSchema` keeps the format for `$ref`'d plain strings. Refactoring an inline enum into
a named component therefore silently changes the generated API (custom type → raw enum DTO).

```yaml
Kind:
  type: string
  format: issue382kind
  enum: [NEW, OLD]
KindHolder:
  type: object
  required: [refKind]
  properties:
    refKind: { $ref: '#/components/schemas/Kind' }
```

With `formatTypeMapping { formatType = "issue382kind"; classType = …CustomKind; … }`,
`getRefKind()` returns `KindDto` instead of `CustomKind`.

Test: `issues/issue383`, spec `openapi-issue-383.yml` + mapping in `example/build.gradle`.

---

## [#385] Issue K — jsonSupport=none + xmlSupport=jackson generates non-compiling code (annotations without imports, wrong deserializer dialect)

**Suggested title:** XML-only Jackson configuration produces uncompilable generated code

**Severity: MEDIUM-HIGH** — `xmlSupport` without JSON support is a documented configuration
(`doc/010_configuration.md`).

Two defects, same root cause (the code decides "Jackson JSON annotations needed" via
`PojoSettings.isJacksonJson()` — true for xml-only configs — but resolves import prefixes
via `getJsonSupport()`, which is NONE):

1. `JacksonRefs.jsonRefString` (`JacksonRefs.java:66-75`) returns `""` for
   `jsonSupport=none`, and `JacksonRefs.generator` silently drops empty refs — but
   `@JsonPOJOBuilder`/`@JsonDeserialize` are still printed. Generated DTOs reference the
   annotations without imports → compile error.
2. `JacksonZonedDateTimeDeserializerGenerator` checks `getJsonSupport() == JACKSON_2` at
   lines 44/55/67/95; all false for `jsonSupport=none`, so even `xmlSupport = "jackson-2"`
   gets the Jackson-3 shape (`extends ValueDeserializer`, `p.getString()`) with zero
   imports.

Reproduction (verified against 4.0.1): any spec with an object schema + a `date-time`
property, config `jsonSupport = "none"; xmlSupport = "jackson-2"` → 7 compile errors
(`cannot find symbol: class JsonPOJOBuilder / JsonDeserialize / JsonParser /
DeserializationContext / ValueDeserializer`).

No example test possible (generated code does not compile; note
`example/build.gradle` always sets `xmlSupport = jsonSupport`, so no existing suite reaches
the xml-only path). Red plugin unit tests for both defects already exist on branch
`266-improve-deserialisation-of-enums` (`JacksonRefsTest`,
`JacksonZonedDateTimeDeserializerGeneratorTest`).

---

## [#386] Issue L — Mixed Jackson generations (jsonSupport=jackson-2 + xmlSupport=jackson-3) are not rejected

**Suggested title:** Validate against mixing jackson-2 and jackson-3 between jsonSupport and xmlSupport

**Severity: LOW-MEDIUM**

Nothing rejects `jsonSupport = "jackson-2"` + `xmlSupport = "jackson-3"` (or the reverse);
`PojoSettings.validate()` (`PojoSettings.java:135-162`) only checks mapping-conversion
warnings. Verified against 4.0.1: the build succeeds and a single generated file imports
both `com.fasterxml.jackson.databind.annotation.JsonDeserialize` and
`tools.jackson.dataformat.xml.annotation.JacksonXmlRootElement`. With both jars on the
classpath this compiles and runs — but each mapper silently ignores the other generation's
annotations (Jackson 3's XmlMapper does not read `com.fasterxml` builder annotations), a
plausible partial-migration trap. A clear configuration-time error would prevent it.

No example test possible (the desired behavior is a build failure).

---

## [#387] Issue M — Same property with different necessity/nullability across composition branches generates uncompilable code

**Suggested title:** oneOf/anyOf/allOf with same-named property of different requiredness/nullability does not compile

**Severity: HIGH**

The parent DTO's fields come from the least-restrictive merge
(`JavaPojoMember.mergeToLeastRestrictive`, `JavaPojoMember.java:240-252`), but:

- `ConversionMethodGenerator.PojoAndMember.getFieldNames`
  (`ConversionMethodGenerator.java:136-138`) builds `asXxxDto()` constructor calls from the
  **branch** pojo's technical members (e.g. references `isFooPresent` when the parent has
  `isFooNull`),
- `DtoSetterGenerator` (`DtoSetterGenerator.java:64-87`) calls parent builder setters with
  the **branch** getter types (`setFoo(dto.getFooOpt())` where only `setFoo(String)` /
  `setFoo(Tristate<String>)` exist),
- `AllOfBuilderStage.java:196-211` generates stage setters from the sub-pojo's members,
  delegating to nonexistent builder overloads.

```yaml
SchemaA:
  type: object
  required: [foo]
  properties:
    foo: { type: string, nullable: true }   # required + nullable
SchemaB:
  type: object
  properties:
    foo: { type: string }                   # optional + not nullable
AOrB:
  oneOf: [ { $ref: '#/…/SchemaA' }, { $ref: '#/…/SchemaB' } ]
```

Verified matrix (generate + javac): required+nullable vs optional+notNullable FAILS (4
errors), required+nullable vs optional+nullable FAILS, optional+notNullable vs
optional+nullable FAILS, allOf variant FAILS (6 errors incl. staged-builder stages).
Identical quadrants and required↔optional with same nullability compile fine.

Not a documented limitation (`MemberKey.java:10-15` explicitly claims these merge into one
technical member; #133 covers different *types* only). No example test possible.

---

## [#382] Issue N — Composition container drops dtoMapping conversions of merged members → uncompilable code

**Suggested title:** JavaObjectType.withNullability loses the ApiType — dtoMapping on members of composed schemas breaks the container

**Severity: HIGH** (uncompilable generated code)

`JavaObjectType.withNullability` (`JavaObjectType.java:71-74`) delegates to a constructor
that hardcodes `Optional.empty()` for the `apiType`; every other `JavaType` implementation
preserves it. The merge of same-named members in composition containers
(`JavaPojoMembers.java:39` → `mergeToLeastRestrictive`) always calls `withNullability`, so
an object member mapped via dtoMapping-with-conversion keeps the custom type in the branch
DTOs but reverts to the raw DTO type in the container — and the container's generated
`setHomeDto(HomeDto dto)` then calls `setAddress(dto.getAddress())`, passing the custom
type into the raw-typed setter:

```
LocationDto.java:396: error: no suitable method found for setAddress(CustomAddress)
LocationDto.java:404: error: no suitable method found for setAddress(Optional<CustomAddress>)
```

Spec: `example/src/main/resources/issues/openapi-issue-382.yml` (Address/Home/Office +
`Location: anyOf [Home, Office]`, dtoMapping `AddressDto` → `CustomAddress` with
conversions). The spec is intentionally NOT wired in `example/build.gradle` — wiring it
breaks `compileJava`, which is the demonstration. After the fix, wire `'382'` into
`issueNumbers`, restore the dtoMapping block for Issue382 and add the custom class + a
test asserting the container getter returns `CustomAddress`.

---

## [#388] Issue O — Enum values and extracted descriptions are not Java-escaped

**Suggested title:** Enum members containing quotes/backslashes (or extracted descriptions with quotes) generate uncompilable code

**Severity: HIGH** (realistic trigger via description extraction)

`EnumGenerator.java:95-100` prints `%s("%s", "%s")` without escaping either the enum value
or the description (contrast `ValidationAnnotationGenerator.patternAnnotation`, which uses
`JavaEscaper`).

- `enum: ['with"quote', 'back\slash']` → `WITH_QUOTE("with"quote", "")` — syntax error.
- With `enumDescriptionExtraction`, a description line like `` `ACTIVE`: The "active" state ``
  → `ACTIVE("ACTIVE", "The "active" state")` — uncompilable from an innocuous spec comment.

No example test possible (output does not compile).

---

## [#389] Issue P — Sanitized enum constant names are not uniquified

**Suggested title:** Distinct enum values collapsing to the same Java constant generate duplicate enum constants

**Severity: MEDIUM**

`EnumConstantName.asJavaConstant` (`EnumConstantName.java:25-41`) + `JavaIdentifier`
sanitization map distinct values to one identifier with no collision handling:

- `enum: [foo-bar, foo_bar]` → `FOO_BAR` twice → uncompilable.
- `enum: [abc, ABC]` → `ABC` twice.
- `enum: ["+", "-"]` → both become `_` (also a keyword since Java 9).

No example test possible.

---

## [#390] Issue Q — Empty-string enum member crashes generation

**Suggested title:** Enum containing "" aborts generation with "A name must not be null or empty"

**Severity: MEDIUM**

`EnumConstantName.java:31` → `Name.ofString` throws for `enum: ["", "value"]` (legal in
OpenAPI, common as "unset" sentinel). Generation aborts with an
`IllegalArgumentException` that names neither the schema nor the property. No example test
possible.

---

## [#391] Issue R — OpenAPI 3.1: non-string enum literals under type string crash with ClassCastException

**Suggested title:** 3.1 spec with numeric enum literals crashes generation (works in 3.0)

**Severity: MEDIUM**

`EnumSchema.java:41-44` does `enums.stream().map(String.class::cast)`. The 3.0 parser
coerces YAML literals to strings; the 3.1 parser keeps native types, so

```yaml
openapi: "3.1.0"
…
NumberValues: { type: string, enum: [1, 2] }
```

crashes with `ClassCastException: Integer → String` at `EnumSchema.wrap`. Merely changing a
spec's version string breaks generation with a raw CCE instead of coercion
(`String.valueOf`) or a proper error. No example test possible.

---

## [#392] Issue S — Constraints on nested container values reference validator methods that are never generated

**Suggested title:** uniqueItems/multipleOf on nested arrays or map values generate calls to nonexistent methods

**Severity: HIGH** (uncompilable)

The no-framework validator emits condition calls for every nested `PropertyValue`
(`ConstraintConditions.java:211-239`), but the corresponding method generators only handle
top-level members:

- `uniqueItems` on an inner array (array-of-arrays):
  `hasMatrixValueUniqueItems()` is called but never generated
  (`UniqueItemsValidationMethodGenerator.java:42-46` only fires for members whose own type
  is an array). Bonus symptom: the inner uniqueItems→Set mapping produces a raw type
  `List<Set>` getter (element type lost).
- `multipleOf` on array items or additionalProperties values:
  `isNumbersValueMultipleOfValid()` / `isAdditionalPropertiesValueMultipleOfValid()` called
  but never generated (`MultipleOfValidationMethodGenerator.java:107-116` iterates only
  `pojo.getMembers()`).

```yaml
ItemsMultipleOf:
  type: object
  properties:
    numbers:
      type: array
      items: { type: integer, format: int32, multipleOf: 5 }
```

Both verified by generate+javac: `error: cannot find symbol`. No example test possible.

---

## [#393] Issue T — Fractional multipleOf on integer members generates an invalid Java literal

**Suggested title:** multipleOf: 2.5 on an integer property generates `% 2.5L`

**Severity: MEDIUM** (uncompilable; `multipleOf: 10.0` — YAML float — likely also triggers)

`MultipleOfValidationMethodGenerator.java:81-89` appends an `L` suffix to
`BigDecimal.toString()`:

```java
return intValue == null || intValue % 2.5L == 0;   // error: not a statement
```

The BigDecimal `divideAndRemainder` path already used for Float/Double members would be
correct here. No example test possible.

---

## [#394] Issue U — Required additional property with nullable value schema generates uncompilable staged-builder code

**Suggested title:** required + additionalProperties with nullable value type does not compile

**Severity: MEDIUM**

`RequiredMemberBuilderGenerator.java:28-34` emits an `Optional<T>` stage setter for
nullable required members — including required additional properties
(`JavaRequiredAdditionalProperty.asMember` keeps the nullable value type) — but
`RequiredAdditionalPropertiesSetterGenerator.java:27-41` only ever generates the raw-typed
`Builder` setter:

```yaml
NullableTypedAp:
  type: object
  required: [name, reqAp]      # reqAp not declared under properties
  properties:
    name: { type: string }
  additionalProperties: { type: string, nullable: true }
```

→ `error: incompatible types: Optional<String> cannot be converted to String` (both builder
variants). Secondary semantic issue for the fix: `RequiredAdditionalPropertiesGetter`
emits `@NotNull getReqApAsObject()` unconditionally, so even after the compile fix a
spec-valid `{"name":"x","reqAp":null}` would fail validation — "required" should mean
present (`containsKey`), with nullability governing the value. No example test possible.

---

## [#395] Issue V — No collision detection for derived Java identifiers

**Suggested title:** Legal specs generate uncompilable code when property names collide after sanitization or with synthetic members

**Severity: MEDIUM** (family; silent at generation time)

Verified cases (all generate + fail javac with no warning):

1. `a-b` and `a_b` in one schema → both become `a_b` (`JavaIdentifier.java:88-91`).
2. A property literally named `additionalProperties` collides with the synthetic member
   (`JavaAdditionalProperties.java:26,59`).
3. A property named `propertyCount` collides with `getPropertyCount()`
   (`PojoPropertyCountMethod.java:31`).
4. A required+nullable `username` alongside a boolean property `isUsernamePresent` collides
   with the generated flag field (`IsPresentFlagName.java:14`).

Related but distinct from closed #310 (local *variable* clashes). A graceful fix would
deterministically de-duplicate or fail generation naming the colliding properties. No
example test possible.

---

## [#396] Issue W — type: number without format maps to Float (silent precision loss)

**Suggested title:** Default Java type for format-less `type: number` is Float — consider Double (or make it configurable)

**Severity: LOW / design question**

`NumberSchema.java:65-66` defaults missing/unknown formats to `Format.FLOAT` →
`java.lang.Float`. JSON numbers are double-precision: deserializing
`3.141592653589793` into such a DTO and re-serializing yields `3.1415927` — silent data
corruption for any spec that legitimately omits `format`. Other generators default to
`Double`/`BigDecimal`. Possibly intentional legacy behavior — filed as a
question/enhancement rather than a hard bug; changing the default is breaking, so a config
option may be the right shape. (No red test added since the desired behavior is a design
decision.)

---

## [#397] Issue X — Documentation fixes (collected)

**Suggested title:** Documentation: stale option names, wrong samples and dead links

All doc-only (code behaves differently than documented; fix the docs):

1. `doc/010_configuration.md:425,434` — documents `warnings { failOnMissingConversion }`;
   the DSL property is `failOnMissingMappingConversion` (`WarningsConfig.java:27`). Copying
   the sample fails the build with "unknown property".
2. `doc/040_compositions.md:174-204` — documents `fold(...)`; generated methods are
   `foldOneOf(...)`/`foldAnyOf(...)` (renamed in v2 per the migration guide).
3. `doc/070_validation.md:3` — "enable validation by setting `enableValidation` to true";
   the v4 DSL is `validation { enabled = true }` (restructured per the v3→v4 migration
   guide).
4. `doc/040_compositions.md:139-144` — sample uses nonexistent
   `fullAdminOrUserBuilder()` (real name includes the suffix:
   `fullAdminOrUserDtoBuilder()`) and calls `setAnyOfContainer` on the *oneOf* schema's
   builder with the wrong container type.
5. `doc/091_xml_support.md:137-169` — the "wrapped array without name" sample shows a
   `<book>` wrapper; the implementation (and the adjacent text) produce a wrapper named
   like the property (`<books>`).
6. Dead links: `doc/010_configuration.md:246` → nonexistent `spring-example` project;
   `doc/070_validation.md:58-63` → `TestValidation.java` files actually named
   `ValidationTest.java` (and missing `../` prefixes); several cross-file anchors in
   doc/010 and doc/030 point to sections living in other files.
7. `doc/110_limitations.md:11` ("Conversions for mappings of maps as additional property is
   currently not supported") contradicts the 4.0.0 change-log entry for #307; the leftover
   hard-fail guard in `JavaAdditionalProperties.java:37-45` still references issue 307 —
   reconcile the two statements and the exception message.

---

## Not filed

- Branch-266 markdown finding 10 (discriminator value for mapped enum members): could not
  be reproduced on master — branch-introduced, keep it on the branch's list.
- Cleanups/efficiency items from the branch markdown (its findings 4-6, 11-12): branch
  work respectively test-infra only.
