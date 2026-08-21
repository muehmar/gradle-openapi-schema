package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ParameterizedApiClassNameTest {

  @Test
  void
      ofClassNameAndGenerics_when_javaMapTypeWithMappedString_then_asStringReturnsCorrectFormattedType() {
    final JavaType mappedStringType =
        JavaType.wrap(
            StringType.noFormat(),
            TypeMappings.ofSingleClassTypeMapping(
                ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION));
    final JavaMapType javaMapType =
        JavaMapType.ofKeyAndValueType(JavaTypes.stringType(), mappedStringType);

    final Optional<ParameterizedApiClassName> parameterizedApiClassName =
        ParameterizedApiClassName.fromJavaType(javaMapType);

    assertEquals(
        Optional.of("Map<String, CustomString>"),
        parameterizedApiClassName.map(ParameterizedApiClassName::asString));
  }

  @Test
  void
      ofClassNameAndGenerics_when_javaMapTypeWithMappedString_then_asStringWrappingNullableValueTypeReturnsCorrectFormattedType() {
    final JavaType mappedStringType =
        JavaType.wrap(
                StringType.noFormat(),
                TypeMappings.ofSingleClassTypeMapping(
                    ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION))
            .withNullability(NULLABLE);
    final JavaMapType javaMapType =
        JavaMapType.ofKeyAndValueType(JavaTypes.stringType(), mappedStringType);

    final Optional<ParameterizedApiClassName> parameterizedApiClassName =
        ParameterizedApiClassName.fromJavaType(javaMapType);

    assertEquals(
        Optional.of("Map<String, Optional<CustomString>>"),
        parameterizedApiClassName.map(
            ParameterizedApiClassName::asStringWrappingNullableValueType));
  }

  @Test
  void
      ofClassNameAndGenerics_when_javaArrayTypeWithMappedString_then_asStringReturnsCorrectFormattedType() {
    final ArrayType arrayType = ArrayType.ofItemType(StringType.noFormat(), NOT_NULLABLE);
    final JavaArrayType javaArrayType =
        JavaArrayType.wrap(
            arrayType,
            TypeMappings.ofSingleClassTypeMapping(
                ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION));

    final Optional<ParameterizedApiClassName> parameterizedApiClassName =
        ParameterizedApiClassName.fromJavaType(javaArrayType);

    assertEquals(
        Optional.of("List<CustomString>"),
        parameterizedApiClassName.map(ParameterizedApiClassName::asString));
  }

  @Test
  void
      ofClassNameAndGenerics_when_javaArrayTypeWithMappedString_then_asStringWrappingNullableValueTypeReturnsCorrectFormattedType() {
    final ArrayType arrayType =
        ArrayType.ofItemType(StringType.noFormat().withNullability(NULLABLE), NOT_NULLABLE);
    final JavaArrayType javaArrayType =
        JavaArrayType.wrap(
            arrayType,
            TypeMappings.ofSingleClassTypeMapping(
                ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION));

    final Optional<ParameterizedApiClassName> parameterizedApiClassName =
        ParameterizedApiClassName.fromJavaType(javaArrayType);

    assertEquals(
        Optional.of("List<Optional<CustomString>>"),
        parameterizedApiClassName.map(
            ParameterizedApiClassName::asStringWrappingNullableValueType));
  }

  @Test
  void
      ofClassNameAndGenerics_when_javaArrayTypeWithMappedListAndString_then_asStringReturnsCorrectFormattedType() {
    final ArrayType arrayType = ArrayType.ofItemType(StringType.noFormat(), NOT_NULLABLE);
    final JavaArrayType javaArrayType =
        JavaArrayType.wrap(
            arrayType,
            TypeMappings.ofClassTypeMappings(
                ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION,
                ClassTypeMappings.LIST_MAPPING_WITH_CONVERSION));

    final Optional<ParameterizedApiClassName> parameterizedApiClassName =
        ParameterizedApiClassName.fromJavaType(javaArrayType);

    assertEquals(
        Optional.of("CustomList<CustomString>"),
        parameterizedApiClassName.map(ParameterizedApiClassName::asString));
  }

  @Test
  void ofClassNameAndGenerics_when_mappedStringType_then_asStringReturnsCorrectFormattedType() {
    final JavaType mappedStringType =
        JavaType.wrap(
            StringType.noFormat(),
            TypeMappings.ofSingleClassTypeMapping(
                ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION));

    final Optional<ParameterizedApiClassName> parameterizedApiClassName =
        ParameterizedApiClassName.fromJavaType(mappedStringType);

    assertEquals(
        Optional.of("CustomString"),
        parameterizedApiClassName.map(ParameterizedApiClassName::asString));
  }
}
