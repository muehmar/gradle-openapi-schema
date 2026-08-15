package com.github.muehmar.gradle.openapi.generator.java.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.java.model.type.NonGenericJavaType;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
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
    final NonGenericJavaType javaEnumType = JavaType.wrap(enumType, TypeMappings.empty());
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

  /**
   * A container as additional-property value type is mapped to a dedicated pojo and referenced as
   * object type (see {@code AdditionalPropertiesSchema#mapAdditionalPropertiesSchema}), hence the
   * value type is never a container. This is ensured by the type {@link
   * com.github.muehmar.gradle.openapi.generator.model.type.AdditionalPropertiesValueType}: passing
   * an {@code ArrayType} or {@code MapType} to {@link AdditionalProperties#allowed} does not
   * compile, therefore no test for a container value type exists here.
   */
  @Test
  void wrap_when_objectValueType_then_objectTypeKept() {
    final AdditionalProperties additionalProperties =
        AdditionalProperties.allowed(
            StandardObjectType.ofName(PojoName.ofName(Name.ofString("InvoicePropertyDto"))));

    final JavaAdditionalProperties javaAdditionalProperties =
        JavaAdditionalProperties.wrap(additionalProperties, TypeMappings.empty());

    assertEquals(
        "InvoicePropertyDto",
        javaAdditionalProperties.getType().getQualifiedClassName().getClassName().asString());
    assertEquals(Optional.empty(), javaAdditionalProperties.asEnumContent());
  }

  @Test
  void wrap_when_enumValueType_then_enumKept() {
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("Color"), PList.of("red", "green"));
    final AdditionalProperties additionalProperties = AdditionalProperties.allowed(enumType);

    final JavaAdditionalProperties javaAdditionalProperties =
        JavaAdditionalProperties.wrap(additionalProperties, TypeMappings.empty());

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
