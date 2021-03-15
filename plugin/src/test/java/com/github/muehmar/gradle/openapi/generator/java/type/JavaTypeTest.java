package com.github.muehmar.gradle.openapi.generator.java.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.OpenApiPojo;
import io.swagger.v3.oas.models.media.DateSchema;
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
        javaType.getImports().toHashSet());
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
        classReplacedType.getImports().toHashSet());
  }

  @Test
  void ofOpenApiSchema_when_normalData_then_correctTypeReturned() {
    final JavaType javaType = JavaType.ofOpenApiSchema("pojoKey", "key", "Dto", new DateSchema());
    assertEquals("PojoKeyKeyDto", javaType.getName());

    final PList<OpenApiPojo> openApiPojos = javaType.getOpenApiPojos();
    assertEquals(1, openApiPojos.size());
    assertEquals("PojoKeyKey", openApiPojos.head().getKey());
    assertTrue(openApiPojos.head().getSchema() instanceof DateSchema);
  }

  @Test
  void javaEnum_when_normalData_then_correctTypeReturned() {
    final JavaType javaType = JavaType.javaEnum(PList.of("Admin", "User"));
    assertEquals("enum", javaType.getName());
    assertEquals(PList.of("Admin", "User"), javaType.getEnumMembers());
    assertTrue(javaType.isEnum());
  }

  @Test
  void javaList_when_itemIsOpenApiPojos_then_correctNameAndOpenApiPojos() {
    final JavaType itemType = JavaType.ofOpenApiSchema("pojoKey", "key", "Dto", new DateSchema());
    final JavaType javaType = JavaType.javaList(itemType);

    assertEquals("List<PojoKeyKeyDto>", javaType.getName());

    final PList<OpenApiPojo> openApiPojos = javaType.getOpenApiPojos();
    assertEquals(1, openApiPojos.size());
    assertEquals("PojoKeyKey", openApiPojos.head().getKey());
    assertTrue(openApiPojos.head().getSchema() instanceof DateSchema);
  }

  @Test
  void javaMap_when_itemIsOpenApiPojos_then_correctNameAndOpenApiPojos() {
    final JavaType itemType = JavaType.ofOpenApiSchema("pojoKey", "key", "Dto", new DateSchema());
    final JavaType javaType = JavaType.javaMap(JavaTypes.STRING, itemType);

    assertEquals("Map<String, PojoKeyKeyDto>", javaType.getName());

    final PList<OpenApiPojo> openApiPojos = javaType.getOpenApiPojos();
    assertEquals(1, openApiPojos.size());
    assertEquals("PojoKeyKey", openApiPojos.head().getKey());
    assertTrue(openApiPojos.head().getSchema() instanceof DateSchema);
  }
}
