package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import java.util.Optional;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class BaseJavaType implements JavaType {
  protected final QualifiedClassName internalClassName;
  protected final Optional<QualifiedClassName> apiClassName;
  private final Nullability nullability;

  protected BaseJavaType(
      QualifiedClassName internalClassName,
      Optional<QualifiedClassName> apiClassName,
      Nullability nullability) {
    this.internalClassName = internalClassName;
    this.apiClassName = apiClassName;
    this.nullability = nullability;
  }

  @Override
  public QualifiedClassName getInternalClassName() {
    return internalClassName;
  }

  @Override
  public Optional<QualifiedClassName> getApiClassName() {
    return apiClassName;
  }

  @Override
  public Nullability getNullability() {
    return nullability;
  }
}
