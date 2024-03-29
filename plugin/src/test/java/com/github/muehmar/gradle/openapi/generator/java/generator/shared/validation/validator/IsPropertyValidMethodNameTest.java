package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import org.junit.jupiter.api.Test;

class IsPropertyValidMethodNameTest {

  @Test
  void fromMember_when_calledForRequireStringMember_then_correctMethodName() {
    final IsPropertyValidMethodName isPropertyValidMethodName =
        IsPropertyValidMethodName.fromMember(TestJavaPojoMembers.requiredString());

    assertEquals("isStringValValid", isPropertyValidMethodName.asString());
  }
}
