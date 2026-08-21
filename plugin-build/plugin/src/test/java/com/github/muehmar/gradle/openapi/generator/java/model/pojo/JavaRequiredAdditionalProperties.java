package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.booleanType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.integerType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.mapType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.stringType;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;

public class JavaRequiredAdditionalProperties {
  private JavaRequiredAdditionalProperties() {}

  public static JavaRequiredAdditionalProperty prop1() {
    return JavaRequiredAdditionalProperty.fromNameAndType(Name.ofString("prop1"), stringType());
  }

  public static JavaRequiredAdditionalProperty prop2() {
    return JavaRequiredAdditionalProperty.fromNameAndType(Name.ofString("prop2"), integerType());
  }

  public static JavaRequiredAdditionalProperty prop3() {
    return JavaRequiredAdditionalProperty.fromNameAndType(Name.ofString("prop3"), mapType());
  }

  public static JavaRequiredAdditionalProperty prop4() {
    return JavaRequiredAdditionalProperty.fromNameAndType(Name.ofString("prop4"), booleanType());
  }
}
