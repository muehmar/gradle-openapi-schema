package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FactoryMethodConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.InstanceMethodConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ToApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumTypeBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.DtoMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JavaEnumTypeTest {
  @Test
  void wrap_when_enumTypeWrapped_then_correctWrapped() {
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("Gender"), PList.of("male", "female", "divers"));
    final JavaEnumType javaType = JavaEnumType.wrapForDiscriminator(enumType);

    final QualifiedClassName enumClassName = QualifiedClassName.ofName(Name.ofString("Gender"));

    // The enum is represented internally as String with the enum as plugin api type.
    assertTrue(javaType.getApiType().isPresent());
    assertEquals(enumClassName, javaType.getApiType().get().getClassName());

    assertEquals("String", javaType.getParameterizedClassName().asString());
    assertEquals("String", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("Gender", "java.lang.String"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_enumTypeWrappedWithTypeMappings_then_correctWrapped() {
    final EnumType enumType =
        EnumTypeBuilder.createFull()
            .name(Name.ofString("Gender"))
            .members(PList.of("male", "female", "divers"))
            .nullability(NOT_NULLABLE)
            .legacyNullability(NOT_NULLABLE)
            .format("Gender")
            .build();
    final TypeMappings typeMappings =
        TypeMappings.ofSingleFormatTypeMapping(
            new FormatTypeMapping(
                "Gender", "com.github.muehmar.gradle.openapi.CustomGender", Optional.empty()));

    // method call
    final JavaType javaType = JavaEnumType.wrap(enumType, typeMappings);

    // A format mapping without conversion replaces the enum with the custom type entirely (no enum
    // semantics, no plugin api type).
    assertInstanceOf(JavaObjectType.class, javaType);
    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("CustomGender", javaType.getParameterizedClassName().asString());
    assertEquals(
        "com.github.muehmar.gradle.openapi.CustomGender",
        javaType.getQualifiedClassName().asName().asString());
    assertEquals(
        PList.of("com.github.muehmar.gradle.openapi.CustomGender"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_enumTypeWrappedWithTypeMappingsAndConversion_then_correctWrapped() {
    final EnumType enumType =
        EnumTypeBuilder.createFull()
            .name(Name.ofString("Gender"))
            .members(PList.of("male", "female", "divers"))
            .nullability(NOT_NULLABLE)
            .legacyNullability(NOT_NULLABLE)
            .format("Gender")
            .build();
    final TypeConversion typeConversion =
        new TypeConversion("toEnum", "com.github.muehmar.gradle.openapi.CustomGender#fromEnum");
    final FormatTypeMapping formatTypeMapping =
        new FormatTypeMapping(
            "Gender",
            "com.github.muehmar.gradle.openapi.CustomGender",
            Optional.of(typeConversion));
    final TypeMappings typeMappings = TypeMappings.ofSingleFormatTypeMapping(formatTypeMapping);

    // method call
    final JavaType javaType = JavaEnumType.wrap(enumType, typeMappings);

    final QualifiedClassName customClassName =
        QualifiedClassName.ofQualifiedClassName("com.github.muehmar.gradle.openapi.CustomGender");
    final QualifiedClassName enumClassName = QualifiedClassName.ofName(Name.ofString("Gender"));

    assertEquals(Optional.of(customClassName), javaType.getApiType().map(ApiType::getClassName));
    assertEquals(
        Optional.of("CustomGender"),
        javaType.getApiType().map(apiType -> apiType.getParameterizedClassName().asString()));

    // Now expects BOTH the enum plugin type conversion AND the custom type conversion
    // Note: Enum plugin conversion comes first, then custom type conversion
    assertEquals(
        Optional.of(
            PList.of(
                new ToApiTypeConversion(
                    ConversionMethod.ofFactoryMethod(
                        new FactoryMethodConversion(enumClassName, Name.ofString("fromValue")))),
                new ToApiTypeConversion(
                    ConversionMethod.ofString(customClassName, typeConversion.getToCustomType())))),
        javaType.getApiType().map(ApiType::getToApiTypeConversion));
    assertEquals(
        Optional.of(
            PList.of(
                new FromApiTypeConversion(
                    ConversionMethod.ofString(customClassName, typeConversion.getFromCustomType())),
                new FromApiTypeConversion(
                    ConversionMethod.ofInstanceMethod(
                        InstanceMethodConversion.ofString("getValue"))))),
        javaType.getApiType().map(ApiType::getFromApiTypeConversion));

    // The internal type uses String, so parameterizedClassName returns String
    assertEquals("String", javaType.getParameterizedClassName().asString());
    // Internal className is java.lang.String (the fully qualified underlying type)
    assertEquals("java.lang.String", javaType.getQualifiedClassName().asName().asString());
    // getAllQualifiedClassNames includes custom type and internal String, but not enum name when
    // custom type is present
    assertEquals(
        PList.of("com.github.muehmar.gradle.openapi.CustomGender", "java.lang.String"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_enumTypeWrappedWithEmptyTypeMappings_then_correctWrapped() {
    final EnumType enumType =
        EnumTypeBuilder.createFull()
            .name(Name.ofString("Gender"))
            .members(PList.of("male", "female", "divers"))
            .nullability(NOT_NULLABLE)
            .legacyNullability(NOT_NULLABLE)
            .format("Gender")
            .build();

    // method call
    final JavaType javaType = JavaEnumType.wrap(enumType, TypeMappings.empty());

    final QualifiedClassName enumClassName = QualifiedClassName.ofName(Name.ofString("Gender"));

    // Verify the enum has a PluginApiType with fromValue/getValue conversions
    assertTrue(javaType.getApiType().isPresent());
    assertEquals(enumClassName, javaType.getApiType().get().getClassName());
    assertEquals("Gender", javaType.getApiType().get().getParameterizedClassName().asString());

    // Verify toApiTypeConversion contains fromValue
    final PList<ToApiTypeConversion> toConversions =
        javaType.getApiType().get().getToApiTypeConversion();
    assertEquals(1, toConversions.size());
    final ConversionMethod toMethod = toConversions.head().getConversionMethod();
    toMethod.fold(
        factoryMethod -> {
          assertEquals(enumClassName, factoryMethod.getClassName());
          assertEquals("fromValue", factoryMethod.getMethodName().asString());
          return null;
        },
        instanceMethod -> {
          fail("Expected factory method but got instance method");
          return null;
        },
        constructor -> {
          fail("Expected factory method but got constructor");
          return null;
        });

    // Verify fromApiTypeConversion contains getValue
    final PList<FromApiTypeConversion> fromConversions =
        javaType.getApiType().get().getFromApiTypeConversion();
    assertEquals(1, fromConversions.size());
    final ConversionMethod fromMethod = fromConversions.head().getConversionMethod();
    fromMethod.fold(
        factoryMethod -> {
          fail("Expected instance method but got factory method");
          return null;
        },
        instanceMethod -> {
          assertEquals("getValue", instanceMethod.getMethodName().asString());
          return null;
        },
        constructor -> {
          fail("Expected instance method but got constructor");
          return null;
        });

    // The internal type uses String, so parameterizedClassName returns String
    assertEquals("String", javaType.getParameterizedClassName().asString());
    // Internal className is String
    assertEquals("String", javaType.getQualifiedClassName().getClassName().asString());
    // getAllQualifiedClassNames should include both the internal String and the enum Gender from
    // API type
    assertEquals(
        PList.of("Gender", "java.lang.String"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @ParameterizedTest
  @MethodSource("nullabilityMapping")
  void wrap_when_nullabilityMapping_then_correctWrapped(
      Nullability nullability,
      Nullability legacyNullability,
      boolean allowNullableEnums,
      Nullability expectedNullability) {
    final EnumType enumType =
        EnumTypeBuilder.createFull()
            .name(Name.ofString("Gender"))
            .members(PList.of("male", "female", "divers"))
            .nullability(nullability)
            .legacyNullability(legacyNullability)
            .format("Gender")
            .build();

    final TypeMappings typeMappings =
        new TypeMappings(PList.empty(), PList.empty(), PList.empty(), allowNullableEnums);

    // method call
    final JavaType javaType = JavaEnumType.wrap(enumType, typeMappings);

    assertEquals(expectedNullability, javaType.getNullability());
  }

  public static Stream<Arguments> nullabilityMapping() {
    return Stream.of(
        arguments(NOT_NULLABLE, NOT_NULLABLE, false, NOT_NULLABLE),
        arguments(NULLABLE, NOT_NULLABLE, false, NULLABLE),
        arguments(NULLABLE, NULLABLE, false, NULLABLE),
        arguments(NOT_NULLABLE, NULLABLE, false, NOT_NULLABLE),
        arguments(NULLABLE, NOT_NULLABLE, true, NULLABLE),
        arguments(NOT_NULLABLE, NULLABLE, true, NULLABLE),
        arguments(NULLABLE, NULLABLE, true, NULLABLE),
        arguments(NOT_NULLABLE, NOT_NULLABLE, true, NOT_NULLABLE));
  }

  @Test
  void asInnerClassOf_when_called_then_classNameReferencedWithOuterClass() {
    final JavaEnumType enumType =
        JavaEnumType.wrapForDiscriminator(
            EnumType.ofNameAndMembers(Name.ofString("Color"), PList.of("yellow", "red")));
    final JavaEnumType mappedType = enumType.asInnerClassOf(JavaName.fromString("AdminDto"));

    // The internal type stays String, but the enum class (the api type) is referenced as an inner
    // class of the outer class.
    assertEquals("String", mappedType.getQualifiedClassName().getClassName().asString());
    assertEquals("AdminDto.Color", mappedType.getEnumClassName().getClassName().asString());
  }

  @Test
  void wrap_when_inlineEnum_then_patternConstraintWithAllowedValues() {
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("Gender"), PList.of("male", "female", "divers"));

    final JavaType javaType = JavaEnumType.wrap(enumType, TypeMappings.empty());

    // The enum is represented internally as a String, so its allowed values are validated with a
    // pattern constraint.
    assertEquals(
        "male|female|divers", javaType.getConstraints().getPattern().orElseThrow().getPattern());
  }

  @Test
  void wrap_when_enumMappedToCustomTypeWithoutConversion_then_noPatternConstraint() {
    final EnumType enumType =
        EnumTypeBuilder.createFull()
            .name(Name.ofString("Gender"))
            .members(PList.of("male", "female", "divers"))
            .nullability(NOT_NULLABLE)
            .legacyNullability(NOT_NULLABLE)
            .format("Gender")
            .build();
    final TypeMappings typeMappings =
        TypeMappings.ofSingleFormatTypeMapping(
            new FormatTypeMapping(
                "Gender", "com.github.muehmar.gradle.openapi.CustomGender", Optional.empty()));

    final JavaType javaType = JavaEnumType.wrap(enumType, typeMappings);

    // The enum is replaced by a custom type which can not be validated against the enum value
    // pattern, hence no pattern constraint must be generated.
    assertEquals(Optional.empty(), javaType.getConstraints().getPattern());
  }

  @Test
  void wrap_when_enumMappedToStringWithoutConversion_then_plainStringWithoutEnum() {
    final EnumType enumType =
        EnumTypeBuilder.createFull()
            .name(Name.ofString("Gender"))
            .members(PList.of("male", "female", "divers"))
            .nullability(NOT_NULLABLE)
            .legacyNullability(NOT_NULLABLE)
            .format("Gender")
            .build();
    final TypeMappings typeMappings =
        TypeMappings.ofSingleFormatTypeMapping(
            new FormatTypeMapping("Gender", "java.lang.String", Optional.empty()));

    final JavaType javaType = JavaEnumType.wrap(enumType, typeMappings);

    // The mapping replaces the enum entirely, so no enum is generated at all, even though the
    // mapped type is the same String the enum would be represented as internally.
    assertFalse(javaType instanceof JavaEnumType);
    assertEquals(Optional.empty(), javaType.getApiType());
    assertEquals(Optional.empty(), javaType.getConstraints().getPattern());
  }

  @Test
  void wrapAsObjectType_when_topLevelEnumWithoutMappings_then_patternConstraintWithAllowedValues() {
    // Red test for BUG 1: referenced ($ref) top-level enums must carry the same value-validation
    // pattern constraint as inline enums. wrapAsObjectType currently uses
    // EnumObjectType.getConstraints() which is hardcoded to Constraints.empty().
    final EnumObjectType enumObjectType =
        new EnumObjectType(pojoName("Gender", "Dto"), PList.of("male", "female"), NOT_NULLABLE);

    final JavaType javaType = JavaEnumType.wrapAsObjectType(enumObjectType, TypeMappings.empty());

    assertTrue(
        javaType.getConstraints().getPattern().isPresent(),
        "A top-level enum must carry a value-validation pattern constraint like an inline enum");

    final java.util.regex.Pattern compiledPattern =
        java.util.regex.Pattern.compile(
            javaType.getConstraints().getPattern().orElseThrow().getPattern());
    assertTrue(compiledPattern.matcher("male").matches());
    assertTrue(compiledPattern.matcher("female").matches());
    assertFalse(compiledPattern.matcher("other").matches());
  }

  @Test
  void wrap_when_enumMembersContainRegexMetacharacters_then_patternMatchesLiteralValuesOnly() {
    // Red test for BUG 2: enum member values are joined verbatim into the value-validation regex.
    // Members containing regex metacharacters must be quoted so the pattern matches exactly the
    // literal values. Today the unquoted '.' matches any character, so "1x5" wrongly matches.
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("Version"), PList.of("1.5", "2.5"));

    final JavaType javaType = JavaEnumType.wrap(enumType, TypeMappings.empty());

    final String patternString = javaType.getConstraints().getPattern().orElseThrow().getPattern();
    final java.util.regex.Pattern compiledPattern = java.util.regex.Pattern.compile(patternString);

    assertTrue(compiledPattern.matcher("1.5").matches());
    assertTrue(compiledPattern.matcher("2.5").matches());
    assertFalse(
        compiledPattern.matcher("1x5").matches(),
        "The dot in the member value must be quoted and not act as a regex wildcard");
  }

  @Test
  void wrap_when_enumMemberContainsUnbalancedParenthesis_then_patternCompilesAndMatchesValue() {
    // Red test for BUG 2: a member value with regex metacharacters like "a(b" currently yields an
    // invalid regex (PatternSyntaxException on compile). The member values must be quoted so the
    // produced pattern is a valid regex matching exactly the literal values.
    final EnumType enumType = EnumType.ofNameAndMembers(Name.ofString("Weird"), PList.of("a(b"));

    final JavaType javaType = JavaEnumType.wrap(enumType, TypeMappings.empty());

    final String patternString = javaType.getConstraints().getPattern().orElseThrow().getPattern();
    final java.util.regex.Pattern compiledPattern =
        assertDoesNotThrow(() -> java.util.regex.Pattern.compile(patternString));
    assertTrue(compiledPattern.matcher("a(b").matches());
  }

  @Test
  void wrap_when_nullableEnumMappedToCustomTypeWithoutConversion_then_nullabilityPreserved() {
    final EnumType enumType =
        EnumTypeBuilder.createFull()
            .name(Name.ofString("Gender"))
            .members(PList.of("male", "female"))
            .nullability(NULLABLE)
            .legacyNullability(NOT_NULLABLE)
            .format("Gender")
            .build();
    final TypeMappings typeMappings =
        TypeMappings.ofSingleFormatTypeMapping(
            new FormatTypeMapping(
                "Gender", "com.github.muehmar.gradle.openapi.CustomGender", Optional.empty()));

    final JavaType javaType = JavaEnumType.wrap(enumType, typeMappings);

    // The custom type replaces the enum, but the member keeps its nullability.
    assertEquals(NULLABLE, javaType.getNullability());
  }

  @Test
  void
      wrapAsObjectType_when_nullableEnumWithDtoMappingWithoutConversion_then_nullabilityPreserved() {
    final EnumObjectType enumObjectType =
        new EnumObjectType(pojoName("Gender", "Dto"), PList.of("male", "female"), NULLABLE);
    final DtoMapping dtoMapping =
        new DtoMapping(
            "GenderDto", "com.github.muehmar.gradle.openapi.CustomGender", Optional.empty());

    final JavaType javaType =
        JavaEnumType.wrapAsObjectType(enumObjectType, TypeMappings.ofSingleDtoMapping(dtoMapping));

    // The custom type replaces the enum, but the member keeps its nullability.
    assertEquals(NULLABLE, javaType.getNullability());
  }

  @Test
  void wrapAsObjectType_when_dtoMappingWithoutConversion_then_noEnumApiConversions() {
    // Red test for BUG 3: a dtoMapping WITHOUT conversion on a referenced top-level enum must
    // replace the enum entirely with the custom type (mirroring the inline-enum semantics of
    // wrap()). Today the String-typed enum plugin conversions (fromValue/getValue) are kept
    // against the custom-typed internal class, producing type-mismatched api conversions.
    final EnumObjectType enumObjectType =
        new EnumObjectType(pojoName("Gender", "Dto"), PList.of("male", "female"), NOT_NULLABLE);
    final DtoMapping dtoMapping =
        new DtoMapping(
            "GenderDto", "com.github.muehmar.gradle.openapi.CustomGender", Optional.empty());
    final TypeMappings typeMappings = TypeMappings.ofSingleDtoMapping(dtoMapping);

    final JavaType javaType = JavaEnumType.wrapAsObjectType(enumObjectType, typeMappings);

    // The custom type replaces the enum, so it becomes the internal class ...
    assertEquals(
        "com.github.muehmar.gradle.openapi.CustomGender",
        javaType.getQualifiedClassName().asName().asString());
    // ... and no api-type conversion must remain: the enum conversions fromValue(String)/getValue()
    // do not exist on the custom class.
    assertEquals(Optional.empty(), javaType.getApiType());
  }
}
