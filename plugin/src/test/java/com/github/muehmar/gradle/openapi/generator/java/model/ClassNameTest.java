package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ClassNameTest {

  @Test
  void ofQualifiedClassName_when_stringSupplied_then_correctParsed() {
    final ClassName className = ClassName.ofQualifiedClassName("java.lang.String");
    assertEquals("String", className.getClassName().asString());
    assertEquals("java.lang.String", className.getQualifiedClassName().asString());
  }

  @Test
  void fromFormatTypeMapping_when_called_then_correctClassNameCreated() {
    final ClassName className =
        ClassName.fromFormatTypeMapping(
            new FormatTypeMapping("uuid", "CustomUUID", "com.custom.CustomUUID"));
    assertEquals("CustomUUID", className.getClassName().asString());
    assertEquals("com.custom.CustomUUID", className.getQualifiedClassName().asString());
  }

  @Test
  void fromFormatTypeMapping_when_mappingWithoutImports_then_correctClassNameCreated() {
    final ClassName className =
        ClassName.fromFormatTypeMapping(new FormatTypeMapping("uuid", "CustomUUID", ""));
    assertEquals("CustomUUID", className.getClassName().asString());
    assertEquals("CustomUUID", className.getQualifiedClassName().asString());
  }

  @Test
  void fromFormatTypeMapping_when_noMatchingFormat_then_emptyReturned() {
    final Optional<ClassName> className =
        ClassName.fromFormatTypeMapping(
            "url",
            PList.single(new FormatTypeMapping("uuid", "CustomUUID", "com.custom.CustomUUID")));
    assertEquals(Optional.empty(), className);
  }

  @Test
  void fromFormatTypeMapping_when_matchingFormat_then_mappedClassNameReturned() {
    final Optional<ClassName> className =
        ClassName.fromFormatTypeMapping(
            "uuid",
            PList.single(new FormatTypeMapping("uuid", "CustomUUID", "com.custom.CustomUUID")));
    assertTrue(className.isPresent());
    assertEquals("CustomUUID", className.get().getClassName().asString());
    assertEquals("com.custom.CustomUUID", className.get().getQualifiedClassName().asString());
  }

  @Test
  void fromClassTypeMapping_when_called_then_correctClassNameCreated() {
    final ClassName className =
        ClassName.fromClassTypeMapping(
            new ClassTypeMapping("Double", "CustomDouble", "com.custom.CustomDouble"));
    assertEquals("CustomDouble", className.getClassName().asString());
    assertEquals("com.custom.CustomDouble", className.getQualifiedClassName().asString());
  }

  @Test
  void fromClassTypeMapping_when_calledWithoutImports_then_correctClassNameCreated() {
    final ClassName className =
        ClassName.fromClassTypeMapping(new ClassTypeMapping("Double", "CustomDouble", ""));
    assertEquals("CustomDouble", className.getClassName().asString());
    assertEquals("CustomDouble", className.getQualifiedClassName().asString());
  }

  @Test
  void mapWithClassMappings_when_matchingClass_then_correctMapped() {
    final ClassName className =
        ClassNames.DOUBLE.mapWithClassMappings(
            PList.single(
                new ClassTypeMapping("Double", "CustomDouble", "com.custom.CustomDouble")));
    assertEquals("CustomDouble", className.getClassName().asString());
    assertEquals("com.custom.CustomDouble", className.getQualifiedClassName().asString());
  }

  @Test
  void mapWithClassMappings_when_noMatchingClass_then_notMapped() {
    final ClassName className =
        ClassNames.INTEGER.mapWithClassMappings(
            PList.single(
                new ClassTypeMapping("Double", "CustomDouble", "com.custom.CustomDouble")));
    assertEquals("Integer", className.getClassName().asString());
    assertEquals("java.lang.Integer", className.getQualifiedClassName().asString());
  }

  @Test
  void getClassNameWithGenerics_when_called_then_correctFormatted() {
    final Name classNameWithGenerics =
        ClassNames.MAP.getClassNameWithGenerics(Name.ofString("String"), Name.ofString("UserDto"));
    assertEquals("Map<String, UserDto>", classNameWithGenerics.asString());
  }
}
