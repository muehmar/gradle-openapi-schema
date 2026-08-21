package com.github.muehmar.gradle.openapi.issues.issue266;

import java.util.Objects;

/**
 * A custom type used as the target of a {@code dtoMapping} WITH conversion on a referenced (i.e.
 * {@code $ref}) enum schema. The referenced enum is generated as its own DTO ({@code
 * MappedColorEnumDto}), so the {@code dtoMapping} must be applied via the top-level {@code
 * JavaEnumType.wrapAsObjectType} code path.
 */
public class Color {
  private final String value;

  private Color(String value) {
    this.value = value;
  }

  public static Color fromDto(MappedColorEnumDto dto) {
    return new Color(dto.getValue());
  }

  public MappedColorEnumDto toDto() {
    return MappedColorEnumDto.fromValue(value);
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final Color color = (Color) o;
    return Objects.equals(value, color.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }

  @Override
  public String toString() {
    return "Color{value='" + value + "'}";
  }
}
