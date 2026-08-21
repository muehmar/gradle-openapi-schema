package com.github.muehmar.gradle.openapi.issues.issue266;

import java.util.Objects;

/**
 * A custom type used as the target of a {@code dtoMapping} WITH conversion on a referenced enum
 * schema which is used as the DISCRIMINATOR property of a oneOf composition. The conversion towards
 * this type is deliberately NOT defined on this class but on {@link MediaKindConversions}, so the
 * generated dto needs an import for a class which appears nowhere else in it.
 */
public class MediaKind {
  private final String value;

  MediaKind(String value) {
    this.value = value;
  }

  public MappedDiscriminatorKindDto toDto() {
    return MappedDiscriminatorKindDto.fromValue(value);
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final MediaKind that = (MediaKind) o;
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "MediaKind{value='" + value + "'}";
  }
}
