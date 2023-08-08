package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaEnumTypeTest {
  @Test
  void wrap_when_enumTypeWrapped_then_correctWrapped() {
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("Gender"), PList.of("male", "female", "divers"));
    final JavaEnumType javaType = JavaEnumType.wrap(enumType);

    assertEquals("Gender", javaType.getFullClassName().asString());
    assertEquals("Gender", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("Gender"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
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

    assertEquals("CustomGender", javaType.getFullClassName().asString());
    assertEquals(
        "com.github.muehmar.gradle.openapi.CustomGender",
        javaType.getQualifiedClassName().asName().asString());
    assertEquals(
        PList.of("com.github.muehmar.gradle.openapi.CustomGender"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_enumTypeWrappedWithEmptyTypeMappings_then_correctWrapped() {
    final EnumType enumType =
        EnumType.ofNameAndMembersAndFormat(
            Name.ofString("Gender"), PList.of("male", "female", "divers"), "Gender");
    final JavaType javaType = JavaEnumType.wrap(enumType, TypeMappings.empty());

    assertEquals("Gender", javaType.getFullClassName().asString());
    assertEquals("Gender", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("Gender"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void asInnerClassOf_when_called_then_classNameReferencedWithOuterClass() {
    final JavaEnumType enumType =
        JavaEnumType.wrap(
            EnumType.ofNameAndMembers(Name.ofString("Color"), PList.of("yellow", "red")));
    final JavaEnumType mappedType = enumType.asInnerClassOf(JavaIdentifier.fromString("AdminDto"));

    assertEquals("AdminDto.Color", mappedType.getQualifiedClassName().getClassName().asString());
  }
}
