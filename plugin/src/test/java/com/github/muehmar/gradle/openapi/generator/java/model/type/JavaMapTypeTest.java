package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaMapTypeTest {
  @Test
  void wrap_when_mapTypeWrapped_then_correctWrapped() {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), StringType.uuid());
    final JavaMapType javaType = JavaMapType.wrap(mapType, TypeMappings.empty());

    assertEquals("Map<String, UUID>", javaType.getFullClassName().asString());
    assertEquals("Map", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("java.lang.String", "java.util.Map", "java.util.UUID"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
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

    assertEquals("CustomMap<String, UUID>", javaType.getFullClassName().asString());
    assertEquals("CustomMap", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomMap", "java.lang.String", "java.util.UUID"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void
      getFullAnnotatedClassName_when_calledWithCreatorWithAnnotations_then_correctClassNameAndImportsReturned() {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), StringType.uuid());
    final JavaMapType javaType = JavaMapType.wrap(mapType, TypeMappings.empty());

    final AnnotationsCreator annotationsCreator =
        ignore -> new AnnotationsCreator.Annotations("@Annotation", PList.of("package.Annotation"));

    // method call
    final AnnotatedClassName fullAnnotatedClassName =
        javaType.getFullAnnotatedClassName(annotationsCreator);

    assertEquals("Map<String, @Annotation UUID>", fullAnnotatedClassName.getClassName().asString());
    assertEquals(PList.of("package.Annotation"), fullAnnotatedClassName.getImports());
  }

  @Test
  void
      getFullAnnotatedClassName_when_calledWithCreatorWithoutAnnotations_then_correctClassNameAndImportsReturned() {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), StringType.uuid());
    final JavaMapType javaType = JavaMapType.wrap(mapType, TypeMappings.empty());

    final AnnotationsCreator annotationsCreator =
        ignore -> new AnnotationsCreator.Annotations("", PList.empty());

    // method call
    final AnnotatedClassName fullAnnotatedClassName =
        javaType.getFullAnnotatedClassName(annotationsCreator);

    assertEquals("Map<String, UUID>", fullAnnotatedClassName.getClassName().asString());
    assertEquals(PList.empty(), fullAnnotatedClassName.getImports());
  }
}
