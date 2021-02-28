package com.github.muehmar.gradle.openapi;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;
import java.util.List;

public class Util {
  private Util() {}

  public static <T> void assertEqualsIgnoreOrder(List<T> l1, List<T> l2) {
    assertEquals(new HashSet<>(l1), new HashSet<>(l2));
  }
}
