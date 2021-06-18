package com.github.muehmar.gradle.openapi.generator.constraints;

import java.util.Objects;
import java.util.function.UnaryOperator;

public class Pattern {
  private final String value;

  private Pattern(String value) {
    this.value = value;
  }

  public static Pattern ofUnescapedString(String value) {
    return new Pattern(value);
  }

  public String getPattern() {
    return value;
  }

  public String getPatternEscaped(UnaryOperator<String> escaper) {
    return escaper.apply(value);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Pattern pattern = (Pattern) o;
    return Objects.equals(value, pattern.value);
  }

  @Override
  public int hashCode() {
    return Objects.hash(value);
  }

  @Override
  public String toString() {
    return "Pattern{" + "value='" + value + '\'' + '}';
  }
}
