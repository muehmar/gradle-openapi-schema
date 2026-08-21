package com.github.muehmar.gradle.openapi.issues.issue364;

import java.util.Objects;

/**
 * This string is intentionally not automatically deserializable by jackson as the constructor has
 * two arguments and a factory method not named "fromString".
 */
public class CustomString {
  private final String value;

  public static CustomString create(String value) {
    return new CustomString(value, "");
  }

  public static CustomString create2(String value) {
    return new CustomString(value, "");
  }

  private CustomString(String value1, String value2) {
    this.value = value1 + value2;
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {

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
