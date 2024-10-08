package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ToApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
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

class JavaNumericTypeTest {
  @ParameterizedTest
  @MethodSource("numericFormats")
  void wrap_when_doubleFormatWrapped_then_correctWrapped(
      NumericType.Format format, String className) {
    final NumericType numericType = NumericType.ofFormat(format, NOT_NULLABLE);
    final JavaNumericType javaType = JavaNumericType.wrap(numericType, TypeMappings.empty());

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

    assertEquals(Optional.empty(), javaType.getApiType());

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
    final NumericType numericType = NumericType.ofFormat(NumericType.Format.DOUBLE, NOT_NULLABLE);
    final JavaNumericType javaType =
        JavaNumericType.wrap(
            numericType,
            TypeMappings.ofSingleFormatTypeMapping(
                new FormatTypeMapping("double", "com.custom.CustomDouble", Optional.empty())));

    assertEquals(Optional.empty(), javaType.getApiType());

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
  void wrap_when_numericTypeWrappedWithFormatMappingConversion_then_correctTypeMapped() {
    final NumericType numericType = NumericType.ofFormat(NumericType.Format.DOUBLE, NOT_NULLABLE);
    final TypeConversion typeConversion =
        new TypeConversion("toDouble", "com.ucstom.CustomDouble#fromDouble");
    final FormatTypeMapping formatTypeMapping =
        new FormatTypeMapping("double", "com.custom.CustomDouble", Optional.of(typeConversion));
    final JavaNumericType javaType =
        JavaNumericType.wrap(
            numericType, TypeMappings.ofSingleFormatTypeMapping(formatTypeMapping));

    final QualifiedClassName className =
        QualifiedClassName.ofQualifiedClassName("com.custom.CustomDouble");

    assertEquals(Optional.of(className), javaType.getApiType().map(ApiType::getClassName));
    assertEquals(
        Optional.of("CustomDouble"),
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

    assertEquals("Double", javaType.getParameterizedClassName().asString());
    assertEquals("Double", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomDouble", "java.lang.Double"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
