package com.github.muehmar.gradle.openapi.warningtest;

public class CustomId {
  private final String value;

  private CustomId(String value) {
    this.value = value;
  }

  public static CustomId fromString(String value) {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("CustomId value cannot be null or empty");
    }
    return new CustomId(value);
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CustomId customId = (CustomId) o;
    return value.equals(customId.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "CustomId{" + value + "}";
  }
}
