package com.github.muehmar.gradle.openapi.generator.java;

import ch.bluecare.commons.data.PList;
import ch.bluecare.commons.data.Pair;
import com.github.muehmar.gradle.openapi.generator.Resolver;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import java.util.Optional;

// TODO: Remove this resolver. This should go to the Pojo/PojoMember
public class JavaResolver implements Resolver {
  public static final String ILLEGAL_FIELD_CHARACTERS_PATTERN = "[^A-Za-z0-9$_]";

  @Override
  public Name getterName(Name name, Type type) {
    return toPascalCase(name).prefix("get");
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

  @Override
  public Name enumMemberName(Name name) {
    return toAsciiJavaName(toUppercaseSnakeCase(name.asString()));
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
        .map(Name::ofString)
        .map(JavaResolver::toPascalCase)
        .reduce(Name::append)
        .orElseThrow(() -> new IllegalArgumentException("No names supplied"));
  }

  /** Converts camelCase and PascalCase to uppercase SNAKE_CASE. */
  public static Name toUppercaseSnakeCase(String name) {
    if (name.toUpperCase().equals(name)) {
      return Name.ofString(name);
    }

    final String converted =
        name.trim()
            .replaceAll("([A-Z])", "_$1")
            .toUpperCase()
            .replaceFirst("^_", "")
            .replaceAll("_+", "_");
    return Name.ofString(converted);
  }

  public static Name toAsciiJavaName(Name fieldName) {
    return fieldName.map(
        str ->
            str.replaceAll(ILLEGAL_FIELD_CHARACTERS_PATTERN + "+", "_")
                .replaceAll("_+", "_")
                .replaceFirst("^([0-9])", "_$1"));
  }

  private static PList<Character> asCharactersList(String name) {
    return PList.generate(
            name.trim(),
            n -> n.isEmpty() ? Optional.empty() : Optional.of(Pair.of(n.substring(1), n.charAt(0))))
        .reverse();
  }
}
