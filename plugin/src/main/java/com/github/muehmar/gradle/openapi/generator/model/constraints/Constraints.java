package com.github.muehmar.gradle.openapi.generator.model.constraints;

import com.github.muehmar.gradle.openapi.util.Optionals;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

@Value
@PojoBuilder
public class Constraints {
  Optional<Min> min;
  Optional<Max> max;
  Optional<DecimalMin> decimalMin;
  Optional<DecimalMax> decimalMax;
  Optional<Size> size;
  Optional<Pattern> pattern;
  Optional<Email> email;
  Optional<PropertyCount> propertyCount;
  Optional<MultipleOf> multipleOf;

  public static Constraints empty() {
    return new Constraints(
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.empty());
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

  public static Constraints ofPropertiesCount(PropertyCount propertyCount) {
    return ConstraintsBuilder.create().andOptionals().propertyCount(propertyCount).build();
  }

  public static Constraints ofMultipleOf(MultipleOf multipleOf) {
    return ConstraintsBuilder.create().andOptionals().multipleOf(multipleOf).build();
  }

  public Constraints withMin(Min min) {
    return new Constraints(
        Optional.ofNullable(min),
        max,
        decimalMin,
        decimalMax,
        size,
        pattern,
        email,
        propertyCount,
        multipleOf);
  }

  public Constraints withMax(Max max) {
    return new Constraints(
        min,
        Optional.ofNullable(max),
        decimalMin,
        decimalMax,
        size,
        pattern,
        email,
        propertyCount,
        multipleOf);
  }

  public Constraints withSize(Size size) {
    return new Constraints(
        min,
        max,
        decimalMin,
        decimalMax,
        Optional.ofNullable(size),
        pattern,
        email,
        propertyCount,
        multipleOf);
  }

  public Constraints withPattern(Pattern pattern) {
    return new Constraints(
        min,
        max,
        decimalMin,
        decimalMax,
        size,
        Optional.ofNullable(pattern),
        email,
        propertyCount,
        multipleOf);
  }

  public Constraints withEmail(Email email) {
    return new Constraints(
        min,
        max,
        decimalMin,
        decimalMax,
        size,
        pattern,
        Optional.ofNullable(email),
        propertyCount,
        multipleOf);
  }

  public Constraints withDecimalMin(DecimalMin decimalMin) {
    return new Constraints(
        min,
        max,
        Optional.ofNullable(decimalMin),
        decimalMax,
        size,
        pattern,
        email,
        propertyCount,
        multipleOf);
  }

  public Constraints withDecimalMax(DecimalMax decimalMax) {
    return new Constraints(
        min,
        max,
        decimalMin,
        Optional.ofNullable(decimalMax),
        size,
        pattern,
        email,
        propertyCount,
        multipleOf);
  }

  public Constraints and(Constraints other) {
    return new Constraints(
        Optionals.or(min, min),
        Optionals.or(max, other.max),
        Optionals.or(decimalMin, other.decimalMin),
        Optionals.or(decimalMax, other.decimalMax),
        Optionals.or(size, other.size),
        Optionals.or(pattern, other.pattern),
        Optionals.or(email, other.email),
        Optionals.or(propertyCount, other.propertyCount),
        Optionals.or(multipleOf, other.multipleOf));
  }

  public Optional<Min> getMin() {
    return min;
  }

  public Optional<Max> getMax() {
    return max;
  }

  public Optional<DecimalMin> getDecimalMin() {
    return decimalMin;
  }

  public Optional<DecimalMax> getDecimalMax() {
    return decimalMax;
  }

  public Optional<Size> getSize() {
    return size;
  }

  public Optional<Pattern> getPattern() {
    return pattern;
  }

  public Optional<Email> getEmail() {
    return email;
  }

  public <R> Optional<R> onMinFn(Function<Min, R> onMin) {
    return min.map(onMin);
  }

  public <R> Optional<R> onMaxFn(Function<Max, R> onMax) {
    return max.map(onMax);
  }

  public <R> Optional<R> onDecimalMinFn(Function<DecimalMin, R> onDecimalMin) {
    return decimalMin.map(onDecimalMin);
  }

  public <R> Optional<R> onDecimalMaxFn(Function<DecimalMax, R> onDecimalMax) {
    return decimalMax.map(onDecimalMax);
  }

  public <R> Optional<R> onSizeFn(Function<Size, R> onSize) {
    return size.map(onSize);
  }

  public <R> Optional<R> onPatternFn(Function<Pattern, R> onPattern) {
    return pattern.map(onPattern);
  }
}
