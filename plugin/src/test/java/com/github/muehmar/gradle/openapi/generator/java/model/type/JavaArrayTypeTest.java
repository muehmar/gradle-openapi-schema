package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaArrayTypeTest {
  @Test
  void wrap_when_arrayTypeWrapped_then_correctWrapped() {
    final ArrayType arrayType = ArrayType.ofItemType(StringType.uuid());
    final JavaArrayType javaArrayType = JavaArrayType.wrap(arrayType, TypeMappings.empty());

    assertEquals("List<UUID>", javaArrayType.getFullClassName().asString());
    assertEquals("List", javaArrayType.getClassName().asString());
    assertEquals(
        PList.of("java.util.List", "java.util.UUID"),
        javaArrayType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_arrayTypeWrappedWithClassMapping_then_correctTypeMapped() {
    final ArrayType arrayType = ArrayType.ofItemType(StringType.uuid());
    final JavaArrayType javaArrayType =
        JavaArrayType.wrap(
            arrayType,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("List", "CustomList", "com.custom.CustomList")));

    assertEquals("CustomList<UUID>", javaArrayType.getFullClassName().asString());
    assertEquals("CustomList", javaArrayType.getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomList", "java.util.UUID"),
        javaArrayType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
