package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaBooleanTypeTest {
  @Test
  void wrap_when_noTypeMappings_then_correctWrapped() {
    final JavaBooleanType javaType = JavaTypes.booleanType();

    assertEquals(Optional.empty(), javaType.getApiClassName());
    assertEquals(Optional.empty(), javaType.getApiParameterizedClassName());

    assertEquals("Boolean", javaType.getInternalParameterizedClassName().asString());
    assertEquals("Boolean", javaType.getInternalClassName().getClassName().asString());
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

    assertEquals(
        Optional.of("CustomBoolean"),
        javaType.getApiClassName().map(cn -> cn.getClassName().asString()));
    assertEquals(
        Optional.of("CustomBoolean"),
        javaType.getApiParameterizedClassName().map(ParameterizedClassName::asString));

    assertEquals("Boolean", javaType.getInternalParameterizedClassName().asString());
    assertEquals("Boolean", javaType.getInternalClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomBoolean", "java.lang.Boolean"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
