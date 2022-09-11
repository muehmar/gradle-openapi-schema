package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import java.util.Comparator;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class JavaEnumTypeTest {
  @Test
  void wrap_when_enumTypeWrapped_then_correctWrapped() {
    final EnumType enumType =
        EnumType.ofNameAndMembers(Name.ofString("Gender"), PList.of("male", "female", "divers"));
    final JavaEnumType javaType = JavaEnumType.wrap(enumType);

    assertEquals("Gender", javaType.getFullClassName().asString());
    assertEquals("Gender", javaType.getClassName().asString());
    assertEquals(
        PList.of("Gender"),
        javaType
            .getAllQualifiedClassNames()
            .map(Name::asString)
            .sort(Comparator.comparing(Function.identity())));
  }
}
