package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
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

    assertEquals(Optional.empty(), javaArrayType.getApiClassName());
    assertEquals(Optional.empty(), javaArrayType.getApiParameterizedClassName());

    assertEquals("List<UUID>", javaArrayType.getInternalParameterizedClassName().asString());
    assertEquals("List", javaArrayType.getInternalClassName().getClassName().asString());
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

    assertEquals(
        Optional.of("CustomList<UUID>"),
        javaArrayType.getApiParameterizedClassName().map(ParameterizedClassName::asString));
    assertEquals(
        Optional.of("CustomList"),
        javaArrayType.getApiClassName().map(cn -> cn.getClassName().asString()));

    assertEquals("List<UUID>", javaArrayType.getInternalParameterizedClassName().asString());
    assertEquals("List", javaArrayType.getInternalClassName().getClassName().asString());

    assertEquals(
        PList.of("com.custom.CustomList", "java.util.List", "java.util.UUID"),
        javaArrayType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
