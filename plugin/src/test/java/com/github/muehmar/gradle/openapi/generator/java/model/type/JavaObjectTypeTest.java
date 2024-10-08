package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaObjectTypeTest {
  @Test
  void wrap_when_objectTypeWrapped_then_correctWrapped() {
    final ObjectType objectType = StandardObjectType.ofName(pojoName("User", "Dto"));
    final JavaObjectType javaType = JavaObjectType.wrap(objectType);

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("UserDto", javaType.getParameterizedClassName().asString());
    assertEquals("UserDto", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("UserDto"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
    assertEquals(JavaObjectType.TypeOrigin.OPENAPI, javaType.getOrigin());
  }

  @Test
  void wrap_when_pojoNameWithSpecialCharacters_then_correctType() {
    final ObjectType objectType = StandardObjectType.ofName(pojoName("Prefixed.User", "Dto"));
    final JavaObjectType javaType = JavaObjectType.wrap(objectType);

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("Prefixed_UserDto", javaType.getParameterizedClassName().asString());
    assertEquals("Prefixed_UserDto", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("Prefixed_UserDto"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
    assertEquals(JavaObjectType.TypeOrigin.OPENAPI, javaType.getOrigin());
  }

  @Test
  void fromClassName_when_calledForClassname_then_correctType() {
    final JavaObjectType javaType =
        JavaObjectType.fromClassName(QualifiedClassName.ofName("com.github.muehmar.CustomObject"));

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals(
        "com.github.muehmar.CustomObject", javaType.getParameterizedClassName().asString());
    assertEquals(
        "com.github.muehmar.CustomObject",
        javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(JavaObjectType.TypeOrigin.CUSTOM, javaType.getOrigin());
  }
}
