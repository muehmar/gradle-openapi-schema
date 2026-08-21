package com.github.muehmar.gradle.openapi.generator.java.model.name;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import org.junit.jupiter.api.Test;

class LocalVariableNameTest {
  @Test
  void asString_when_variableNameIsEqualToMemberName_then_variableNameEscaped() {
    final JavaPojoMember member = TestJavaPojoMembers.requiredBirthdate();

    final LocalVariableName variableName =
        LocalVariableName.of(member.getName().asString()).withPojoMemberAsMethodArgument(member);

    assertEquals("birthdate_", variableName.asString());
  }

  @Test
  void asString_when_variableNameIsNotEqualToMemberName_then_variableNameNotEscaped() {
    final JavaPojoMember member = TestJavaPojoMembers.requiredBirthdate();

    final LocalVariableName variableName =
        LocalVariableName.of("value").withPojoMemberAsMethodArgument(member);

    assertEquals("value", variableName.asString());
  }
}
