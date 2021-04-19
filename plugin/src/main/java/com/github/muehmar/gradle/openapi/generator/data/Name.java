package com.github.muehmar.gradle.openapi.generator.data;

import java.util.Objects;
import java.util.function.UnaryOperator;

/** Name of a pojo or a pojo member. */
public class Name {
  private final String value;

  private Name(String val) {
    this.value = val;
  }

  public static Name of(String val) {
    if (val == null || val.trim().isEmpty()) {
      throw new IllegalArgumentException("A name must no be null or empty");
    }

    return new Name(val);
  }

  public static Name concat(Name n1, Name n2) {
    return n1.append(n2);
  }

  public String asString() {
    return value;
  }

  public Name map(UnaryOperator<String> f) {
    return Name.of(f.apply(value));
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
    return SuffixedName.of(this, suffix);
  }

  public boolean equalsIgnoreCase(Name other) {
    return value.equalsIgnoreCase(other.value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Name name = (Name) o;
    return Objects.equals(value, name.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "Name{" + "value='" + value + '\'' + '}';
  }
}
