package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
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
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumTypeBuilder;
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

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("Gender", javaType.getParameterizedClassName().asString());
    assertEquals("Gender", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("Gender"),
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
                        new FactoryMethodConversion(enumClassName, Name.ofString("valueOf")))),
                new ToApiTypeConversion(
                    ConversionMethod.ofString(customClassName, typeConversion.getToCustomType())))),
        javaType.getApiType().map(ApiType::getToApiTypeConversion));
    assertEquals(
        Optional.of(
            PList.of(
                new FromApiTypeConversion(
                    ConversionMethod.ofString(customClassName, typeConversion.getFromCustomType())),
                new FromApiTypeConversion(
                    ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("name"))))),
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

    // Verify the enum has a PluginApiType with valueOf/name conversions
    assertTrue(javaType.getApiType().isPresent());
    assertEquals(enumClassName, javaType.getApiType().get().getClassName());
    assertEquals("Gender", javaType.getApiType().get().getParameterizedClassName().asString());

    // Verify toApiTypeConversion contains valueOf
    final PList<ToApiTypeConversion> toConversions =
        javaType.getApiType().get().getToApiTypeConversion();
    assertEquals(1, toConversions.size());
    final ConversionMethod toMethod = toConversions.head().getConversionMethod();
    toMethod.fold(
        factoryMethod -> {
          assertEquals(enumClassName, factoryMethod.getClassName());
          assertEquals("valueOf", factoryMethod.getMethodName().asString());
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

    // Verify fromApiTypeConversion contains name
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
          assertEquals("name", instanceMethod.getMethodName().asString());
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

    assertEquals("AdminDto.Color", mappedType.getQualifiedClassName().getClassName().asString());
  }
}
