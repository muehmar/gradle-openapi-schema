package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import org.junit.jupiter.api.Test;

class ApiTypeTest {
  @Test
  void fromConversion_when_called_then_correctApiType() {
    final TypeConversion typeConversion = new TypeConversion("toString", "fromString");

    final ApiType apiType =
        ApiType.fromConversion(
            QualifiedClassNames.LIST, typeConversion, PList.of(JavaTypes.integerType()));

    assertEquals("java.util.List", apiType.getClassName().asString());
    assertEquals("List<Integer>", apiType.getParameterizedClassName().asString());
    assertEquals(
        "fromString",
        apiType
            .getToApiTypeConversion()
            .fold(
                factoryMethodConversion -> fail("Should not be a factory method"),
                instanceMethodConversion -> instanceMethodConversion.getMethodName().asString()));
    assertEquals(
        "toString",
        apiType
            .getFromApiTypeConversion()
            .fold(
                factoryMethodConversion -> fail("Should not be a factory method"),
                instanceMethodConversion -> instanceMethodConversion.getMethodName().asString()));
  }
}
