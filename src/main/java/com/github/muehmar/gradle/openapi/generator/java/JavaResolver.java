package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.BOOLEAN;

import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.Type;

public class JavaResolver implements Resolver {

  @Override
  public String getterName(String key, Type type) {
    final String prefix = type.getName().equals(BOOLEAN.getName()) ? "is" : "get";
    return prefix + toCamelCase(key);
  }

  @Override
  public String setterName(String key) {
    return "set" + toCamelCase(key);
  }

  @Override
  public String witherName(String key) {
    return "with" + toCamelCase(key);
  }

  @Override
  public String memberName(String key) {
    return toSnakeCase(key);
  }

  @Override
  public String className(String key) {
    return toCamelCase(key);
  }

  public static String toSnakeCase(String key) {
    return key.substring(0, 1).toLowerCase() + key.substring(1);
  }

  public static String toCamelCase(String key) {
    return key.substring(0, 1).toUpperCase() + key.substring(1);
  }
}
