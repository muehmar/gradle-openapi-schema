## Name Collisions

Two properties of the same schema, or a property and a method the plugin always generates, may end up producing the
same Java name. This page lists the cases and how each is resolved, so that a surprising method name in the generated
code can be traced back to its cause (see
[Issue 438](https://github.com/muehmar/gradle-openapi-schema/issues/438)).

### Contract names and anchors

The distinction which drives every resolution below:

* A **contract name** is referenced from outside its declaration - an api getter, a builder setter, a framework method.
  It is part of the api of the generated class and is therefore **never renamed**.
* An **anchor** is only ever declared and never referenced: the JSON getter, the JSON setter and the validation getter.
  Jackson is unaffected by their name because every serialized member carries an explicit `@JsonProperty` and
  auto-detection is switched off; bean validation is unaffected because the constraints sit on the method, not on its
  name. An anchor can therefore be renamed to dodge a collision.

A **companion flag field** (the `private boolean` tracking whether a nullable property is present) is likewise private
and only read within its own class, so it can be renamed too.

**A schema without a collision generates exactly the same code as before**: the plain name is always tried first, and
only a real collision triggers a rename.

### The cases

Grouped by what a collision costs you. Everything above the last row is resolved automatically; only
the last one fails the generation.

| What collides | Case | Resolution | Visible to you |
|---|---|---|---|
| JSON getter / JSON setter of a property vs. the api of a sibling | 1, 2 | the anchor gets the lowest free counter appended | nothing - Jackson maps by the explicit `@JsonProperty` |
| Validation getter vs. the api of a sibling | 4 | the anchor gets the lowest free counter appended | the property path of a constraint violation |
| Companion flag field vs. a property | 5 | the field gets an underscore, then a counter | the property path of a constraint violation |
| Cross-DTO accessor vs. the api of a sibling | 6 | the accessors carry the fixed suffix `Internal2741988768` | nothing - they are package-private |
| A **private** framework method vs. a property | 7 | the method gets an underscore, then a counter | the property path of a constraint violation |
| A **public** framework method vs. a property | 7 | the method gets an underscore, then a counter, **and a `NAME_COLLISION` warning is emitted** | the api of the dto - code calling the method has to be adapted |
| Api getter vs. api getter of a sibling | 3 | **the generation fails** | - |

Examples:

| Case | Example |
|---|---|
| 1, 2 | `name` + `nameJson`: the anchors of `name` become `getNameJson1()` / `setNameJson1()`, the sibling keeps `getNameJson()` |
| 4 | a property named `point.` is sanitized to `point_`, so its validation getter becomes `getPoint_1()` |
| 5 | nullable `name` + `isNamePresent`: the flag field becomes `isNamePresent_`, the property keeps `isNamePresent` |
| 6 | `name` + `isNameNotNull` in a `oneOf` member: the accessors are `getNameInternal2741988768()` and `...Flag()` |
| 7 | a property `propertyCount`: the framework method becomes `getPropertyCount_()`, the property keeps `getPropertyCount()` |
| 3 | `propertyName` + `PropertyName` both yield `getPropertyName()` - the generation fails |

The `NAME_COLLISION` warning is emitted only where the **api** of the dto changes, because that is
the only case which breaks code you wrote by hand. A moved violation property path may still break
assertions in your tests, but it does not stop anything from compiling. Like every warning it can be
configured to fail the build, see [Warnings](030_warnings.md); users failing on warnings are
expected either to rename the property or to allow this particular warning:

```groovy
warnings {
    failOnNameCollision = false
}
```

### Framework methods

A framework method - one every dto carries regardless of its properties - is a contract name and cannot be renamed to
dodge a collision. Instead they live in a namespace no property can reach: since every method name derived from a
property carries `get`, `with` or `is`, a framework method carrying none of these prefixes can never collide.

There is one exception. Bean validation discovers constrained methods by the JavaBeans convention, so a method carrying
`@Valid`, `@AssertTrue`, `@AssertFalse`, `@Min` or `@Max` **must** keep a `get`/`is` prefix, or its constraints are
silently not evaluated. These methods therefore keep their prefixed name and remain collidable:

| Method | Annotation |
|--------|------------|
| `getPropertyCount()` | `@Min` / `@Max` from `minProperties` / `maxProperties` |
| `isAllAdditionalPropertiesHaveCorrectType()` | `@AssertTrue` |
| `getAdditionalProperties_()` | `@Valid`, plus `@JsonAnyGetter` for Jackson |
| `getOneOf()` / `getAnyOf()`, `getInvalidOneOf()` / `getInvalidAnyOf()` | `@Valid` |
| `isValidAgainstNoOneOfSchema()`, `isValidAgainstMoreThanOneSchema()`, `isValidAgainstTheCorrectOneOfSchema()` | `@AssertFalse` / `@AssertTrue` |

A property colliding with one of the methods above renames the method, appending an underscore and then a counter -
the same rule as for a companion flag field. `getPropertyCount()` is the only public one, so it is the only one whose
rename emits the `NAME_COLLISION` warning.

### Which names a property occupies

A method name derived from a property always carries a prefix:

| Prefix | Methods |
|--------|---------|
| `get` | the api getters, the JSON getter, the validation getter |
| `with` | the withers |
| `is` | the `@AssertTrue` method of a companion flag |
| the configured `builderMethodPrefix` | the builder setters - **empty by default**, so the setters are named after the bare property (`name(...)`) unless a prefix such as `set` is configured |

Note that a property name is normalised before a method name is derived from it: `propertyCount` and `PropertyCount`
both yield `getPropertyCount()`, so two properties differing only in the case of their first letter collide even though
Java itself is case-sensitive. This is a case 3 collision - and, for this particular name, additionally a case 7 one
with the generated `getPropertyCount()`.
