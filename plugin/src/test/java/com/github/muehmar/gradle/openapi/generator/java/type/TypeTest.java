package com.github.muehmar.gradle.openapi.generator.java.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class TypeTest {

  @Test
  void getName_when_genericType_then_correctName() {
    final JavaType javaType =
        JavaType.javaMap(JavaTypes.STRING, JavaType.javaList(JavaTypes.LOCAL_TIME));
    assertEquals("Map<String, List<LocalTime>>", javaType.getFullName());
  }

  @Test
  void getImports_when_genericType_then_containsAllNecessaryImports() {
    final JavaType javaType =
        JavaType.javaMap(JavaTypes.STRING, JavaType.javaList(JavaTypes.LOCAL_TIME));
    assertEquals(
        new HashSet<>(Arrays.asList("java.util.Map", "java.time.LocalTime", "java.util.List")),
        javaType.getImports().toHashSet());
  }

  @Test
  void replaceClass_when_replaceClass_then_classCorrectReplacedAndImportAdded() {
    final JavaType javaType =
        JavaType.javaMap(JavaTypes.STRING, JavaType.javaList(JavaTypes.LOCAL_TIME));

    final JavaType classReplacedType =
        javaType.replaceClass("List", "CustomList", Optional.of("com.package.CustomList"));

    assertEquals("Map<String, CustomList<LocalTime>>", classReplacedType.getFullName());

    assertEquals(
        new HashSet<>(
            Arrays.asList("java.util.Map", "java.time.LocalTime", "com.package.CustomList")),
        classReplacedType.getImports().toHashSet());
    assertTrue(classReplacedType.containsPojo());
  }

  @Test
  void ofOpenApiSchema_when_normalData_then_correctName() {
    final JavaType javaType = JavaType.ofOpenApiSchema("PojoKeyKey", "Dto");
    assertEquals("PojoKeyKeyDto", javaType.getFullName());
    assertTrue(javaType.containsPojo());
  }

  @Test
  void javaEnum_when_normalData_then_correctTypeReturned() {
    final JavaType javaType = JavaType.javaEnum(PList.of("Admin", "User"));
    assertEquals("enum", javaType.getFullName());
    assertEquals(PList.of("Admin", "User"), javaType.getEnumMembers());
    assertTrue(javaType.isEnum());
  }
}
