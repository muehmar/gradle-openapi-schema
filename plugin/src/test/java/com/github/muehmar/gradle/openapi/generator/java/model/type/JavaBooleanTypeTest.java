package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaBooleanTypeTest {
  @Test
  void wrap_when_noTypeMappings_then_correctWrapped() {
    final JavaBooleanType javaType = JavaTypes.booleanType();

    assertEquals("Boolean", javaType.getParameterizedClassName().asString());
    assertEquals("Boolean", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("java.lang.Boolean"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_withClassMapping_then_correctTypeMapped() {
    final JavaBooleanType javaType =
        JavaBooleanType.wrap(
            BooleanType.create(Nullability.NOT_NULLABLE),
            TypeMappings.ofSingleClassTypeMapping(
                new ClassTypeMapping("Boolean", "com.custom.CustomBoolean")));

    assertEquals("CustomBoolean", javaType.getParameterizedClassName().asString());
    assertEquals("CustomBoolean", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomBoolean"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
