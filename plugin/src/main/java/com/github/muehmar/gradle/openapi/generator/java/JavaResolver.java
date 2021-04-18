package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.BOOLEAN;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.Type;

public class JavaResolver implements Resolver {

  @Override
  public String getterName(String key, Type type) {
    final String prefix = type.getFullName().equalsIgnoreCase(BOOLEAN.getFullName()) ? "is" : "get";
    return prefix + toPascalCase(key);
  }

  @Override
  public String setterName(String key) {
    return "set" + toPascalCase(key);
  }

  @Override
  public String witherName(String key) {
    return "with" + toPascalCase(key);
  }

  @Override
  public String memberName(String key) {
    return toCamelCase(key);
  }

  @Override
  public String className(String key) {
    return toPascalCase(key);
  }

  @Override
  public String enumName(String key) {
    return toPascalCase(key) + "Enum";
  }

  public static String toCamelCase(String key) {
    return key.substring(0, 1).toLowerCase() + key.substring(1);
  }

  public static String toPascalCase(String key) {
    return key.substring(0, 1).toUpperCase() + key.substring(1);
  }

  public static String toPascalCase(String... keys) {
    return PList.fromArray(keys).map(JavaResolver::toPascalCase).mkString("");
  }

  public static String snakeCaseToPascalCase(String key) {
    return PList.fromArray(key.split("_"))
        .map(String::toLowerCase)
        .map(JavaResolver::toPascalCase)
        .mkString("");
  }
}
