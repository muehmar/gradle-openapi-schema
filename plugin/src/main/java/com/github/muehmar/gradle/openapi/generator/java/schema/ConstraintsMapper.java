package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
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

  @SuppressWarnings("java:S4276")
  private static Constraints getSizeConstraints(
      Supplier<Integer> getMin, Supplier<Integer> getMax) {
    final Optional<Integer> minItems = Optional.ofNullable(getMin.get());
    final Optional<Integer> maxItems = Optional.ofNullable(getMax.get());

    return Optionals.combine(minItems, maxItems, Size::ofMin, Size::ofMax, Size::of)
        .map(Constraints::ofSize)
        .orElseGet(Constraints::empty);
  }

  public static Constraints getMinimumAndMaximum(Schema<?> schema) {
    final Optional<Min> min =
        Optional.ofNullable(schema.getMinimum()).map(BigDecimal::longValue).map(Min::new);
    final Optional<Max> max =
        Optional.ofNullable(schema.getMaximum()).map(BigDecimal::longValue).map(Max::new);

    return Optionals.combine(
            min, max, Constraints::ofMin, Constraints::ofMax, Constraints::ofMinAndMax)
        .orElseGet(Constraints::empty);
  }

  public static Constraints getDecimalMinimumAndMaximum(Schema<?> schema) {
    final Boolean inclusiveMin =
        Optional.ofNullable(schema.getExclusiveMinimum()).map(Booleans::negate).orElse(true);

    final Optional<DecimalMin> min =
        Optional.ofNullable(schema.getMinimum())
            .map(BigDecimal::toString)
            .map(DecimalMin::inclusive)
            .map(decimalMin -> decimalMin.withInclusiveMin(inclusiveMin));

    final Boolean inclusiveMax =
        Optional.ofNullable(schema.getExclusiveMaximum()).map(Booleans::negate).orElse(true);

    final Optional<DecimalMax> max =
        Optional.ofNullable(schema.getMaximum())
            .map(BigDecimal::toString)
            .map(DecimalMax::inclusive)
            .map(decimalMax -> decimalMax.withInclusiveMax(inclusiveMax));

    return Optionals.combine(
            min,
            max,
            Constraints::ofDecimalMin,
            Constraints::ofDecimalMax,
            Constraints::ofDecimalMinAndMax)
        .orElseGet(Constraints::empty);
  }
}
