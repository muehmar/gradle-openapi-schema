package com.github.muehmar.gradle.openapi;

import java.util.Arrays;
import java.util.List;

public class Lists {
  private Lists() {}

  @SafeVarargs
  public static <T> List<T> list(T... elements) {
    return Arrays.asList(elements);
  }
}
