package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.Optional;
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
    final NumericType numericType = NumericType.ofFormat(format, NOT_NULLABLE);
    final JavaNumericType javaType = JavaNumericType.wrap(numericType, TypeMappings.empty());

    assertEquals(Optional.empty(), javaType.getApiClassName());
    assertEquals(Optional.empty(), javaType.getApiParameterizedClassName());

    assertEquals(className, javaType.getInternalParameterizedClassName().asString());
    assertEquals(className, javaType.getInternalClassName().getClassName().asString());
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
    final NumericType numericType = NumericType.ofFormat(NumericType.Format.DOUBLE, NOT_NULLABLE);
    final JavaNumericType javaType =
        JavaNumericType.wrap(
            numericType,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("Double", "com.custom.CustomDouble", Optional.empty())));

    assertEquals(
        Optional.of("CustomDouble"),
        javaType.getApiClassName().map(cn -> cn.getClassName().asString()));
    assertEquals(
        Optional.of("CustomDouble"),
        javaType.getApiParameterizedClassName().map(ParameterizedClassName::asString));

    assertEquals("Double", javaType.getInternalParameterizedClassName().asString());
    assertEquals("Double", javaType.getInternalClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomDouble", "java.lang.Double"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_numericTypeWrappedWithFormatMapping_then_correctTypeMapped() {
    final NumericType numericType = NumericType.ofFormat(NumericType.Format.DOUBLE, NOT_NULLABLE);
    final JavaNumericType javaType =
        JavaNumericType.wrap(
            numericType,
            TypeMappings.ofSingleFormatTypeMapping(
                new FormatTypeMapping("double", "com.custom.CustomDouble", Optional.empty())));

    assertEquals("Double", javaType.getInternalParameterizedClassName().asString());
    assertEquals("Double", javaType.getInternalClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomDouble", "java.lang.Double"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
