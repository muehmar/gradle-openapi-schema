package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class BaseJavaType implements JavaType {
  protected final ClassName className;
  protected final Type type;

  protected BaseJavaType(ClassName className, Type type) {
    this.className = className;
    this.type = type;
  }

  @Override
  public Name getClassName() {
    return className.getClassName();
  }

  @Override
  public Type getType() {
    return type;
  }
}
