package com.github.muehmar.gradle.openapi.generator.model.constraints;

import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class PropertyCount {
  private final Optional<Integer> min;
  private final Optional<Integer> max;

  private PropertyCount(Optional<Integer> min, Optional<Integer> max) {
    this.min = min;
    this.max = max;
  }

  public static PropertyCount ofMinProperties(int min) {
    return new PropertyCount(Optional.of(min), Optional.empty());
  }

  public static PropertyCount ofMaxProperties(int max) {
    return new PropertyCount(Optional.empty(), Optional.of(max));
  }

  public static PropertyCount ofMinAndMaxProperties(int min, int max) {
    return new PropertyCount(Optional.of(min), Optional.of(max));
  }

  public Optional<Integer> getMinProperties() {
    return min;
  }

  public Optional<Integer> getMaxProperties() {
    return max;
  }
}
