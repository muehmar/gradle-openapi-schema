package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
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

class JavaIntegerTypeTest {
  @ParameterizedTest
  @MethodSource("integerFormats")
  void wrap_when_integerFormatWrapped_then_correctWrapped(
      IntegerType.Format format, String className) {
    final IntegerType integerType = IntegerType.ofFormat(format, NOT_NULLABLE);
    final JavaIntegerType javaType = JavaIntegerType.wrap(integerType, TypeMappings.empty());

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

  public static Stream<Arguments> integerFormats() {
    return Stream.of(
        Arguments.of(IntegerType.Format.INTEGER, "Integer"),
        Arguments.of(IntegerType.Format.LONG, "Long"));
  }

  @Test
  void wrap_when_numericTypeWrappedWithClassMapping_then_correctTypeMapped() {
    final IntegerType integerType = IntegerType.ofFormat(IntegerType.Format.LONG, NOT_NULLABLE);
    final JavaIntegerType javaType =
        JavaIntegerType.wrap(
            integerType,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("Long", "com.custom.CustomLong", Optional.empty())));

    assertEquals(
        Optional.of("CustomLong"),
        javaType.getApiClassName().map(cn -> cn.getClassName().asString()));
    assertEquals(
        Optional.of("CustomLong"),
        javaType.getApiParameterizedClassName().map(ParameterizedClassName::asString));

    assertEquals("Long", javaType.getInternalParameterizedClassName().asString());
    assertEquals("Long", javaType.getInternalClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomLong", "java.lang.Long"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_numericTypeWrappedWithFormatMapping_then_correctTypeMapped() {
    final IntegerType integerType = IntegerType.ofFormat(IntegerType.Format.LONG, NOT_NULLABLE);
    final JavaIntegerType javaType =
        JavaIntegerType.wrap(
            integerType,
            TypeMappings.ofSingleFormatTypeMapping(
                new FormatTypeMapping("int64", "com.custom.CustomLong", Optional.empty())));

    assertEquals(
        Optional.of("CustomLong"),
        javaType.getApiClassName().map(cn -> cn.getClassName().asString()));
    assertEquals(
        Optional.of("CustomLong"),
        javaType.getApiParameterizedClassName().map(ParameterizedClassName::asString));

    assertEquals("Long", javaType.getInternalParameterizedClassName().asString());
    assertEquals("Long", javaType.getInternalClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomLong", "java.lang.Long"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
