package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class BaseJavaType implements JavaType {
  protected final QualifiedClassName qualifiedClassName;
  protected final Type type;

  protected BaseJavaType(QualifiedClassName qualifiedClassName, Type type) {
    this.qualifiedClassName = qualifiedClassName;
    this.type = type;
  }

  @Override
  public QualifiedClassName getQualifiedClassName() {
    return qualifiedClassName;
  }

  @Override
  public Type getType() {
    return type;
  }
}
