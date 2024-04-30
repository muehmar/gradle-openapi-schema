package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ToApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaArrayTypeTest {
  @Test
  void wrap_when_arrayTypeWrapped_then_correctWrapped() {
    final ArrayType arrayType = ArrayType.ofItemType(StringType.uuid(), NOT_NULLABLE);
    final JavaArrayType javaArrayType = JavaArrayType.wrap(arrayType, TypeMappings.empty());

    assertEquals(Optional.empty(), javaArrayType.getApiType());

    assertEquals("List<UUID>", javaArrayType.getParameterizedClassName().asString());
    assertEquals("List", javaArrayType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("java.util.List", "java.util.UUID"),
        javaArrayType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_arrayTypeWrappedWithClassMapping_then_correctTypeMapped() {
    final ArrayType arrayType = ArrayType.ofItemType(StringType.uuid(), NOT_NULLABLE);
    final JavaArrayType javaArrayType =
        JavaArrayType.wrap(
            arrayType,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("List", "com.custom.CustomList", Optional.empty())));

    assertEquals(Optional.empty(), javaArrayType.getApiType());

    assertEquals("CustomList<UUID>", javaArrayType.getParameterizedClassName().asString());
    assertEquals("CustomList", javaArrayType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomList", "java.util.UUID"),
        javaArrayType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_arrayTypeWrappedWithClassMappingAndConversion_then_correctTypeMapped() {
    final ArrayType arrayType = ArrayType.ofItemType(StringType.uuid(), NOT_NULLABLE);
    final TypeConversion typeConversion =
        new TypeConversion("toList", "com.custom.CustomList#fromList");
    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("List", "com.custom.CustomList", Optional.of(typeConversion));
    final JavaArrayType javaArrayType =
        JavaArrayType.wrap(arrayType, TypeMappings.ofSingleClassTypeMapping(classTypeMapping));

    assertEquals(
        Optional.of("com.custom.CustomList"),
        javaArrayType.getApiType().map(apiType -> apiType.getClassName().asString()));
    assertEquals(
        Optional.of("CustomList<UUID>"),
        javaArrayType.getApiType().map(apiType -> apiType.getParameterizedClassName().asString()));
    assertEquals(
        Optional.of(
            new ToApiTypeConversion(ConversionMethod.ofString(typeConversion.getToCustomType()))),
        javaArrayType.getApiType().map(ApiType::getToApiTypeConversion));
    assertEquals(
        Optional.of(
            new FromApiTypeConversion(
                ConversionMethod.ofString(typeConversion.getFromCustomType()))),
        javaArrayType.getApiType().map(ApiType::getFromApiTypeConversion));

    assertEquals("List<UUID>", javaArrayType.getParameterizedClassName().asString());
    assertEquals("List", javaArrayType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomList", "java.util.List", "java.util.UUID"),
        javaArrayType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
