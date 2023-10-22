package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
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

class JavaIntegerTypeTest {
  @ParameterizedTest
  @MethodSource("integerFormats")
  void wrap_when_integerFormatWrapped_then_correctWrapped(
      IntegerType.Format format, String className) {
    final IntegerType integerType = IntegerType.ofFormat(format);
    final JavaIntegerType javaType = JavaIntegerType.wrap(integerType, TypeMappings.empty());

    assertEquals(className, javaType.getFullClassName().asString());
    assertEquals(className, javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("java.lang." + className),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  public static Stream<Arguments> integerFormats() {
    return Stream.of(
        Arguments.of(IntegerType.Format.INTEGER, "Integer"),
        Arguments.of(IntegerType.Format.LONG, "Long"));
  }

  @Test
  void wrap_when_numericTypeWrappedWithClassMapping_then_correctTypeMapped() {
    final IntegerType integerType = IntegerType.ofFormat(IntegerType.Format.LONG);
    final JavaIntegerType javaType =
        JavaIntegerType.wrap(
            integerType,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("Long", "com.custom.CustomLong")));

    assertEquals("CustomLong", javaType.getFullClassName().asString());
    assertEquals("CustomLong", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomLong"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_numericTypeWrappedWithFormatMapping_then_correctTypeMapped() {
    final IntegerType integerType = IntegerType.ofFormat(IntegerType.Format.LONG);
    final JavaIntegerType javaType =
        JavaIntegerType.wrap(
            integerType,
            TypeMappings.ofSingleFormatTypeMapping(
                new FormatTypeMapping("int64", "com.custom.CustomLong")));

    assertEquals("CustomLong", javaType.getFullClassName().asString());
    assertEquals("CustomLong", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomLong"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
