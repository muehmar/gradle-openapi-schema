package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ToApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
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

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("Map<String, UUID>", javaType.getParameterizedClassName().asString());
    assertEquals("Map", javaType.getQualifiedClassName().getClassName().asString());
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
                new ClassTypeMapping("Map", "com.custom.CustomMap", Optional.empty())));

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("CustomMap<String, UUID>", javaType.getParameterizedClassName().asString());
    assertEquals("CustomMap", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomMap", "java.lang.String", "java.util.UUID"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_withClassMappingAndConversion_then_correctTypeMapped() {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), StringType.uuid());
    final TypeConversion typeConversion =
        new TypeConversion("toMap", "com.custom.CustomMap#fromMap");
    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("Map", "com.custom.CustomMap", Optional.of(typeConversion));
    final JavaMapType javaType =
        JavaMapType.wrap(mapType, TypeMappings.ofSingleClassTypeMapping(classTypeMapping));

    assertEquals(
        Optional.of("com.custom.CustomMap"),
        javaType.getApiType().map(apiType -> apiType.getClassName().asString()));
    assertEquals(
        Optional.of("CustomMap<String, UUID>"),
        javaType.getApiType().map(apiType -> apiType.getParameterizedClassName().asString()));
    assertEquals(
        Optional.of(
            new ToApiTypeConversion(ConversionMethod.ofString(typeConversion.getToCustomType()))),
        javaType.getApiType().map(ApiType::getToApiTypeConversion));
    assertEquals(
        Optional.of(
            new FromApiTypeConversion(
                ConversionMethod.ofString(typeConversion.getFromCustomType()))),
        javaType.getApiType().map(ApiType::getFromApiTypeConversion));

    assertEquals("Map<String, UUID>", javaType.getParameterizedClassName().asString());
    assertEquals("Map", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomMap", "java.lang.String", "java.util.Map", "java.util.UUID"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void
      getParameterizedClassName_when_suppliedFunctionCreatesAnnotation_then_correctClassNameAndImportsReturned() {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), StringType.uuid());
    final JavaMapType javaType = JavaMapType.wrap(mapType, TypeMappings.empty());

    final Function<JavaType, String> createAnnotations = ignore -> "@Annotation";

    // method call
    final ParameterizedClassName parameterizedClassName = javaType.getParameterizedClassName();

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
    final ParameterizedClassName parameterizedClassName = javaType.getParameterizedClassName();

    assertEquals(
        "Map<String, UUID>",
        parameterizedClassName.asStringWithValueTypeAnnotations(createAnnotations));
  }
}
