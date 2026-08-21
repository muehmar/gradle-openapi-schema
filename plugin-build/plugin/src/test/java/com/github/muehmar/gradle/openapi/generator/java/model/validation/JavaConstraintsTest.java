package com.github.muehmar.gradle.openapi.generator.java.model.validation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JavaConstraintsTest {

  @ParameterizedTest
  @MethodSource("javaTypeAndConstraintTypeCombinations")
  void isSupported_when_javaTypeAndConstraintTypeCombinations_then_matchExpectedSupported(
      JavaType javaType, ConstraintType type, boolean expectedSupported) {
    assertEquals(expectedSupported, JavaConstraints.isSupported(javaType, type));
  }

  public static Stream<Arguments> javaTypeAndConstraintTypeCombinations() {
    return Stream.of(
        arguments(JavaTypes.stringType(), ConstraintType.EMAIL, true),
        arguments(JavaTypes.stringType(), ConstraintType.MIN, false),
        arguments(JavaTypes.integerType(), ConstraintType.MIN, true),
        arguments(JavaTypes.integerType(), ConstraintType.EMAIL, false));
  }
}
