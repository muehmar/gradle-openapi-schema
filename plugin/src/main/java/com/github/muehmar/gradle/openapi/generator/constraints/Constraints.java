package com.github.muehmar.gradle.openapi.generator.constraints;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class Constraints {
  private final Min min;
  private final Max max;
  private final Size size;
  private final Pattern pattern;

  private Constraints(Min min, Max max, Size size, Pattern pattern) {
    this.min = min;
    this.max = max;
    this.size = size;
    this.pattern = pattern;
  }

  public static Constraints empty() {
    return new Constraints(null, null, null, null);
  }

  public static Constraints ofMin(Min min) {
    return new Constraints(min, null, null, null);
  }

  public static Constraints ofMax(Max max) {
    return new Constraints(null, max, null, null);
  }

  public static Constraints ofSize(Size size) {
    return new Constraints(null, null, size, null);
  }

  public static Constraints ofPattern(Pattern pattern) {
    return new Constraints(null, null, null, pattern);
  }

  public Constraints withMin(Min min) {
    return new Constraints(min, max, size, pattern);
  }

  public Constraints withMax(Max max) {
    return new Constraints(min, max, size, pattern);
  }

  public Constraints withSize(Size size) {
    return new Constraints(min, max, size, pattern);
  }

  public Constraints withPattern(Pattern pattern) {
    return new Constraints(min, max, size, pattern);
  }

  public void onMin(Consumer<Min> onMin) {
    Optional.ofNullable(min).ifPresent(onMin);
  }

  public void onMax(Consumer<Max> onMax) {
    Optional.ofNullable(max).ifPresent(onMax);
  }

  public void onSize(Consumer<Size> onSize) {
    Optional.ofNullable(size).ifPresent(onSize);
  }

  public void onPattern(Consumer<Pattern> onPattern) {
    Optional.ofNullable(pattern).ifPresent(onPattern);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Constraints that = (Constraints) o;
    return Objects.equals(min, that.min)
        && Objects.equals(max, that.max)
        && Objects.equals(size, that.size)
        && Objects.equals(pattern, that.pattern);
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max, size, pattern);
  }

  @Override
  public String toString() {
    return "Constraints{"
        + "min="
        + min
        + ", max="
        + max
        + ", size="
        + size
        + ", pattern="
        + pattern
        + '}';
  }
}
