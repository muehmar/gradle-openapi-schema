package com.github.muehmar.gradle.openapi.issues.issue383;

import java.util.Objects;

/** Custom type for the referenced Kind enum, mapped via formatTypeMapping with conversions. */
public class CustomKind {
  private final KindDto dto;

  private CustomKind(KindDto dto) {
    this.dto = dto;
  }

  public static CustomKind fromDto(KindDto dto) {
    return new CustomKind(dto);
  }

  public KindDto toDto() {
    return dto;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final CustomKind that = (CustomKind) o;
    return Objects.equals(dto, that.dto);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(dto);
  }

  @Override
  public String toString() {
    return "CustomKind{" + "dto=" + dto + '}';
  }
}
