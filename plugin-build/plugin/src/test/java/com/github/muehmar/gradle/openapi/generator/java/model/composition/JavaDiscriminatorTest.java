package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.Collections;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JavaDiscriminatorTest {
  @ParameterizedTest
  @MethodSource("schemaNamesAndExpectedStringValues")
  void getStringValueForSchemaName_when_schemaName_then_matchExpectedStringValue(
      Name schemaName, String expectedStringValue) {
    final JavaDiscriminator discriminator =
        new JavaDiscriminator(
            JavaName.fromString("prop"),
            JavaDiscriminatorType.fromStringType(JavaTypes.stringType()),
            Optional.of(Collections.singletonMap("-user-", Name.ofString("User"))));

    final String stringValue = discriminator.getStringValueForSchemaName(schemaName);

    assertEquals(expectedStringValue, stringValue);
  }

  @ParameterizedTest
  @MethodSource("schemaNamesAndExpectedStringValues")
  void getValueForSchemaName_when_stringType_then_stringTypeMappingExecutedWithCorrectValue(
      Name schemaName, String expectedStringValue) {
    final JavaDiscriminator discriminator =
        new JavaDiscriminator(
            JavaName.fromString("prop"),
            JavaDiscriminatorType.fromStringType(JavaTypes.stringType()),
            Optional.of(Collections.singletonMap("-user-", Name.ofString("User"))));

    final String stringValue =
        discriminator.getValueForSchemaName(
            schemaName,
            stringType -> stringType + "String",
            enumName -> "Gender." + enumName.asString());

    assertEquals(expectedStringValue + "String", stringValue);
  }

  public static Stream<Arguments> schemaNamesAndExpectedStringValues() {
    return Stream.of(
        arguments(Name.ofString("User"), "-user-"), arguments(Name.ofString("Client"), "Client"));
  }

  @Test
  void getValueForSchemaName_when_enumType_then_enumTypeMappingExecutedWithCorrectEnumValue() {
    final JavaDiscriminator discriminator =
        new JavaDiscriminator(
            JavaName.fromString("prop"),
            JavaDiscriminatorType.fromEnumType(JavaTypes.enumType()),
            Optional.of(Collections.singletonMap("male", Name.ofString("Male"))));

    final String stringValue =
        discriminator.getValueForSchemaName(
            Name.ofString("Male"),
            stringType -> stringType + "String",
            enumName -> "Gender." + enumName.asString());

    assertEquals("Gender.MALE", stringValue);
  }

  @Test
  void getValueForSchemaName_when_enumTypeButInvalidMapping_then_throws() {
    final JavaDiscriminator discriminator =
        new JavaDiscriminator(
            JavaName.fromString("prop"),
            JavaDiscriminatorType.fromEnumType(JavaTypes.enumType()),
            Optional.of(Collections.singletonMap("male", Name.ofString("Male"))));

    assertThrows(
        OpenApiGeneratorException.class,
        () ->
            discriminator.getValueForSchemaName(
                Name.ofString("Female"),
                stringType -> stringType + "String",
                enumName -> "Gender." + enumName.asString()));
  }
}
