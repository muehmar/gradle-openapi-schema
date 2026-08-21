package com.github.muehmar.gradle.openapi.generator.java.model.name;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import lombok.EqualsAndHashCode;

/**
 * Name for Java identifiers, like member, method or classnames. A {@link JavaName} can be
 * constructed from any string and gets escaped to conform to the specification for identifiers.
 * Render the name as string with {@link JavaName#asString()} or {@link JavaName#toString()} will
 * result in the escaped string.<br>
 * <br>
 * Any operation like {@link JavaName#prefix(String)} is performed on the original string rather
 * than on the escaped string. This will produce identifiers which are as close as possible to the
 * original name, i.e. only do escaping if necessary. E.g. if a Java keyword is used as name and
 * gets prefixed, there is no longer the need to escape it.
 */
@EqualsAndHashCode
public class JavaName {
  private final Name originalName;
  private final JavaIdentifier identifier;

  private JavaName(Name name) {
    this.originalName = name;
    this.identifier = JavaIdentifier.fromName(name);
  }

  public static JavaName fromName(Name name) {
    return new JavaName(name);
  }

  public static JavaName fromString(String name) {
    return new JavaName(Name.ofString(name));
  }

  public JavaName prefix(String prefix) {
    return new JavaName(originalName.prefix(prefix));
  }

  public JavaName append(String append) {
    return new JavaName(originalName.append(append));
  }

  public JavaName startUpperCase() {
    return new JavaName(originalName.startUpperCase());
  }

  public JavaName startLowerCase() {
    return new JavaName(originalName.startLowerCase());
  }

  public JavaName prefixedMethodName(String prefix) {
    if (prefix.isEmpty()) {
      return startLowerCase();
    } else {
      return startUpperCase().prefix(prefix);
    }
  }

  public String wordBoundaryPattern() {
    return String.format("\\b%s\\b", identifier);
  }

  public Name getOriginalName() {
    return originalName;
  }

  public String asString() {
    return identifier.asString();
  }

  @Override
  public String toString() {
    return asString();
  }
}
