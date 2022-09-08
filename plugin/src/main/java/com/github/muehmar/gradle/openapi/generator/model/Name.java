package com.github.muehmar.gradle.openapi.generator.model;

import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;

/** Name of a pojo or a pojo member. */
@EqualsAndHashCode
public class Name {
  private final String value;

  private Name(String val) {
    this.value = val;
  }

  public static Name ofString(String val) {
    if (val == null || val.trim().isEmpty()) {
      throw new IllegalArgumentException("A name must not be null or empty");
    }

    return new Name(val);
  }

  public String asString() {
    return value;
  }

  public Name map(UnaryOperator<String> f) {
    return Name.ofString(f.apply(value));
  }

  public Name append(String append) {
    return new Name(value + append);
  }

  public Name append(Name append) {
    return new Name(value + append.value);
  }

  public Name prefix(String prefix) {
    return new Name(prefix + value);
  }

  public SuffixedName suffix(String suffix) {
    return SuffixedName.ofNameAndSuffix(this, suffix);
  }

  public boolean equalsIgnoreCase(Name other) {
    return value.equalsIgnoreCase(other.value);
  }

  public Name startUpperCase() {
    return map(s -> s.substring(0, 1).toUpperCase() + s.substring(1));
  }

  @Override
  public String toString() {
    return asString();
  }
}
