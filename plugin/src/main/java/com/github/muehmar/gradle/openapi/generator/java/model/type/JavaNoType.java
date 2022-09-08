package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.ClassNames;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString
public class JavaNoType extends NonGenericJavaType {
  private static final ClassName CLASS_NAME = ClassNames.OBJECT;

  private JavaNoType() {
    super(CLASS_NAME);
  }

  public static JavaNoType create() {
    return new JavaNoType();
  }

  @Override
  public JavaType asPrimitive() {
    return this;
  }
}
