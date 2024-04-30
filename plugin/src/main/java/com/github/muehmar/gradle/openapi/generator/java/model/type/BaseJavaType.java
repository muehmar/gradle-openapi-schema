package com.github.muehmar.gradle.openapi.generator.java.model.type;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ApiType;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import java.util.Optional;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public abstract class BaseJavaType implements JavaType {
  protected final QualifiedClassName className;
  protected final Optional<ApiType> apiType;
  private final Nullability nullability;

  protected BaseJavaType(
      QualifiedClassName className, Optional<ApiType> apiType, Nullability nullability) {
    this.className = className;
    this.apiType = apiType;
    this.nullability = nullability;
  }

  @Override
  public QualifiedClassName getQualifiedClassName() {
    return className;
  }

  @Override
  public Optional<ApiType> getApiType() {
    return apiType;
  }

  @Override
  public Nullability getNullability() {
    return nullability;
  }
}
