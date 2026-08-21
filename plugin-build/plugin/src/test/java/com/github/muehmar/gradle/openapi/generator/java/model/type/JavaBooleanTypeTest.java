package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.FromApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ToApiTypeConversion;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMapping;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaBooleanTypeTest {
  @Test
  void wrap_when_noTypeMappings_then_correctWrapped() {
    final JavaBooleanType javaType = JavaTypes.booleanType();

    assertEquals(Optional.empty(), javaType.getApiType());

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
                new ClassTypeMapping("Boolean", "com.custom.CustomBoolean", Optional.empty())));

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("CustomBoolean", javaType.getParameterizedClassName().asString());
    assertEquals("CustomBoolean", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomBoolean"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_withClassMappingAndConversion_then_correctTypeMapped() {
    final TypeConversion typeConversion =
        new TypeConversion("toBoolean", "com.custom.CustomBoolean#fromBoolean");
    final ClassTypeMapping classTypeMapping =
        new ClassTypeMapping("Boolean", "com.custom.CustomBoolean", Optional.of(typeConversion));
    final JavaBooleanType javaType =
        JavaBooleanType.wrap(
            BooleanType.create(Nullability.NOT_NULLABLE),
            TypeMappings.ofSingleClassTypeMapping(classTypeMapping));

    final QualifiedClassName className =
        QualifiedClassName.ofQualifiedClassName("com.custom.CustomBoolean");

    assertEquals(Optional.of(className), javaType.getApiType().map(ApiType::getClassName));
    assertEquals(
        Optional.of("CustomBoolean"),
        javaType.getApiType().map(apiType -> apiType.getParameterizedClassName().asString()));
    assertEquals(
        Optional.of(
            PList.single(
                new ToApiTypeConversion(
                    ConversionMethod.ofString(className, typeConversion.getToCustomType())))),
        javaType.getApiType().map(ApiType::getToApiTypeConversion));
    assertEquals(
        Optional.of(
            PList.single(
                new FromApiTypeConversion(
                    ConversionMethod.ofString(className, typeConversion.getFromCustomType())))),
        javaType.getApiType().map(ApiType::getFromApiTypeConversion));

    assertEquals("Boolean", javaType.getParameterizedClassName().asString());
    assertEquals("Boolean", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("com.custom.CustomBoolean", "java.lang.Boolean"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
