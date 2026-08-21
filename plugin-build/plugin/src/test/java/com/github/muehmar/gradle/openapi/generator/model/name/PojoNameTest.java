package com.github.muehmar.gradle.openapi.generator.model.name;

import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PojoNameTest {
  @ParameterizedTest
  @MethodSource("equalsIgnoringCase")
  void equalsIgnoreCase_when_equalsIgnoringCase_then_true(PojoName name1, PojoName name2) {
    assertTrue(name1.equalsIgnoreCase(name2));
  }

  public static Stream<Arguments> equalsIgnoringCase() {
    return Stream.of(
        arguments(pojoName("User", "Dto"), pojoName("user", "Dto")),
        arguments(pojoName("user", "Dto"), pojoName("User", "Dto")));
  }

  @ParameterizedTest
  @MethodSource("notEqualsIgnoringCase")
  void equalsIgnoreCase_when_notEqualsIgnoringCase_then_false(PojoName name1, PojoName name2) {
    assertFalse(name1.equalsIgnoreCase(name2));
  }

  public static Stream<Arguments> notEqualsIgnoringCase() {
    return Stream.of(
        arguments(pojoName("User", "Dto"), pojoName("Gender", "Dto")),
        arguments(pojoName("User", "Dto"), pojoName("User", "")));
  }

  @Test
  void deriveMemberSchemaName_when_called_then_nameDerivedCorrectly() {
    final PojoName pojoName = pojoName("User", "Dto");
    final PojoName openApiPojoName = pojoName.deriveMemberSchemaName(Name.ofString("Member"));
    assertEquals("UserMemberDto", openApiPojoName.asString());
  }
}
