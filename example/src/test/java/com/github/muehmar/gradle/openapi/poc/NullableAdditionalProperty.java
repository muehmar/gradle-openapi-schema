package com.github.muehmar.gradle.openapi.poc;

import java.util.Objects;
import java.util.Optional;

public class NullableAdditionalProperty<T> {
  private final String name;
  private final T value;

  private NullableAdditionalProperty(String name, T value) {
    this.name = name;
    this.value = value;
  }

  public static <T> NullableAdditionalProperty<T> ofNullable(String name, T value) {
    return new NullableAdditionalProperty<>(name, value);
  }

  public String getName() {
    return name;
  }

  public Optional<T> getValue() {
    return Optional.ofNullable(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final NullableAdditionalProperty<?> that = (NullableAdditionalProperty<?>) o;
    return Objects.equals(name, that.name) && Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, value);
  }

  @Override
  public String toString() {
    return "AdditionalProperty{" + "name='" + name + '\'' + ", value=" + value + '}';
  }
}
