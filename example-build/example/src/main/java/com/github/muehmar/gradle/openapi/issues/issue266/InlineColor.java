package com.github.muehmar.gradle.openapi.issues.issue266;

import java.util.Objects;

/**
 * A custom type used as the target of a {@code formatTypeMapping} WITH conversion on an INLINE enum
 * schema. The enum stays inline and is generated as nested class of the referencing DTO, so the api
 * type of the member combines the user conversion defined here with the plugin conversion of the
 * re-nested enum ({@code Palette2Dto.ColorEnum}).
 */
public class InlineColor {
  private final String value;

  private InlineColor(String value) {
    this.value = value;
  }

  public static InlineColor fromEnum(Palette2Dto.ColorEnum colorEnum) {
    return new InlineColor(colorEnum.getValue());
  }

  public Palette2Dto.ColorEnum toEnum() {
    return Palette2Dto.ColorEnum.fromValue(value);
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final InlineColor that = (InlineColor) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "InlineColor{value='" + value + "'}";
  }
}
