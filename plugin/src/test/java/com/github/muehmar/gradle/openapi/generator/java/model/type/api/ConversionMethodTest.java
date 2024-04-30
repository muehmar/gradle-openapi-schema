package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static shadow.org.assertj.core.api.Assertions.fail;

import org.junit.jupiter.api.Test;

class ConversionMethodTest {
  @Test
  void ofString_when_factoryMethodString_then_foldExecutesWithFactoryMethod() {
    final ConversionMethod conversionMethod =
        ConversionMethod.ofString("com.github.muehmar.CustomObject#methodName");

    final String result =
        conversionMethod.fold(
            factoryMethodConversion -> factoryMethodConversion.getMethodName().asString(),
            instanceMethodConversion -> fail("Should not be an instance method conversion"));

    assertEquals("methodName", result);
  }

  @Test
  void ofString_when_instanceMethodString_then_foldExecutesWithInstanceMethod() {
    final ConversionMethod conversionMethod = ConversionMethod.ofString("toObject");

    final String result =
        conversionMethod.fold(
            factoryMethodConversion -> fail("Should not be an instance method conversion"),
            instanceMethodConversion -> instanceMethodConversion.getMethodName().asString());

    assertEquals("toObject", result);
  }
}
