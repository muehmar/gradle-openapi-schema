package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import java.util.Comparator;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaObjectTypeTest {
  @Test
  void wrap_when_objectTypeWrapped_then_correctWrapped() {
    final ObjectType objectType = ObjectType.ofName(pojoName("User", "Dto"));
    final JavaObjectType javaType = JavaObjectType.wrap(objectType);

    assertEquals("UserDto", javaType.getFullClassName().asString());
    assertEquals("UserDto", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("UserDto"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
    assertEquals(JavaObjectType.TypeOrigin.OPENAPI, javaType.getOrigin());
  }

  @Test
  void wrap_when_pojoNameWithSpecialCharacters_then_correctType() {
    final ObjectType objectType = ObjectType.ofName(pojoName("Prefixed.User", "Dto"));
    final JavaObjectType javaType = JavaObjectType.wrap(objectType);

    assertEquals("Prefixed_UserDto", javaType.getFullClassName().asString());
    assertEquals("Prefixed_UserDto", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("Prefixed_UserDto"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
    assertEquals(JavaObjectType.TypeOrigin.OPENAPI, javaType.getOrigin());
  }

  @Test
  void fromClassName_when_calledForClassname_then_correctType() {
    final JavaObjectType javaType =
        JavaObjectType.fromClassName(QualifiedClassName.ofName("com.github.muehmar.CustomObject"));

    assertEquals("com.github.muehmar.CustomObject", javaType.getFullClassName().asString());
    assertEquals(
        "com.github.muehmar.CustomObject",
        javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(JavaObjectType.TypeOrigin.CUSTOM, javaType.getOrigin());
  }
}
