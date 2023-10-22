package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import lombok.Value;

@Value
public class IsPropertyValidMethodName {
  JavaName name;

  public static IsPropertyValidMethodName fromMember(JavaPojoMember member) {
    return fromName(member.getName());
  }

  public static IsPropertyValidMethodName fromName(JavaName name) {
    final JavaName upperCaseName = name.startUpperCase();
    final JavaName methodName = JavaName.fromString(String.format("is%sValid", upperCaseName));
    return new IsPropertyValidMethodName(methodName);
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
