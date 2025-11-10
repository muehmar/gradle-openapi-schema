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
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FromApiTypeConversion;
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

    final QualifiedClassName className =
        QualifiedClassName.ofQualifiedClassName("com.github.muehmar.gradle.openapi.CustomGender");

    assertEquals(Optional.of(className), javaType.getApiType().map(ApiType::getClassName));
    assertEquals(
        Optional.of("CustomGender"),
        javaType.getApiType().map(apiType -> apiType.getParameterizedClassName().asString()));
    assertEquals(
        Optional.of(
            PList.single(
                new ToApiTypeConversion(
                    ConversionMethod.ofString(className, typeConversion.getToCustomType())))),
        javaType.getApiType().map(ApiType::getToApiTypeConversion));
    assertEquals(
        Optional.of(
            PList.single(
                new FromApiTypeConversion(
                    ConversionMethod.ofString(className, typeConversion.getFromCustomType())))),
        javaType.getApiType().map(ApiType::getFromApiTypeConversion));

    assertEquals("Gender", javaType.getParameterizedClassName().asString());
    assertEquals("Gender", javaType.getQualifiedClassName().asName().asString());
    assertEquals(
        PList.of("Gender", "com.github.muehmar.gradle.openapi.CustomGender"),
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
