package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import java.util.Comparator;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaObjectTypeTest {
  @Test
  void wrap_when_objectTypeWrapped_then_correctWrapped() {
    final ObjectType objectType = ObjectType.ofName(PojoName.ofNameAndSuffix("User", "Dto"));
    final JavaObjectType javaType = JavaObjectType.wrap(objectType);

    assertEquals("UserDto", javaType.getFullClassName().asString());
    assertEquals("UserDto", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("UserDto"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void wrap_when_pojoNameWithSpecialCharacters_then_correctType() {
    final ObjectType objectType =
        ObjectType.ofName(PojoName.ofNameAndSuffix("Prefixed.User", "Dto"));
    final JavaObjectType javaType = JavaObjectType.wrap(objectType);

    assertEquals("Prefixed_UserDto", javaType.getFullClassName().asString());
    assertEquals("Prefixed_UserDto", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("Prefixed_UserDto"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
