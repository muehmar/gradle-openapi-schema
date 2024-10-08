package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class QualifiedClassNameTest {

  @Test
  void ofQualifiedClassName_when_stringSupplied_then_correctParsed() {
    final QualifiedClassName className =
        QualifiedClassName.ofQualifiedClassName("java.lang.String");
    assertEquals("String", className.getClassName().asString());
    assertEquals("java.lang.String", className.asName().asString());
  }

  @Test
  void ofPojoName_when_pojoNameWithSpecialCharacters_then_correctClassName() {
    final QualifiedClassName className =
        QualifiedClassName.ofPojoName(pojoName("Prefixed.User", "Dto"));
    assertEquals("Prefixed_UserDto", className.getClassName().asString());
    assertEquals("Prefixed_UserDto", className.asName().asString());
  }

  @Test
  void fromFormatTypeMapping_when_called_then_correctClassNameCreated() {
    final QualifiedClassName className =
        QualifiedClassName.fromFormatTypeMapping(
            new FormatTypeMapping("uuid", "com.custom.CustomUUID", Optional.empty()));
    assertEquals("CustomUUID", className.getClassName().asString());
    assertEquals("com.custom.CustomUUID", className.asName().asString());
  }

  @Test
  void fromFormatTypeMapping_when_noMatchingFormat_then_emptyReturned() {
    final Optional<QualifiedClassName> className =
        QualifiedClassName.fromFormatTypeMapping(
            "url",
            PList.single(new FormatTypeMapping("uuid", "com.custom.CustomUUID", Optional.empty())));
    assertEquals(Optional.empty(), className);
  }

  @Test
  void fromFormatTypeMapping_when_matchingFormat_then_mappedClassNameReturned() {
    final Optional<QualifiedClassName> className =
        QualifiedClassName.fromFormatTypeMapping(
            "uuid",
            PList.single(new FormatTypeMapping("uuid", "com.custom.CustomUUID", Optional.empty())));
    assertTrue(className.isPresent());
    assertEquals("CustomUUID", className.get().getClassName().asString());
    assertEquals("com.custom.CustomUUID", className.get().asName().asString());
  }

  @Test
  void fromClassTypeMapping_when_called_then_correctClassNameCreated() {
    final QualifiedClassName className =
        QualifiedClassName.fromClassTypeMapping(
            new ClassTypeMapping("Double", "com.custom.CustomDouble", Optional.empty()));
    assertEquals("CustomDouble", className.getClassName().asString());
    assertEquals("com.custom.CustomDouble", className.asName().asString());
  }

  @Test
  void mapWithClassMappings_when_matchingClass_then_correctMapped() {
    final Optional<QualifiedClassName> maybeClassName =
        QualifiedClassNames.DOUBLE.mapWithClassMappings(
            PList.single(
                new ClassTypeMapping("Double", "com.custom.CustomDouble", Optional.empty())));

    assertTrue(maybeClassName.isPresent());
    final QualifiedClassName className = maybeClassName.get();

    assertEquals("CustomDouble", className.getClassName().asString());
    assertEquals("com.custom.CustomDouble", className.asName().asString());
  }

  @Test
  void mapWithClassMappings_when_noMatchingClass_then_emptyMappingReturned() {
    final Optional<QualifiedClassName> maybeClassName =
        QualifiedClassNames.INTEGER.mapWithClassMappings(
            PList.single(
                new ClassTypeMapping("Double", "com.custom.CustomDouble", Optional.empty())));

    assertEquals(Optional.empty(), maybeClassName);
  }

  @Test
  void getClassNameWithGenerics_when_called_then_correctFormatted() {
    final Name classNameWithGenerics =
        QualifiedClassNames.MAP.getClassNameWithGenerics(
            Name.ofString("String"), Name.ofString("UserDto"));
    assertEquals("Map<String, UserDto>", classNameWithGenerics.asString());
  }

  @Test
  void asInnerClassOf_when_calledWithDtoName_then_correctReferenceWithOuterClass() {
    final QualifiedClassName className = QualifiedClassName.ofName("Enum");
    final QualifiedClassName innerClassName =
        className.asInnerClassOf(JavaName.fromString("AdminDto"));
    assertEquals("AdminDto.Enum", innerClassName.getClassName().asString());
  }
}
