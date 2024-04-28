package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaMapTypeTest {
  @Test
  void wrap_when_mapTypeWrapped_then_correctWrapped() {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), StringType.uuid());
    final JavaMapType javaType = JavaMapType.wrap(mapType, TypeMappings.empty());

    assertEquals(Optional.empty(), javaType.getApiClassName());
    assertEquals(Optional.empty(), javaType.getApiParameterizedClassName());

    assertEquals("Map<String, UUID>", javaType.getInternalParameterizedClassName().asString());
    assertEquals("Map", javaType.getInternalClassName().getClassName().asString());
    assertEquals(
        PList.of("java.lang.String", "java.util.Map", "java.util.UUID"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_withClassMapping_then_correctTypeMapped() {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), StringType.uuid());
    final JavaMapType javaType =
        JavaMapType.wrap(
            mapType,
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("Map", "com.custom.CustomMap")));

    assertEquals(
        Optional.of("CustomMap"),
        javaType.getApiClassName().map(cn -> cn.getClassName().asString()));
    assertEquals(
        Optional.of("CustomMap<String, UUID>"),
        javaType.getApiParameterizedClassName().map(ParameterizedClassName::asString));

    assertEquals("Map<String, UUID>", javaType.getInternalParameterizedClassName().asString());
    assertEquals("Map", javaType.getInternalClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomMap", "java.lang.String", "java.util.Map", "java.util.UUID"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void
      getParameterizedClassName_when_suppliedFunctionCreatesAnnotation_then_correctStandardTypeClassNameAndImportsReturned() {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), StringType.uuid());
    final JavaMapType javaType = JavaMapType.wrap(mapType, TypeMappings.empty());

    final Function<JavaType, String> createAnnotations = ignore -> "@Annotation";

    // method call
    final ParameterizedClassName parameterizedClassName =
        javaType.getInternalParameterizedClassName();

    assertEquals(
        "Map<String, @Annotation UUID>",
        parameterizedClassName.asStringWithValueTypeAnnotations(createAnnotations));
  }

  @Test
  void
      getFullAnnotatedClassName_when_suppliedFunctionCreatesNoAnnotations_then_correctClassNameAndImportsReturned() {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), StringType.uuid());
    final JavaMapType javaType = JavaMapType.wrap(mapType, TypeMappings.empty());

    final Function<JavaType, String> createAnnotations = ignore -> "";

    // method call
    final ParameterizedClassName parameterizedClassName =
        javaType.getInternalParameterizedClassName();

    assertEquals(
        "Map<String, UUID>",
        parameterizedClassName.asStringWithValueTypeAnnotations(createAnnotations));
  }
}
