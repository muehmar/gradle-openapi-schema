package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
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
    assertEquals("Map", javaType.getClassName().asString());
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
                new ClassTypeMapping("Map", "CustomMap", "com.custom.CustomMap")));

    assertEquals("CustomMap<String, UUID>", javaType.getFullClassName().asString());
    assertEquals("CustomMap", javaType.getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomMap", "java.lang.String", "java.util.UUID"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void asPrimitive_when_called_then_unchanged() {
    final JavaType javaType = JavaTypes.MAP.asPrimitive();
    assertEquals(JavaTypes.MAP, javaType);
  }
}
