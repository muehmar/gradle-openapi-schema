package com.github.muehmar.gradle.openapi.issues.issue382;

import java.util.Objects;

/** Custom type for the AddressDto, mapped via dtoMapping with conversions. */
public class CustomAddress {
  private final AddressDto dto;

  private CustomAddress(AddressDto dto) {
    this.dto = dto;
  }

  public static CustomAddress fromDto(AddressDto dto) {
    return new CustomAddress(dto);
  }

  public AddressDto toDto() {
    return dto;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final CustomAddress that = (CustomAddress) o;
    return Objects.equals(dto, that.dto);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(dto);
  }

  @Override
  public String toString() {
    return "CustomAddress{" + "dto=" + dto + '}';
  }
}
