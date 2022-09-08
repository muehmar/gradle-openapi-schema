package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
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

    assertEquals(className, javaType.getFullClassName().asString());
    assertEquals(className, javaType.getClassName().asString());
    assertEquals(
        PList.of("java.lang." + className),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  public static Stream<Arguments> numericFormats() {
    return Stream.of(
        Arguments.of(NumericType.Format.DOUBLE, "Double"),
        Arguments.of(NumericType.Format.FLOAT, "Float"),
        Arguments.of(NumericType.Format.LONG, "Long"),
        Arguments.of(NumericType.Format.INTEGER, "Integer"));
  }

  @Test
  void wrap_when_numericTypeWrappedWithClassMapping_then_correctTypeMapped() {
    final NumericType numericType = NumericType.ofFormat(NumericType.Format.DOUBLE);
    final JavaNumericType javaType =
        JavaNumericType.wrap(
            numericType,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("Double", "CustomDouble", "com.custom.CustomDouble")));

    assertEquals("CustomDouble", javaType.getFullClassName().asString());
    assertEquals("CustomDouble", javaType.getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomDouble"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_numericTypeWrappedWithFormatMapping_then_correctTypeMapped() {
    final NumericType numericType = NumericType.ofFormat(NumericType.Format.DOUBLE);
    final JavaNumericType javaType =
        JavaNumericType.wrap(
            numericType,
            TypeMappings.ofSingleFormatTypeMapping(
                new FormatTypeMapping("double", "CustomDouble", "com.custom.CustomDouble")));

    assertEquals("CustomDouble", javaType.getFullClassName().asString());
    assertEquals("CustomDouble", javaType.getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomDouble"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void asPrimitive_when_called_then_mappedToPrimitiveClassName() {
    final JavaType javaType = JavaTypes.INTEGER.asPrimitive();

    assertNotEquals(JavaTypes.INTEGER, javaType);
    assertEquals("int", javaType.getClassName().asString());
  }
}
