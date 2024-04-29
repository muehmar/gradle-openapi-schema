package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
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

class JavaStringTypeTest {
  @ParameterizedTest
  @MethodSource("stringFormats")
  void wrap_when_stringFormatWrapped_then_correctWrapped(
      StringType.Format format, String className, String qualifiedClassName) {
    final StringType stringType = StringType.ofFormat(format);
    final JavaStringType javaType = JavaStringType.wrap(stringType, TypeMappings.empty());

    assertEquals(Optional.empty(), javaType.getApiClassName());
    assertEquals(Optional.empty(), javaType.getApiParameterizedClassName());

    assertEquals(className, javaType.getInternalParameterizedClassName().asString());
    assertEquals(className, javaType.getInternalClassName().getClassName().asString());
    assertEquals(
        PList.of(qualifiedClassName),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  public static Stream<Arguments> stringFormats() {
    return Stream.of(
        Arguments.of(StringType.Format.DATE, "LocalDate", "java.time.LocalDate"),
        Arguments.of(StringType.Format.DATE_TIME, "LocalDateTime", "java.time.LocalDateTime"),
        Arguments.of(StringType.Format.TIME, "LocalTime", "java.time.LocalTime"),
        Arguments.of(StringType.Format.URI, "URI", "java.net.URI"),
        Arguments.of(StringType.Format.URL, "URL", "java.net.URL"),
        Arguments.of(StringType.Format.UUID, "UUID", "java.util.UUID"),
        Arguments.of(StringType.Format.BINARY, "byte[]", "java.lang.byte[]"));
  }

  @Test
  void wrap_when_stringTypeWrappedWithClassMapping_then_correctTypeMapped() {
    final StringType stringType = StringType.ofFormat(StringType.Format.UUID);
    final JavaStringType javaType =
        JavaStringType.wrap(
            stringType,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("UUID", "com.custom.CustomUUID", Optional.empty())));

    assertEquals(
        Optional.of("CustomUUID"),
        javaType.getApiClassName().map(cn -> cn.getClassName().asString()));
    assertEquals(
        Optional.of("CustomUUID"),
        javaType.getApiParameterizedClassName().map(ParameterizedClassName::asString));

    assertEquals("UUID", javaType.getInternalParameterizedClassName().asString());
    assertEquals("UUID", javaType.getInternalClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomUUID", "java.util.UUID"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_stringTypeWrappedWithFormatMapping_then_correctTypeMapped() {
    final StringType stringType = StringType.ofFormat(StringType.Format.BINARY);
    final JavaStringType javaType =
        JavaStringType.wrap(
            stringType,
            TypeMappings.ofSingleFormatTypeMapping(
                new FormatTypeMapping("binary", "com.custom.CustomBinary", Optional.empty())));

    assertEquals(
        Optional.of("CustomBinary"),
        javaType.getApiClassName().map(cn -> cn.getClassName().asString()));
    assertEquals(
        Optional.of("CustomBinary"),
        javaType.getApiParameterizedClassName().map(ParameterizedClassName::asString));

    assertEquals("byte[]", javaType.getInternalParameterizedClassName().asString());
    assertEquals("byte[]", javaType.getInternalClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomBinary", "java.lang.byte[]"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
