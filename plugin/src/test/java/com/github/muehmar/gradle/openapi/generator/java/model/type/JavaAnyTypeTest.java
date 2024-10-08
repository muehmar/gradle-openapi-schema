package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.anyType;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaAnyTypeTest {
  @Test
  void create_when_created_then_correctType() {
    final JavaAnyType javaType = anyType();

    assertEquals(Optional.empty(), javaType.getApiType());

    assertEquals("Object", javaType.getParameterizedClassName().asString());
    assertEquals("Object", javaType.getQualifiedClassName().getClassName().asString());
    assertEquals(
        PList.of("java.lang.Object"),
        javaType
            .getAllQualifiedClassNames()
            .map(QualifiedClassName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
