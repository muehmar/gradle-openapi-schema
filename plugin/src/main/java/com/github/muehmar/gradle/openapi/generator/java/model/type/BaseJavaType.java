package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.ClassName;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class BaseJavaType implements JavaType {
  protected final ClassName className;

  protected BaseJavaType(ClassName className) {
    this.className = className;
  }

  @Override
  public Name getClassName() {
    return className.getClassName();
  }
}
