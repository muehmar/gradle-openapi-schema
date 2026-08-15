package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaEnumType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class JavaAdditionalPropertiesTest {

  @Test
  void getMapContainerType_when_called_then_correctMapContainerTypeCreated() {
    final JavaAdditionalProperties javaAdditionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.integerType());

    final JavaType mapContainerType = javaAdditionalProperties.getMapContainerType();

    final JavaMapType expectedMapContainerType =
        JavaMapType.ofKeyAndValueType(
            JavaStringType.noFormat(), javaAdditionalProperties.getType());

    assertEquals(expectedMapContainerType, mapContainerType);
  }

  @Test
  void asEnumContent_when_typeIsEnum_then_returnsEnumContentWithCorrectProperties() {
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("PropertyEnum"), PList.of("RED", "GREEN", "BLUE"));
    final JavaType javaEnumType = JavaType.wrap(enumType, TypeMappings.empty());
    final JavaAdditionalProperties javaAdditionalProperties =
        JavaAdditionalProperties.allowedFor(javaEnumType);

    final Optional<EnumGenerator.EnumContent> enumContent =
        javaAdditionalProperties.asEnumContent();

    assertTrue(enumContent.isPresent());
    assertEquals("PropertyEnum", enumContent.get().getClassName().asString());
    assertEquals("Additional property enum", enumContent.get().getDescription());
    assertEquals(
        PList.of("RED", "GREEN", "BLUE"),
        enumContent.get().getMembers().map(EnumConstantName::getOriginalConstant));
  }

  @Test
  void wrap_when_mapOfEnumsValueType_then_throwsAsConversionInContainerIsNotSupported() {
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("Color"), PList.of("red", "green"));
    final AdditionalProperties additionalProperties =
        AdditionalProperties.allowed(MapType.ofKeyAndValueType(StringType.noFormat(), enumType));

    // An enum is represented internally as a String and uses the enum as api type, i.e. it needs a
    // conversion, which is not supported for values nested within a container. See issue #421.
    assertThrows(
        OpenApiGeneratorException.class,
        () ->
            JavaAdditionalProperties.wrap(
                JavaPojoNames.invoiceName(), additionalProperties, TypeMappings.empty()));
  }

  @Test
  void wrap_when_arrayOfEnumsValueType_then_enumKeptAsEnum() {
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("Color"), PList.of("red", "green"));
    final AdditionalProperties additionalProperties =
        AdditionalProperties.allowed(ArrayType.ofItemType(enumType, Nullability.NOT_NULLABLE));

    final JavaAdditionalProperties javaAdditionalProperties =
        JavaAdditionalProperties.wrap(
            JavaPojoNames.invoiceName(), additionalProperties, TypeMappings.empty());

    // The enum is kept as enum, although the conversion of the items is not generated yet and the
    // generated code therefore does not compile. See issue #421.
    final JavaType itemType =
        javaAdditionalProperties
            .getType()
            .onArrayType()
            .orElseThrow(IllegalStateException::new)
            .getItemType();
    assertTrue(itemType instanceof JavaEnumType);
  }

  @Test
  void wrap_when_enumValueType_then_enumKept() {
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("Color"), PList.of("red", "green"));
    final AdditionalProperties additionalProperties = AdditionalProperties.allowed(enumType);

    final JavaAdditionalProperties javaAdditionalProperties =
        JavaAdditionalProperties.wrap(
            JavaPojoNames.invoiceName(), additionalProperties, TypeMappings.empty());

    assertTrue(javaAdditionalProperties.asEnumContent().isPresent());
  }

  @Test
  void asEnumContent_when_typeIsNotEnum_then_returnsEmptyOptional() {
    final JavaAdditionalProperties javaAdditionalProperties =
        JavaAdditionalProperties.allowedFor(JavaTypes.stringType());

    final Optional<EnumGenerator.EnumContent> enumContent =
        javaAdditionalProperties.asEnumContent();

    assertEquals(Optional.empty(), enumContent);
  }
}
