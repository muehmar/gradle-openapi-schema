package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ToApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
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

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals(className, javaType.getParameterizedClassName().asString());
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
    final IntegerType integerType = IntegerType.ofFormat(IntegerType.Format.LONG, NOT_NULLABLE);

    final JavaIntegerType javaType =
        JavaIntegerType.wrap(
            integerType,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("Long", "com.custom.CustomLong", Optional.empty()),
                TaskIdentifier.fromString("test")));

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("CustomLong", javaType.getParameterizedClassName().asString());
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
    final IntegerType integerType = IntegerType.ofFormat(IntegerType.Format.LONG, NOT_NULLABLE);
    final JavaIntegerType javaType =
        JavaIntegerType.wrap(
            integerType,
            TypeMappings.ofSingleFormatTypeMapping(
                new FormatTypeMapping("int64", "com.custom.CustomLong", Optional.empty()),
                TaskIdentifier.fromString("test")));

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("CustomLong", javaType.getParameterizedClassName().asString());
    assertEquals("CustomLong", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomLong"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_numericTypeWrappedWithFormatMappingAndConversion_then_correctTypeMapped() {
    final IntegerType integerType = IntegerType.ofFormat(IntegerType.Format.LONG, NOT_NULLABLE);
    final TypeConversion typeConversion =
        new TypeConversion("toLong", "com.custom.CustomLong#fromLong");
    final FormatTypeMapping formatTypeMapping =
        new FormatTypeMapping("int64", "com.custom.CustomLong", Optional.of(typeConversion));
    final JavaIntegerType javaType =
        JavaIntegerType.wrap(
            integerType,
            TypeMappings.ofSingleFormatTypeMapping(
                formatTypeMapping, TaskIdentifier.fromString("test")));

    final QualifiedClassName className =
        QualifiedClassName.ofQualifiedClassName("com.custom.CustomLong");

    assertEquals(Optional.of(className), javaType.getApiType().map(ApiType::getClassName));
    assertEquals(
        Optional.of("CustomLong"),
        javaType.getApiType().map(apiType -> apiType.getParameterizedClassName().asString()));
    assertEquals(
        Optional.of(
            PList.single(
                new ToApiTypeConversion(
                    ConversionMethod.ofString(className, typeConversion.getToCustomType())))),
        javaType.getApiType().map(ApiType::getToApiTypeConversion));
    assertEquals(
        Optional.of(
            PList.single(
                new FromApiTypeConversion(
                    ConversionMethod.ofString(className, typeConversion.getFromCustomType())))),
        javaType.getApiType().map(ApiType::getFromApiTypeConversion));

    assertEquals("Long", javaType.getParameterizedClassName().asString());
    assertEquals("Long", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomLong", "java.lang.Long"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
