package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class FactoryMethodConversionTest {

  @Test
  void fromString_when_correctFactoryMethodString_then_correctFactoryMethodConversionClass() {
    final QualifiedClassName className =
        QualifiedClassName.ofQualifiedClassName("com.github.muehmar.CustomObject");
    final Optional<FactoryMethodConversion> factoryMethodConversion =
        FactoryMethodConversion.fromString(className, "com.github.muehmar.CustomObject#methodName");

    final FactoryMethodConversion expectedFactoryMethodConversion =
        new FactoryMethodConversion(className, Name.ofString("methodName"));

    assertEquals(Optional.of(expectedFactoryMethodConversion), factoryMethodConversion);
  }

  @Test
  void
      fromString_when_unqualifiedFactorMethodString_then_useQualifiedClassInFactoryMethodConversionClass() {
    final QualifiedClassName className =
        QualifiedClassName.ofQualifiedClassName("com.github.muehmar.CustomObject");
    final Optional<FactoryMethodConversion> factoryMethodConversion =
        FactoryMethodConversion.fromString(className, "CustomObject#methodName");

    final FactoryMethodConversion expectedFactoryMethodConversion =
        new FactoryMethodConversion(className, Name.ofString("methodName"));

    assertEquals(Optional.of(expectedFactoryMethodConversion), factoryMethodConversion);
  }

  @ParameterizedTest
  @ValueSource(
      strings = {
        "com.github.muehmar#CustomObject#methodName",
        "com.github.muehmar.CustomObject# ",
        "com.github.muehmar.CustomObject#",
        " #methodName",
        "#methodName"
      })
  void fromString_when_invalidFactoryMethodString_then_throwsException(
      String factoryMethodConversion) {
    assertThrows(
        OpenApiGeneratorException.class,
        () ->
            FactoryMethodConversion.fromString(
                QualifiedClassName.ofQualifiedClassName("Clazz"), factoryMethodConversion));
  }

  @ParameterizedTest
  @ValueSource(strings = {"methodName", "invalid.MethodName"})
  void fromString_when_stringIsNotFactoryMethod_then_returnEmptyOptional(
      String factoryMethodConversionString) {
    final Optional<FactoryMethodConversion> factoryMethodConversion =
        FactoryMethodConversion.fromString(
            QualifiedClassName.ofQualifiedClassName("Clazz"), factoryMethodConversionString);

    assertEquals(Optional.empty(), factoryMethodConversion);
  }
}
