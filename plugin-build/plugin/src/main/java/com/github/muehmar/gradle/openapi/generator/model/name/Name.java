package com.github.muehmar.gradle.openapi.generator.model.name;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;

/** General-purpose name. */
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

  public boolean equalsIgnoreCase(Name other) {
    return value.equalsIgnoreCase(other.value);
  }

  public Name startUpperCase() {
    return map(s -> s.substring(0, 1).toUpperCase() + s.substring(1));
  }

  public Name startLowerCase() {
    return map(s -> s.substring(0, 1).toLowerCase() + s.substring(1));
  }

  public boolean contains(CharSequence s) {
    return value.contains(s);
  }

  public boolean startsNotWith(String prefix) {
    return not(value.startsWith(prefix));
  }

  @Override
  public String toString() {
    return asString();
  }
}
