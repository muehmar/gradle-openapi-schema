package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import java.util.Objects;

public class CustomString {
  private final String value;

  public static CustomString fromString(String value) {
    return new CustomString(value);
  }

  public CustomString(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final CustomString that = (CustomString) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "CustomString{" + "value='" + value + '\'' + '}';
  }
}
