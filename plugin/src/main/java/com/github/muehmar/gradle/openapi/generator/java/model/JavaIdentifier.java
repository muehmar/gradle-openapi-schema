package com.github.muehmar.gradle.openapi.generator.java.model;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class JavaIdentifier {
  private static final String ILLEGAL_IDENTIFIER_CHARACTERS_PATTERN = "[^A-Za-z0-9$_]";
  private final String identifier;

  private static final PList<String> JAVA_KEYWORDS =
      PList.of(
          "abstract",
          "continue",
          "for",
          "new",
          "switch",
          "assert",
          "default",
          "goto",
          "package",
          "synchronized",
          "boolean",
          "do",
          "if",
          "private",
          "this",
          "break",
          "double",
          "implements",
          "protected",
          "throw",
          "byte",
          "else",
          "import",
          "public",
          "throws",
          "case",
          "enum",
          "instanceof",
          "return",
          "transient",
          "catch",
          "extends",
          "int",
          "short",
          "try",
          "char",
          "final",
          "interface",
          "static",
          "void",
          "class",
          "finally",
          "long",
          "strictfp",
          "volatile",
          "const",
          "float",
          "native",
          "super",
          "while");

  private static final PList<String> JAVA_LITERALS = PList.of("true", "false", "null");

  private JavaIdentifier(String identifier) {
    this.identifier = identifier;
  }

  public static JavaIdentifier fromString(String name) {
    final String mappedName = escapeReservedKeywords(toValidIdentifierPattern(name));
    return new JavaIdentifier(mappedName);
  }

  private static String escapeReservedKeywords(String name) {
    return JAVA_KEYWORDS
        .concat(JAVA_LITERALS)
        .find(name::equals)
        .map(n -> n.concat("_"))
        .orElse(name);
  }

  private static String toValidIdentifierPattern(String name) {
    return name.replaceAll(ILLEGAL_IDENTIFIER_CHARACTERS_PATTERN.concat("+"), "_")
        .replaceAll("_+", "_")
        .replaceFirst("^(\\d)", "_$1");
  }

  public static JavaIdentifier fromName(Name name) {
    return fromString(name.asString());
  }

  public String wordBoundaryPattern() {
    return String.format("\\b%s\\b", identifier);
  }

  public JavaIdentifier startLowercase() {
    return new JavaIdentifier(Name.ofString(identifier).startLowerCase().asString());
  }

  public String asString() {
    return identifier;
  }

  @Override
  public String toString() {
    return asString();
  }
}
