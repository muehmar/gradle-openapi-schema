package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.util.Optionals;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import java.util.Optional;

public class ConstraintsMapper {
  private ConstraintsMapper() {}

  public static Constraints getMinAndMaxItems(Schema<?> schema) {
    final Optional<Integer> minItems = Optional.ofNullable(schema.getMinItems());
    final Optional<Integer> maxItems = Optional.ofNullable(schema.getMaxItems());

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
}
