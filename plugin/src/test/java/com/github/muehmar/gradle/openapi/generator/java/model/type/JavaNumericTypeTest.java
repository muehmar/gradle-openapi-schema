package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class JavaNumericTypeTest {
  @ParameterizedTest
  @MethodSource("numericFormats")
  void wrap_when_doubleFormatWrapped_then_correctWrapped(
      NumericType.Format format, String className) {
    final NumericType numericType = NumericType.ofFormat(format);
    final JavaNumericType javaType = JavaNumericType.wrap(numericType, TypeMappings.empty());

    assertEquals(className, javaType.getParameterizedClassName().asString());
    assertEquals(className, javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("java.lang." + className),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  public static Stream<Arguments> numericFormats() {
    return Stream.of(
        Arguments.of(NumericType.Format.DOUBLE, "Double"),
        Arguments.of(NumericType.Format.FLOAT, "Float"));
  }

  @Test
  void wrap_when_numericTypeWrappedWithClassMapping_then_correctTypeMapped() {
    final NumericType numericType = NumericType.ofFormat(NumericType.Format.DOUBLE);
    final JavaNumericType javaType =
        JavaNumericType.wrap(
            numericType,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("Double", "com.custom.CustomDouble")));

    assertEquals("CustomDouble", javaType.getParameterizedClassName().asString());
    assertEquals("CustomDouble", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomDouble"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_numericTypeWrappedWithFormatMapping_then_correctTypeMapped() {
    final NumericType numericType = NumericType.ofFormat(NumericType.Format.DOUBLE);
    final JavaNumericType javaType =
        JavaNumericType.wrap(
            numericType,
            TypeMappings.ofSingleFormatTypeMapping(
                new FormatTypeMapping("double", "com.custom.CustomDouble")));

    assertEquals("CustomDouble", javaType.getParameterizedClassName().asString());
    assertEquals("CustomDouble", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomDouble"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
