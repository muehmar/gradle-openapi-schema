package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import java.util.Comparator;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaNoTypeTest {
  @Test
  void create_when_created_then_correctType() {
    final JavaNoType javaType = JavaNoType.create();

    assertEquals("Object", javaType.getFullClassName().asString());
    assertEquals("Object", javaType.getClassName().asString());
    assertEquals(
        PList.of("java.lang.Object"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void asPrimitive_when_called_then_unchanged() {
    final JavaType javaType = JavaTypes.NO_TYPE.asPrimitive();
    assertEquals(JavaTypes.NO_TYPE, javaType);
  }
}
