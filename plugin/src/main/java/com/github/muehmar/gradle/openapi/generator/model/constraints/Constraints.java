package com.github.muehmar.gradle.openapi.generator.model.constraints;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class Constraints {
  private final Min min;
  private final Max max;
  private final DecimalMin decimalMin;
  private final DecimalMax decimalMax;
  private final Size size;
  private final Pattern pattern;
  private final Email email;

  private Constraints(
      Min min,
      Max max,
      DecimalMin decimalMin,
      DecimalMax decimalMax,
      Size size,
      Pattern pattern,
      Email email) {
    this.min = min;
    this.max = max;
    this.decimalMin = decimalMin;
    this.decimalMax = decimalMax;
    this.size = size;
    this.pattern = pattern;
    this.email = email;
  }

  public static Constraints empty() {
    return new Constraints(null, null, null, null, null, null, null);
  }

  public static Constraints ofMin(Min min) {
    return Constraints.empty().withMin(min);
  }

  public static Constraints ofMax(Max max) {
    return Constraints.empty().withMax(max);
  }

  public static Constraints ofMinAndMax(Min min, Max max) {
    return Constraints.empty().withMin(min).withMax(max);
  }

  public static Constraints ofSize(Size size) {
    return Constraints.empty().withSize(size);
  }

  public static Constraints ofPattern(Pattern pattern) {
    return Constraints.empty().withPattern(pattern);
  }

  public static Constraints ofEmail() {
    return Constraints.empty().withEmail(new Email());
  }

  public static Constraints ofDecimalMin(DecimalMin decimalMin) {
    return Constraints.empty().withDecimalMin(decimalMin);
  }

  public static Constraints ofDecimalMax(DecimalMax decimalMax) {
    return Constraints.empty().withDecimalMax(decimalMax);
  }

  public static Constraints ofDecimalMinAndMax(DecimalMin decimalMin, DecimalMax decimalMax) {
    return Constraints.empty().withDecimalMin(decimalMin).withDecimalMax(decimalMax);
  }

  public Constraints withMin(Min min) {
    return new Constraints(min, max, decimalMin, decimalMax, size, pattern, email);
  }

  public Constraints withMax(Max max) {
    return new Constraints(min, max, decimalMin, decimalMax, size, pattern, email);
  }

  public Constraints withSize(Size size) {
    return new Constraints(min, max, decimalMin, decimalMax, size, pattern, email);
  }

  public Constraints withPattern(Pattern pattern) {
    return new Constraints(min, max, decimalMin, decimalMax, size, pattern, email);
  }

  public Constraints withEmail(Email email) {
    return new Constraints(min, max, decimalMin, decimalMax, size, pattern, email);
  }

  public Constraints withDecimalMin(DecimalMin decimalMin) {
    return new Constraints(min, max, decimalMin, decimalMax, size, pattern, email);
  }

  public Constraints withDecimalMax(DecimalMax decimalMax) {
    return new Constraints(min, max, decimalMin, decimalMax, size, pattern, email);
  }

  public Constraints and(Constraints other) {
    return new Constraints(
        Optional.ofNullable(min).orElse(other.min),
        Optional.ofNullable(max).orElse(other.max),
        Optional.ofNullable(decimalMin).orElse(other.decimalMin),
        Optional.ofNullable(decimalMax).orElse(other.decimalMax),
        Optional.ofNullable(size).orElse(other.size),
        Optional.ofNullable(pattern).orElse(other.pattern),
        Optional.ofNullable(email).orElse(other.email));
  }

  public void onMin(Consumer<Min> onMin) {
    Optional.ofNullable(min).ifPresent(onMin);
  }

  public <R> Optional<R> onMinFn(Function<Min, R> onMin) {
    return Optional.ofNullable(min).map(onMin);
  }

  public void onMax(Consumer<Max> onMax) {
    Optional.ofNullable(max).ifPresent(onMax);
  }

  public <R> Optional<R> onMaxFn(Function<Max, R> onMax) {
    return Optional.ofNullable(max).map(onMax);
  }

  public void onDecimalMin(Consumer<DecimalMin> onDecimalMin) {
    Optional.ofNullable(decimalMin).ifPresent(onDecimalMin);
  }

  public <R> Optional<R> onDecimalMinFn(Function<DecimalMin, R> onDecimalMin) {
    return Optional.ofNullable(decimalMin).map(onDecimalMin);
  }

  public void onDecimalMax(Consumer<DecimalMax> onDecimalMax) {
    Optional.ofNullable(decimalMax).ifPresent(onDecimalMax);
  }

  public <R> Optional<R> onDecimalMaxFn(Function<DecimalMax, R> onDecimalMax) {
    return Optional.ofNullable(decimalMax).map(onDecimalMax);
  }

  public void onSize(Consumer<Size> onSize) {
    Optional.ofNullable(size).ifPresent(onSize);
  }

  public <R> Optional<R> onSizeFn(Function<Size, R> onSize) {
    return Optional.ofNullable(size).map(onSize);
  }

  public void onPattern(Consumer<Pattern> onPattern) {
    Optional.ofNullable(pattern).ifPresent(onPattern);
  }

  public <R> Optional<R> onPatternFn(Function<Pattern, R> onPattern) {
    return Optional.ofNullable(pattern).map(onPattern);
  }

  public void onEmail(Consumer<Email> onEmail) {
    Optional.ofNullable(email).ifPresent(onEmail);
  }

  public <R> Optional<R> onEmailFn(Function<Email, R> onEmail) {
    return Optional.ofNullable(email).map(onEmail);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Constraints that = (Constraints) o;
    return Objects.equals(min, that.min)
        && Objects.equals(max, that.max)
        && Objects.equals(decimalMin, that.decimalMin)
        && Objects.equals(decimalMax, that.decimalMax)
        && Objects.equals(size, that.size)
        && Objects.equals(pattern, that.pattern)
        && Objects.equals(email, that.email);
  }

  @Override
  public int hashCode() {
    return Objects.hash(min, max, decimalMin, decimalMax, size, pattern, email);
  }

  @Override
  public String toString() {
    return "Constraints{"
        + "min="
        + min
        + ", max="
        + max
        + ", decimalMin="
        + decimalMin
        + ", decimalMax="
        + decimalMax
        + ", size="
        + size
        + ", pattern="
        + pattern
        + ", email="
        + email
        + '}';
  }
}
