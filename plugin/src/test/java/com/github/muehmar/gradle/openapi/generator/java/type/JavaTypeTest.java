package com.github.muehmar.gradle.openapi.generator.java.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class JavaTypeTest {

  @Test
  void getName_when_genericType_then_correctName() {
    final JavaType javaType =
        JavaType.javaMap(JavaTypes.STRING, JavaType.javaList(JavaTypes.LOCAL_TIME));
    assertEquals("Map<String, List<LocalTime>>", javaType.getName());
  }

  @Test
  void getImports_when_genericType_then_containsAllNecessaryImports() {
    final JavaType javaType =
        JavaType.javaMap(JavaTypes.STRING, JavaType.javaList(JavaTypes.LOCAL_TIME));
    assertEquals(
        new HashSet<>(Arrays.asList("java.util.Map", "java.time.LocalTime", "java.util.List")),
        javaType.getImports());
  }

  @Test
  void replaceClass_when_replaceClass_then_classCorrectReplacedAndImportAdded() {
    final JavaType javaType =
        JavaType.javaMap(JavaTypes.STRING, JavaType.javaList(JavaTypes.LOCAL_TIME));

    final JavaType classReplacedType =
        javaType.replaceClass("List", "CustomList", Optional.of("com.package.CustomList"));

    assertEquals("Map<String, CustomList<LocalTime>>", classReplacedType.getName());

    assertEquals(
        new HashSet<>(
            Arrays.asList("java.util.Map", "java.time.LocalTime", "com.package.CustomList")),
        classReplacedType.getImports());
  }
}
