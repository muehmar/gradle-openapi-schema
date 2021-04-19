package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.BOOLEAN;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Type;

public class JavaResolver implements Resolver {

  @Override
  public Name getterName(Name name, Type type) {
    final String prefix = type.getFullName().equalsIgnoreCase(BOOLEAN.getFullName()) ? "is" : "get";
    return toPascalCase(name).prefix(prefix);
  }

  @Override
  public Name setterName(Name name) {
    return toPascalCase(name).prefix("set");
  }

  @Override
  public Name witherName(Name name) {
    return toPascalCase(name).prefix("with");
  }

  @Override
  public Name memberName(Name name) {
    return toCamelCase(name);
  }

  @Override
  public Name className(Name name) {
    return toPascalCase(name);
  }

  @Override
  public Name enumName(Name name) {
    return toPascalCase(name).append("Enum");
  }

  public static Name toCamelCase(Name name) {
    return name.map(n -> n.substring(0, 1).toLowerCase() + n.substring(1));
  }

  public static Name toPascalCase(Name name) {
    return name.map(n -> n.substring(0, 1).toUpperCase() + n.substring(1));
  }

  public static Name toPascalCase(Name... names) {
    return PList.fromArray(names)
        .map(JavaResolver::toPascalCase)
        .reduce(Name::append)
        .orElseThrow(() -> new IllegalArgumentException("No names supplied"));
  }

  public static Name snakeCaseToPascalCase(String name) {
    return PList.fromArray(name.split("_"))
        .map(String::toLowerCase)
        .map(Name::of)
        .map(JavaResolver::toPascalCase)
        .reduce(Name::append)
        .orElseThrow(() -> new IllegalArgumentException("No names supplied"));
  }
}
