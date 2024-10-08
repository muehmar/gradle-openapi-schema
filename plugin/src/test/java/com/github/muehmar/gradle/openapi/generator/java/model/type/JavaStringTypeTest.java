package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ToApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
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

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals(className, javaType.getParameterizedClassName().asString());
    assertEquals(className, javaType.getQualifiedClassName().getClassName().asString());
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

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("CustomUUID", javaType.getParameterizedClassName().asString());
    assertEquals("CustomUUID", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomUUID"),
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

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("CustomBinary", javaType.getParameterizedClassName().asString());
    assertEquals("CustomBinary", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomBinary"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_stringTypeWrappedWithFormatMappingAndConversion_then_correctTypeMapped() {
    final StringType stringType = StringType.ofFormat(StringType.Format.BINARY);
    final TypeConversion typeConversion =
        new TypeConversion("toBytes", "com.custom.CustomBinary#fromBytes");
    final FormatTypeMapping formatTypeMapping =
        new FormatTypeMapping("binary", "com.custom.CustomBinary", Optional.of(typeConversion));
    final JavaStringType javaType =
        JavaStringType.wrap(stringType, TypeMappings.ofSingleFormatTypeMapping(formatTypeMapping));

    final QualifiedClassName className =
        QualifiedClassName.ofQualifiedClassName("com.custom.CustomBinary");

    assertEquals(Optional.of(className), javaType.getApiType().map(ApiType::getClassName));
    assertEquals(
        Optional.of("CustomBinary"),
        javaType.getApiType().map(apiType -> apiType.getParameterizedClassName().asString()));
    assertEquals(
        Optional.of(
            new ToApiTypeConversion(
                ConversionMethod.ofString(className, typeConversion.getToCustomType()))),
        javaType.getApiType().map(ApiType::getToApiTypeConversion));
    assertEquals(
        Optional.of(
            new FromApiTypeConversion(
                ConversionMethod.ofString(className, typeConversion.getFromCustomType()))),
        javaType.getApiType().map(ApiType::getFromApiTypeConversion));

    assertEquals("byte[]", javaType.getParameterizedClassName().asString());
    assertEquals("byte[]", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomBinary", "java.lang.byte[]"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
