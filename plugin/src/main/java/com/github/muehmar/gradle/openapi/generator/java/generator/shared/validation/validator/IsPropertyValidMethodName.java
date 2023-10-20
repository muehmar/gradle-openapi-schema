package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.validator;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import lombok.Value;

@Value
public class IsPropertyValidMethodName {
  JavaIdentifier name;

  public static IsPropertyValidMethodName fromMember(JavaPojoMember member) {
    return fromName(member.getName().asName());
  }

  public static IsPropertyValidMethodName fromName(Name name) {
    final Name upperCaseName = name.startUpperCase();
    final JavaIdentifier methodeName =
        JavaIdentifier.fromString(String.format("is%sValid", upperCaseName));
    return new IsPropertyValidMethodName(methodeName);
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}