package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.FormatTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TypeMappingTest {

  private static final ClassTypeMapping STRING_MAPPING_WITHOUT_CONVERSION =
      new ClassTypeMapping("String", "com.custom.CustomString", Optional.empty());

  private static final TypeConversion STRING_CONVERSION =
      new TypeConversion("toString", "com.custom.CustomString#fromString");
  private static final ClassTypeMapping STRING_MAPPING_WITH_CONVERSION =
      new ClassTypeMapping("String", "com.custom.CustomString", Optional.of(STRING_CONVERSION));

  private static final FormatTypeMapping STRING_FORMAT_MAPPING_WITHOUT_CONVERSION =
      new FormatTypeMapping("id", "com.custom.Id", Optional.empty());

  private static final TypeConversion STRING_ID_CONVERSION =
      new TypeConversion("toString", "com.custom.Id#fromString");
  private static final FormatTypeMapping STRING_FORMAT_MAPPING_WITH_CONVERSION =
      new FormatTypeMapping("id", "com.custom.Id", Optional.of(STRING_ID_CONVERSION));

  @Test
  void fromClassMappings_when_wrongMapping_then_noApiTypeAndCorrectClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            QualifiedClassNames.LONG, PList.of(STRING_MAPPING_WITH_CONVERSION));

    assertEquals(Optional.empty(), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.LONG, typeMapping.getClassName());
  }

  @Test
  void fromClassMappings_when_mappingWithoutConversion_then_noApiTypeAndCorrectClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            QualifiedClassNames.STRING, PList.of(STRING_MAPPING_WITHOUT_CONVERSION));

    assertEquals(Optional.empty(), typeMapping.getApiType());
    assertEquals(
        QualifiedClassName.ofQualifiedClassName("com.custom.CustomString"),
        typeMapping.getClassName());
  }

  @Test
  void fromClassMappings_when_mappingWithConversion_then_correctApiTypeAndClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            QualifiedClassNames.STRING, PList.of(STRING_MAPPING_WITH_CONVERSION));

    final ApiType expectedApiType =
        ApiType.fromConversion(
            QualifiedClassName.ofQualifiedClassName("com.custom.CustomString"),
            STRING_CONVERSION,
            PList.empty());
    assertEquals(Optional.of(expectedApiType), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @Test
  void fromClassMappings_when_mappingWithConversionAndGenerics_then_correctApiTypeAndClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromClassMappings(
            QualifiedClassNames.STRING,
            PList.of(STRING_MAPPING_WITH_CONVERSION),
            PList.of(JavaTypes.integerType()));
    final ApiType expectedApiType =
        ApiType.fromConversion(
            QualifiedClassName.ofQualifiedClassName("com.custom.CustomString"),
            STRING_CONVERSION,
            PList.of(JavaTypes.integerType()));
    assertEquals(Optional.of(expectedApiType), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @Test
  void fromFormatMappings_when_wrongMapping_then_noApiTypeAndCorrectClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromFormatMappings(
            QualifiedClassNames.STRING, "key", PList.of(STRING_FORMAT_MAPPING_WITHOUT_CONVERSION));

    assertEquals(Optional.empty(), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @Test
  void fromFormatMappings_when_mappingWithoutConversion_then_noApiTypeAndCorrectClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromFormatMappings(
            QualifiedClassNames.STRING, "id", PList.of(STRING_FORMAT_MAPPING_WITHOUT_CONVERSION));

    assertEquals(Optional.empty(), typeMapping.getApiType());
    assertEquals(
        QualifiedClassName.ofQualifiedClassName("com.custom.Id"), typeMapping.getClassName());
  }

  @Test
  void fromFormatMappings_when_mappingWithConversion_then_correctApiTypeAndClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromFormatMappings(
            QualifiedClassNames.STRING, "id", PList.of(STRING_FORMAT_MAPPING_WITH_CONVERSION));

    final ApiType expectedApiType =
        ApiType.fromConversion(
            QualifiedClassName.ofQualifiedClassName("com.custom.Id"),
            STRING_ID_CONVERSION,
            PList.empty());

    assertEquals(Optional.of(expectedApiType), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @Test
  void fromFormatMappings_when_mappingWithConversionAndGenerics_then_correctApiTypeAndClassName() {
    final TypeMapping typeMapping =
        TypeMapping.fromFormatMappings(
            QualifiedClassNames.STRING,
            "id",
            PList.of(STRING_FORMAT_MAPPING_WITH_CONVERSION),
            PList.of(JavaTypes.integerType()));

    final ApiType expectedApiType =
        ApiType.fromConversion(
            QualifiedClassName.ofQualifiedClassName("com.custom.Id"),
            STRING_ID_CONVERSION,
            PList.of(JavaTypes.integerType()));

    assertEquals(Optional.of(expectedApiType), typeMapping.getApiType());
    assertEquals(QualifiedClassNames.STRING, typeMapping.getClassName());
  }

  @ParameterizedTest
  @MethodSource("typeMappings")
  void or_when_typeMappings_then_returnExpectedMapping(
      TypeMapping typeMapping1,
      TypeMapping typeMapping2,
      QualifiedClassName originalClassName,
      TypeMapping expectedTypeMapping) {
    final TypeMapping resultingMapping = typeMapping1.or(typeMapping2, originalClassName);

    assertEquals(expectedTypeMapping, resultingMapping);
  }

  public static Stream<Arguments> typeMappings() {
    final TypeMapping longMapping = new TypeMapping(QualifiedClassNames.LONG, Optional.empty());
    final TypeMapping stringMapping = new TypeMapping(QualifiedClassNames.STRING, Optional.empty());

    final ApiType apiType =
        ApiType.fromConversion(
            QualifiedClassName.ofQualifiedClassName("com.custom.Id"),
            STRING_ID_CONVERSION,
            PList.empty());

    final TypeMapping stringMappingApiType =
        new TypeMapping(QualifiedClassNames.STRING, Optional.of(apiType));

    return Stream.of(
        arguments(longMapping, stringMapping, QualifiedClassNames.STRING, longMapping),
        arguments(stringMapping, longMapping, QualifiedClassNames.STRING, longMapping),
        arguments(
            stringMappingApiType, longMapping, QualifiedClassNames.STRING, stringMappingApiType),
        arguments(
            stringMappingApiType, longMapping, QualifiedClassNames.LONG, stringMappingApiType),
        arguments(
            longMapping, stringMappingApiType, QualifiedClassNames.STRING, stringMappingApiType),
        arguments(
            longMapping, stringMappingApiType, QualifiedClassNames.LONG, stringMappingApiType));
  }
}
