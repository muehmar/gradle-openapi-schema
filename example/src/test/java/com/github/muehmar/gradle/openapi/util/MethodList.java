package com.github.muehmar.gradle.openapi.util;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;

public class MethodList {
  private MethodList() {}

  public static String listMethodNames(Class<?> clazz) {
    return Arrays.stream(clazz.getDeclaredMethods())
        .map(Method::getName)
        .collect(Collectors.joining(", "));
  }
}
