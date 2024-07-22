package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import java.util.function.Predicate;

public enum GetterType {
  STANDARD,
  STANDARD_NO_VALIDATION,
  API_TYPE,
  API_TYPE_FRAMEWORK;

  public <T> Predicate<T> validationFilter() {
    return ignore -> this.equals(STANDARD) || this.equals(API_TYPE_FRAMEWORK);
  }
}
