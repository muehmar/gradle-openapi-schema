package com.github.muehmar.gradle.openapi.typemappingwithconversion;

public class CustomStrings {
  private CustomStrings() {}

  public static CustomString customString(String value) {
    return new CustomString(value);
  }
}
