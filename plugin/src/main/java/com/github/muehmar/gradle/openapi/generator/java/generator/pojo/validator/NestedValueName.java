package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaMemberName;
import lombok.Value;

@Value
public class NestedValueName {
  JavaMemberName name;

  public static NestedValueName fromMemberName(JavaMemberName name) {
    return new NestedValueName(JavaMemberName.wrap(name.asName().append("Value")));
  }

  public String asString() {
    return name.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
