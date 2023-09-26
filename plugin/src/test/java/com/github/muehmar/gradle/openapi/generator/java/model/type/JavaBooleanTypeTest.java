package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaBooleanTypeTest {
  @Test
  void wrap_when_noTypeMappings_then_correctWrapped() {
    final JavaBooleanType javaType = JavaBooleanType.wrap(TypeMappings.empty());

    assertEquals("Boolean", javaType.getFullClassName().asString());
    assertEquals("Boolean", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("java.lang.Boolean"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_withClassMapping_then_correctTypeMapped() {
    final JavaBooleanType javaType =
        JavaBooleanType.wrap(
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("Boolean", "com.custom.CustomBoolean")));

    assertEquals("CustomBoolean", javaType.getFullClassName().asString());
    assertEquals("CustomBoolean", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomBoolean"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
