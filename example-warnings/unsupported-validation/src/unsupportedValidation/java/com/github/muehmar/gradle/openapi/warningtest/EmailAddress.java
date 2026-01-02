package com.github.muehmar.gradle.openapi.warningtest;

public class EmailAddress {
  private final String value;

  private EmailAddress(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    EmailAddress that = (EmailAddress) o;
    return value.equals(that.value);
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return "EmailAddress{" + value + "}";
  }
}
