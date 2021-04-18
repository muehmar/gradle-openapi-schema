package com.github.muehmar.gradle.openapi.generator.java;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import org.junit.jupiter.api.Test;

class JavaResolverTest {

  @Test
  void getterName_when_primitiveBoolean_then_correctGetter() {
    final JavaResolver javaResolver = new JavaResolver();
    final String getterName = javaResolver.getterName("activeUser", JavaType.ofName("boolean"));
    assertEquals("isActiveUser", getterName);
  }

  @Test
  void getterName_when_booleanType_then_correctGetter() {
    final JavaResolver javaResolver = new JavaResolver();
    final String getterName = javaResolver.getterName("activeUser", JavaTypes.BOOLEAN);
    assertEquals("isActiveUser", getterName);
  }

  @Test
  void setterName_when_anyCamelCaseAttribute_then_correctSetter() {
    final JavaResolver javaResolver = new JavaResolver();
    final String setterName = javaResolver.setterName("lastName");
    assertEquals("setLastName", setterName);
  }

  @Test
  void witherName_when_anyCamelCaseName_then_correctWither() {
    final JavaResolver javaResolver = new JavaResolver();
    final String witherName = javaResolver.witherName("lastName");
    assertEquals("withLastName", witherName);
  }

  @Test
  void className_when_anyCamelCaseName_then_classNameIsPascalCase() {
    final JavaResolver javaResolver = new JavaResolver();
    final String className = javaResolver.className("lastName");
    assertEquals("LastName", className);
  }

  @Test
  void memberName_when_anyPascalCaseName_then_memberNameIsCamelCase() {
    final JavaResolver javaResolver = new JavaResolver();
    final String memberName = javaResolver.memberName("LastName");
    assertEquals("lastName", memberName);
  }

  @Test
  void enumName_when_anyCamelCaseName_then_correctEnumName() {
    final JavaResolver javaResolver = new JavaResolver();
    final String memberName = javaResolver.enumName("lastName");
    assertEquals("LastNameEnum", memberName);
  }

  @Test
  void toPascalCase_when_multipleKeys_then_everyKeyIsPascalCaseAndConcatenated() {
    final String pascalCase = JavaResolver.toPascalCase("userGroup", "language");
    assertEquals("UserGroupLanguage", pascalCase);
  }

  @Test
  void snakeCaseToPascalCase_when_snakeCase_then_convertedToPascalCase() {
    final String pascalCase = JavaResolver.snakeCaseToPascalCase("SNAKE_CASE");
    assertEquals("SnakeCase", pascalCase);
  }
}
