package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class BaseJavaType implements JavaType {
  protected final QualifiedClassName qualifiedClassName;
  private final Nullability nullability;

  protected BaseJavaType(QualifiedClassName qualifiedClassName, Nullability nullability) {
    this.qualifiedClassName = qualifiedClassName;
    this.nullability = nullability;
  }

  @Override
  public QualifiedClassName getQualifiedClassName() {
    return qualifiedClassName;
  }

  @Override
  public Nullability getNullability() {
    return nullability;
  }
}
