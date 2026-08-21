package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import org.junit.jupiter.api.Test;
import shadow.org.assertj.core.api.Assertions;

class UserDefinedApiTypeTest {
  @Test
  void fromConversion_when_called_then_correctApiType() {
    final TypeConversion typeConversion = new TypeConversion("toString", "fromString");

    final UserDefinedApiType userDefinedApiType =
        UserDefinedApiType.fromConversion(
            QualifiedClassNames.LIST, typeConversion, PList.of(JavaTypes.integerType()));

    assertEquals("java.util.List", userDefinedApiType.getClassName().asString());
    assertEquals("List<Integer>", userDefinedApiType.getParameterizedClassName().asString());
    assertEquals(
        "fromString",
        userDefinedApiType
            .getToApiTypeConversion()
            .fold(
                factoryMethodConversion -> fail("Should not be a factory method"),
                instanceMethodConversion -> instanceMethodConversion.getMethodName().asString(),
                constructorConversion ->
                    Assertions.fail("Should not be an constructor conversion")));
    assertEquals(
        "toString",
        userDefinedApiType
            .getFromApiTypeConversion()
            .fold(
                factoryMethodConversion -> fail("Should not be a factory method"),
                instanceMethodConversion -> instanceMethodConversion.getMethodName().asString(),
                constructorConversion ->
                    Assertions.fail("Should not be an constructor conversion")));
  }
}
