package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
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

    assertEquals(Optional.empty(), javaType.getApiClassName());
    assertEquals(Optional.empty(), javaType.getApiParameterizedClassName());

    assertEquals("Gender", javaType.getInternalParameterizedClassName().asString());
    assertEquals("Gender", javaType.getInternalClassName().getClassName().asString());
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
            new FormatTypeMapping("Gender", "com.github.muehmar.gradle.openapi.CustomGender"));
    final JavaType javaType = JavaEnumType.wrap(enumType, typeMappings);

    assertEquals(
        Optional.of("com.github.muehmar.gradle.openapi.CustomGender"),
        javaType.getApiClassName().map(QualifiedClassName::asString));
    assertEquals(
        Optional.of("CustomGender"),
        javaType.getApiParameterizedClassName().map(ParameterizedClassName::asString));

    assertEquals("Gender", javaType.getInternalParameterizedClassName().asString());
    assertEquals("Gender", javaType.getInternalClassName().asName().asString());
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
    final JavaType javaType = JavaEnumType.wrap(enumType, TypeMappings.empty());

    assertEquals(Optional.empty(), javaType.getApiClassName());
    assertEquals(Optional.empty(), javaType.getApiParameterizedClassName());

    assertEquals("Gender", javaType.getInternalParameterizedClassName().asString());
    assertEquals("Gender", javaType.getInternalClassName().getClassName().asString());
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

    assertEquals("AdminDto.Color", mappedType.getInternalClassName().getClassName().asString());
  }
}
