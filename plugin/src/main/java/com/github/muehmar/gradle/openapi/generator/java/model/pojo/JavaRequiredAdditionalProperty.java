package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import lombok.Value;

@Value
public class JavaRequiredAdditionalProperty {
  Name name;
  JavaType javaType;

  public static JavaRequiredAdditionalProperty fromNameAndType(Name name, JavaType javaType) {
    return new JavaRequiredAdditionalProperty(name, javaType);
  }

  public String getDescription() {
    return String.format("Additional Property '%s'", name);
  }

  public boolean isAnyType() {
    return javaType.equals(javaAnyType());
  }

  public boolean isNotAnyType() {
    return not(isAnyType());
  }
}
