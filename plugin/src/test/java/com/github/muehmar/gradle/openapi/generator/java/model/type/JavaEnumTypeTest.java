package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ToApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaEnumTypeTest {
  @Test
  void wrap_when_enumTypeWrapped_then_correctWrapped() {
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("Gender"), PList.of("male", "female", "divers"));
    final JavaEnumType javaType = JavaEnumType.wrap(enumType);

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
        EnumType.ofNameAndMembersAndFormat(
            Name.ofString("Gender"), PList.of("male", "female", "divers"), "Gender");
    final TypeMappings typeMappings =
        TypeMappings.ofSingleFormatTypeMapping(
            new FormatTypeMapping(
                "Gender", "com.github.muehmar.gradle.openapi.CustomGender", Optional.empty()));

    // method call
    final JavaType javaType = JavaEnumType.wrap(enumType, typeMappings);

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
        EnumType.ofNameAndMembersAndFormat(
            Name.ofString("Gender"), PList.of("male", "female", "divers"), "Gender");
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

    assertEquals(
        Optional.of("com.github.muehmar.gradle.openapi.CustomGender"),
        javaType.getApiType().map(apiType -> apiType.getClassName().asString()));
    assertEquals(
        Optional.of("CustomGender"),
        javaType.getApiType().map(apiType -> apiType.getParameterizedClassName().asString()));
    assertEquals(
        Optional.of(
            new ToApiTypeConversion(ConversionMethod.ofString(typeConversion.getToCustomType()))),
        javaType.getApiType().map(ApiType::getToApiTypeConversion));
    assertEquals(
        Optional.of(
            new FromApiTypeConversion(
                ConversionMethod.ofString(typeConversion.getFromCustomType()))),
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
        EnumType.ofNameAndMembersAndFormat(
            Name.ofString("Gender"), PList.of("male", "female", "divers"), "Gender");

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

  @Test
  void asInnerClassOf_when_called_then_classNameReferencedWithOuterClass() {
    final JavaEnumType enumType =
        JavaEnumType.wrap(
            EnumType.ofNameAndMembers(Name.ofString("Color"), PList.of("yellow", "red")));
    final JavaEnumType mappedType = enumType.asInnerClassOf(JavaName.fromString("AdminDto"));

    assertEquals("AdminDto.Color", mappedType.getQualifiedClassName().getClassName().asString());
  }
}
