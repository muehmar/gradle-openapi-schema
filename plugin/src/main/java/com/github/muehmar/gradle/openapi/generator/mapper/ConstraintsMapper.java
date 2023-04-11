package com.github.muehmar.gradle.openapi.generator.mapper;

import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.util.Booleans;
import com.github.muehmar.gradle.openapi.util.Optionals;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.Supplier;

public class ConstraintsMapper {
  private ConstraintsMapper() {}

  public static Constraints getPattern(Schema<?> schema) {
    return Optional.ofNullable(schema.getPattern())
        .map(Pattern::ofUnescapedString)
        .map(Constraints::ofPattern)
        .orElseGet(Constraints::empty);
  }

  public static Constraints getMinAndMaxLength(Schema<?> schema) {
    return getSizeConstraints(schema::getMinLength, schema::getMaxLength);
  }

  public static Constraints getMinAndMaxItems(Schema<?> schema) {
    return getSizeConstraints(schema::getMinItems, schema::getMaxItems);
  }

  public static Constraints getMultipleOf(Schema<?> schema) {
    return Optional.ofNullable(schema.getMultipleOf())
        .map(MultipleOf::new)
        .map(Constraints::ofMultipleOf)
        .orElseGet(Constraints::empty);
  }

  @SuppressWarnings("java:S4276")
  private static Constraints getSizeConstraints(
      Supplier<Integer> getMin, Supplier<Integer> getMax) {
    final Optional<Integer> minItems = Optional.ofNullable(getMin.get());
    final Optional<Integer> maxItems = Optional.ofNullable(getMax.get());

    return Optionals.combine(minItems, maxItems, Size::ofMin, Size::ofMax, Size::of)
        .map(Constraints::ofSize)
        .orElseGet(Constraints::empty);
  }

  public static Constraints getPropertyCountConstraints(Schema<?> schema) {
    final Optional<Integer> minProperties = Optional.ofNullable(schema.getMinProperties());
    final Optional<Integer> maxProperties = Optional.ofNullable(schema.getMaxProperties());

    return Optionals.combine(
            minProperties,
            maxProperties,
            PropertyCount::ofMinProperties,
            PropertyCount::ofMaxProperties,
            PropertyCount::ofMinAndMaxProperties)
        .map(Constraints::ofPropertiesCount)
        .orElseGet(Constraints::empty);
  }

  public static Constraints getMinimumAndMaximum(Schema<?> schema) {
    final Optional<Min> min =
        Optional.ofNullable(schema.getMinimum())
            .map(BigDecimal::longValue)
            .map(val -> isInclusiveMin(schema) ? val : val + 1)
            .map(Min::new);
    final Optional<Max> max =
        Optional.ofNullable(schema.getMaximum())
            .map(BigDecimal::longValue)
            .map(val -> isInclusiveMax(schema) ? val : val - 1)
            .map(Max::new);

    return Optionals.combine(
            min, max, Constraints::ofMin, Constraints::ofMax, Constraints::ofMinAndMax)
        .orElseGet(Constraints::empty);
  }

  public static Constraints getDecimalMinimumAndMaximum(Schema<?> schema) {

    final Optional<DecimalMin> min =
        Optional.ofNullable(schema.getMinimum())
            .map(BigDecimal::toString)
            .map(DecimalMin::inclusive)
            .map(decimalMin -> decimalMin.withInclusiveMin(isInclusiveMin(schema)));

    final Optional<DecimalMax> max =
        Optional.ofNullable(schema.getMaximum())
            .map(BigDecimal::toString)
            .map(DecimalMax::inclusive)
            .map(decimalMax -> decimalMax.withInclusiveMax(isInclusiveMax(schema)));

    return Optionals.combine(
            min,
            max,
            Constraints::ofDecimalMin,
            Constraints::ofDecimalMax,
            Constraints::ofDecimalMinAndMax)
        .orElseGet(Constraints::empty);
  }

  private static boolean isInclusiveMax(Schema<?> schema) {
    return Optional.ofNullable(schema.getExclusiveMaximum()).map(Booleans::not).orElse(true);
  }

  private static boolean isInclusiveMin(Schema<?> schema) {
    return Optional.ofNullable(schema.getExclusiveMinimum()).map(Booleans::not).orElse(true);
  }
}
