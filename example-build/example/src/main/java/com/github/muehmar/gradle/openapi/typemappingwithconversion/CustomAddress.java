package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import java.util.Objects;

public class CustomAddress {
  private final String street;
  private final Integer number;
  private final Integer zipCode;
  private final String city;

  public CustomAddress(String street, Integer number, Integer zipCode, String city) {
    this.street = street;
    this.number = number;
    this.zipCode = zipCode;
    this.city = city;
  }

  public static CustomAddress fromDto(AddressDto address) {
    return new CustomAddress(
        address.getStreet().getValue(),
        address.getNumber(),
        address.getZipCode(),
        address.getCity().getValue());
  }

  public AddressDto toDto() {
    return AddressDto.fullAddressDtoBuilder()
        .setStreet(new CustomString(street))
        .setNumber(number)
        .setZipCode(zipCode)
        .setCity(new CustomString(city))
        .build();
  }

  public String getStreet() {
    return street;
  }

  public Integer getNumber() {
    return number;
  }

  public Integer getZipCode() {
    return zipCode;
  }

  public String getCity() {
    return city;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final CustomAddress that = (CustomAddress) o;
    return Objects.equals(street, that.street)
        && Objects.equals(number, that.number)
        && Objects.equals(zipCode, that.zipCode)
        && Objects.equals(city, that.city);
  }

  @Override
  public int hashCode() {
    return Objects.hash(street, number, zipCode, city);
  }

  @Override
  public String toString() {
    return "CustomAddress{"
        + "street='"
        + street
        + '\''
        + ", number="
        + number
        + ", zipCode="
        + zipCode
        + ", city='"
        + city
        + '\''
        + '}';
  }
}
