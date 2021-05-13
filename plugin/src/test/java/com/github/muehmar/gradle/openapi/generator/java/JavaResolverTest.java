package com.github.muehmar.gradle.openapi.generator.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class JavaResolverTest {

  @Test
  void getterName_when_primitiveBoolean_then_correctGetter() {
    final JavaResolver javaResolver = new JavaResolver();
    final Name getterName =
        javaResolver.getterName(Name.of("activeUser"), JavaType.ofName("boolean"));
    assertEquals("isActiveUser", getterName.asString());
  }

  @Test
  void getterName_when_booleanType_then_correctGetter() {
    final JavaResolver javaResolver = new JavaResolver();
    final Name getterName = javaResolver.getterName(Name.of("activeUser"), JavaTypes.BOOLEAN);
    assertEquals("isActiveUser", getterName.asString());
  }

  @Test
  void setterName_when_anyCamelCaseAttribute_then_correctSetter() {
    final JavaResolver javaResolver = new JavaResolver();
    final Name setterName = javaResolver.setterName(Name.of("lastName"));
    assertEquals("setLastName", setterName.asString());
  }

  @Test
  void witherName_when_anyCamelCaseName_then_correctWither() {
    final JavaResolver javaResolver = new JavaResolver();
    final Name witherName = javaResolver.witherName(Name.of("lastName"));
    assertEquals("withLastName", witherName.asString());
  }

  @Test
  void className_when_anyCamelCaseName_then_classNameIsPascalCase() {
    final JavaResolver javaResolver = new JavaResolver();
    final Name className = javaResolver.className(Name.of("lastName"));
    assertEquals("LastName", className.asString());
  }

  @Test
  void memberName_when_anyPascalCaseName_then_memberNameIsCamelCase() {
    final JavaResolver javaResolver = new JavaResolver();
    final Name memberName = javaResolver.memberName(Name.of("LastName"));
    assertEquals("lastName", memberName.asString());
  }

  @Test
  void enumName_when_anyCamelCaseName_then_correctEnumName() {
    final JavaResolver javaResolver = new JavaResolver();
    final Name memberName = javaResolver.enumName(Name.of("lastName"));
    assertEquals("LastNameEnum", memberName.asString());
  }

  @Test
  void toPascalCase_when_multipleKeys_then_everyKeyIsPascalCaseAndConcatenated() {
    final Name pascalCase = JavaResolver.toPascalCase(Name.of("userGroup"), Name.of("language"));
    assertEquals("UserGroupLanguage", pascalCase.asString());
  }

  @Test
  void snakeCaseToPascalCase_when_snakeCase_then_convertedToPascalCase() {
    final Name pascalCase = JavaResolver.snakeCaseToPascalCase("SNAKE_CASE");
    assertEquals("SnakeCase", pascalCase.asString());
  }

  @ParameterizedTest
  @ValueSource(strings = {"anyCase", "AnyCase", " anyCase ", "ANY_CASE", "Any_Case"})
  void toUppercaseSnakeCase_when_pascalCase_then_convertedToSnakeCase(String in) {
    final Name snakeCase = JavaResolver.toUppercaseSnakeCase(in);
    assertEquals("ANY_CASE", snakeCase.asString());
  }
}
