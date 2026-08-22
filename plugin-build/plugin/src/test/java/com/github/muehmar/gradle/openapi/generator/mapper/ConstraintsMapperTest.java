package com.github.muehmar.gradle.openapi.generator.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.media.Schema;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class ConstraintsMapperTest {
  @Test
  void getMinAndMaxItems_when_nothing_then_emptyConstraint() {
    final Constraints minAndMaxItems = ConstraintsMapper.getMinAndMaxItems(new Schema<>());

    assertEquals(Constraints.empty(), minAndMaxItems);
  }

  @Test
  void getMinAndMaxItems_when_minItemsDefined_then_minSize() {
    final Constraints minAndMaxItems =
        ConstraintsMapper.getMinAndMaxItems(new Schema<>().minItems(10));

    assertEquals(Constraints.ofSize(Size.ofMin(10)), minAndMaxItems);
  }

  @Test
  void getMinAndMaxItems_when_maxItemsDefined_then_maxSize() {
    final Constraints minAndMaxItems =
        ConstraintsMapper.getMinAndMaxItems(new Schema<>().maxItems(50));

    assertEquals(Constraints.ofSize(Size.ofMax(50)), minAndMaxItems);
  }

  @Test
  void getMinAndMaxItems_when_bothDefined_then_fullSize() {
    final Constraints minAndMaxItems =
        ConstraintsMapper.getMinAndMaxItems(new Schema<>().minItems(10).maxItems(50));

    assertEquals(Constraints.ofSize(Size.of(10, 50)), minAndMaxItems);
  }

  @Test
  void getMinAndMaxLength_when_nothing_then_emptyConstraint() {
    final Constraints minAndMaxLength = ConstraintsMapper.getMinAndMaxLength(new Schema<>());

    assertEquals(Constraints.empty(), minAndMaxLength);
  }

  @Test
  void getMinAndMaxLength_when_minLengthDefined_then_minSize() {
    final Constraints minAndMaxLength =
        ConstraintsMapper.getMinAndMaxLength(new Schema<>().minLength(10));

    assertEquals(Constraints.ofSize(Size.ofMin(10)), minAndMaxLength);
  }

  @Test
  void getMinAndMaxLength_when_maxLengthDefined_then_maxSize() {
    final Constraints minAndMaxLength =
        ConstraintsMapper.getMinAndMaxLength(new Schema<>().maxLength(50));

    assertEquals(Constraints.ofSize(Size.ofMax(50)), minAndMaxLength);
  }

  @Test
  void getMinAndMaxLength_when_bothDefined_then_fullSize() {
    final Constraints minAndMaxLength =
        ConstraintsMapper.getMinAndMaxLength(new Schema<>().minLength(10).maxLength(50));

    assertEquals(Constraints.ofSize(Size.of(10, 50)), minAndMaxLength);
  }

  @Test
  void getMinimumAndMaximum_when_nothing_then_emptyConstraint() {
    final Constraints minAndMaxItems = ConstraintsMapper.getMinimumAndMaximum(new Schema<>());

    assertEquals(Constraints.empty(), minAndMaxItems);
  }

  @Test
  void getMinimumAndMaximum_when_minimumDefined_then_minConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(new Schema<>().minimum(new BigDecimal(10)));

    assertEquals(Constraints.ofMin(new Min(10)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_minimumDefinedAndExclusiveMin_then_minConstraintIncrement() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().minimum(new BigDecimal(10)).exclusiveMinimum(true));

    assertEquals(Constraints.ofMin(new Min(11)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_maximumDefined_then_maxConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(new Schema<>().maximum(new BigDecimal(50)));

    assertEquals(Constraints.ofMax(new Max(50)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_maximumDefinedAndExclusiveMax_then_maxConstraintDecremented() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().maximum(new BigDecimal(50)).exclusiveMaximum(true));

    assertEquals(Constraints.ofMax(new Max(49)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_bothDefined_then_minAndMaxConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().minimum(new BigDecimal(10)).maximum(new BigDecimal(50)));

    assertEquals(Constraints.ofMinAndMax(new Min(10), new Max(50)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_v31AndMinimumDefined_then_minConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().specVersion(SpecVersion.V31).minimum(new BigDecimal(10)));

    assertEquals(Constraints.ofMin(new Min(10)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_v31AndMaximumDefined_then_maxConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().specVersion(SpecVersion.V31).maximum(new BigDecimal(25)));

    assertEquals(Constraints.ofMax(new Max(25)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_v31AndExclusiveMinimumDefined_then_minConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().specVersion(SpecVersion.V31).exclusiveMinimumValue(new BigDecimal(10)));

    assertEquals(Constraints.ofMin(new Min(11)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_v31AndExclusiveMaximumDefined_then_maxConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().specVersion(SpecVersion.V31).exclusiveMaximumValue(new BigDecimal(25)));

    assertEquals(Constraints.ofMax(new Max(24)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_fractionalMinimum_then_minRoundedUp() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(new Schema<>().minimum(new BigDecimal("5.5")));

    assertEquals(Constraints.ofMin(new Min(6)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_fractionalMaximum_then_maxRoundedDown() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(new Schema<>().maximum(new BigDecimal("100.5")));

    assertEquals(Constraints.ofMax(new Max(100)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_negativeFractionalMinimum_then_minRoundedUp() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(new Schema<>().minimum(new BigDecimal("-5.5")));

    assertEquals(Constraints.ofMin(new Min(-5)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_negativeFractionalMaximum_then_maxRoundedDown() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(new Schema<>().maximum(new BigDecimal("-5.5")));

    assertEquals(Constraints.ofMax(new Max(-6)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_fractionalExclusiveMinimumV30_then_minRoundedUp() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().minimum(new BigDecimal("5.5")).exclusiveMinimum(true));

    assertEquals(Constraints.ofMin(new Min(6)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_fractionalExclusiveMaximumV30_then_maxRoundedDown() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().maximum(new BigDecimal("100.5")).exclusiveMaximum(true));

    assertEquals(Constraints.ofMax(new Max(100)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_v31AndFractionalExclusiveMinimum_then_minRoundedUp() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>()
                .specVersion(SpecVersion.V31)
                .exclusiveMinimumValue(new BigDecimal("5.5")));

    assertEquals(Constraints.ofMin(new Min(6)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_v31AndFractionalExclusiveMaximum_then_maxRoundedDown() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>()
                .specVersion(SpecVersion.V31)
                .exclusiveMaximumValue(new BigDecimal("100.5")));

    assertEquals(Constraints.ofMax(new Max(100)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_v31AndFractionalMinimumAndMaximum_then_roundedTowardsValidRange() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>()
                .specVersion(SpecVersion.V31)
                .minimum(new BigDecimal("5.5"))
                .maximum(new BigDecimal("100.5")));

    assertEquals(Constraints.ofMinAndMax(new Min(6), new Max(100)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_smallFractionMinimum_then_minRoundedUpNotToNearest() {
    // 5.1 must round up to 6, not to the nearest integer 5
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(new Schema<>().minimum(new BigDecimal("5.1")));

    assertEquals(Constraints.ofMin(new Min(6)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_largeFractionMaximum_then_maxRoundedDownNotToNearest() {
    // 100.9 must round down to 100, not to the nearest integer 101
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(new Schema<>().maximum(new BigDecimal("100.9")));

    assertEquals(Constraints.ofMax(new Max(100)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_integralBoundWithDecimalScale_then_valueUnchanged() {
    // 5.0 / 100.0 are integral despite the scale, so the bounds stay as-is
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().minimum(new BigDecimal("5.0")).maximum(new BigDecimal("100.0")));

    assertEquals(Constraints.ofMinAndMax(new Min(5), new Max(100)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_exclusiveIntegralBoundWithDecimalScale_then_valueExcluded() {
    // 5.0 exclusive still has to exclude 5 itself
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().minimum(new BigDecimal("5.0")).exclusiveMinimum(true));

    assertEquals(Constraints.ofMin(new Min(6)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_negativeSmallFractionBounds_then_roundedTowardsValidRange() {
    // -5.1 as a minimum rounds up to -5; -100.9 as a maximum rounds down to -101
    assertEquals(
        Constraints.ofMin(new Min(-5)),
        ConstraintsMapper.getMinimumAndMaximum(new Schema<>().minimum(new BigDecimal("-5.1"))));
    assertEquals(
        Constraints.ofMax(new Max(-101)),
        ConstraintsMapper.getMinimumAndMaximum(new Schema<>().maximum(new BigDecimal("-100.9"))));
  }

  @Test
  void getMinimumAndMaximum_when_v31FractionalExclusiveBoundsJustOffInteger_then_roundedInwards() {
    assertEquals(
        Constraints.ofMin(new Min(6)),
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>()
                .specVersion(SpecVersion.V31)
                .exclusiveMinimumValue(new BigDecimal("5.1"))));
    assertEquals(
        Constraints.ofMax(new Max(100)),
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>()
                .specVersion(SpecVersion.V31)
                .exclusiveMaximumValue(new BigDecimal("100.9"))));
  }

  @Test
  void getMinimumAndMaximum_when_v31IntegralExclusiveBounds_then_valuesExcluded() {
    // the 3.1 numeric form must still exclude an integral bound itself
    assertEquals(
        Constraints.ofMin(new Min(6)),
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().specVersion(SpecVersion.V31).exclusiveMinimumValue(new BigDecimal(5))));
    assertEquals(
        Constraints.ofMax(new Max(99)),
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>()
                .specVersion(SpecVersion.V31)
                .exclusiveMaximumValue(new BigDecimal(100))));
  }

  @Test
  void getDecimalMinimumAndMaximum_when_nothing_then_emptyConstraint() {
    final Constraints minAndMaxItems =
        ConstraintsMapper.getDecimalMinimumAndMaximum(new Schema<>());

    assertEquals(Constraints.empty(), minAndMaxItems);
  }

  @Test
  void getDecimalMinimumAndMaximum_when_minimumDefined_then_decimalMinConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getDecimalMinimumAndMaximum(
            new Schema<>().minimum(new BigDecimal("10.10")));

    assertEquals(Constraints.ofDecimalMin(new DecimalMin("10.10", true)), constraints);
  }

  @Test
  void getDecimalMinimumAndMaximum_when_minimumExclusiveDefined_then_decimalMinConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getDecimalMinimumAndMaximum(
            new Schema<>().minimum(new BigDecimal("10.10")).exclusiveMinimum(true));

    assertEquals(Constraints.ofDecimalMin(new DecimalMin("10.10", false)), constraints);
  }

  @Test
  void getDecimalMinimumAndMaximum_when_maximumDefined_then_maxConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getDecimalMinimumAndMaximum(
            new Schema<>().maximum(new BigDecimal("50.50")));

    assertEquals(Constraints.ofDecimalMax(new DecimalMax("50.50", true)), constraints);
  }

  @Test
  void getDecimalMinimumAndMaximum_when_maximumExclusiveDefined_then_maxConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getDecimalMinimumAndMaximum(
            new Schema<>().maximum(new BigDecimal("50.50")).exclusiveMaximum(true));

    assertEquals(Constraints.ofDecimalMax(new DecimalMax("50.50", false)), constraints);
  }

  @Test
  void getDecimalMinimumAndMaximum_when_bothDefined_then_minAndMaxConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getDecimalMinimumAndMaximum(
            new Schema<>()
                .minimum(new BigDecimal("10.10"))
                .maximum(new BigDecimal("50.50"))
                .exclusiveMaximum(true));

    assertEquals(
        Constraints.ofDecimalMinAndMax(
            new DecimalMin("10.10", true), new DecimalMax("50.50", false)),
        constraints);
  }

  @Test
  void getDecimalMinimumAndMaximum_when_v31AndMinimumDefined_then_decimalMinConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getDecimalMinimumAndMaximum(
            new Schema<>().specVersion(SpecVersion.V31).minimum(new BigDecimal("10.10")));

    assertEquals(Constraints.ofDecimalMin(new DecimalMin("10.10", true)), constraints);
  }

  @Test
  void getDecimalMinimumAndMaximum_when_v31AndMaximumDefined_then_maxConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getDecimalMinimumAndMaximum(
            new Schema<>().specVersion(SpecVersion.V31).maximum(new BigDecimal("50.50")));

    assertEquals(Constraints.ofDecimalMax(new DecimalMax("50.50", true)), constraints);
  }

  @Test
  void getDecimalMinimumAndMaximum_when_v31AndExclusiveMinimumDefined_then_decimalMinConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getDecimalMinimumAndMaximum(
            new Schema<>()
                .specVersion(SpecVersion.V31)
                .exclusiveMinimumValue(new BigDecimal("10.10")));

    assertEquals(Constraints.ofDecimalMin(new DecimalMin("10.10", false)), constraints);
  }

  @Test
  void getDecimalMinimumAndMaximum_when_v31AndExclusiveMaximumDefined_then_maxConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getDecimalMinimumAndMaximum(
            new Schema<>()
                .specVersion(SpecVersion.V31)
                .exclusiveMaximumValue(new BigDecimal("50.50")));

    assertEquals(Constraints.ofDecimalMax(new DecimalMax("50.50", false)), constraints);
  }

  @Test
  void getMultipleOf_when_nothing_then_emptyConstraint() {
    final Constraints minAndMaxItems = ConstraintsMapper.getMultipleOf(new Schema<>());

    assertEquals(Constraints.empty(), minAndMaxItems);
  }

  @Test
  void getMultipleOf_when_minItemsDefined_then_minSize() {
    final Constraints constraints =
        ConstraintsMapper.getMultipleOf(new Schema<>().multipleOf(new BigDecimal("9.02")));

    assertEquals(Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("9.02"))), constraints);
  }

  @Test
  void getUniqueItems_when_nothing_then_emptyConstraint() {
    final Constraints minAndMaxItems = ConstraintsMapper.getUniqueItems(new Schema<>());

    assertEquals(Constraints.empty(), minAndMaxItems);
  }

  @Test
  void getUniqueItems_when_minItemsDefined_then_minSize() {
    final Constraints constraints =
        ConstraintsMapper.getUniqueItems(new Schema<>().uniqueItems(true));

    assertEquals(Constraints.ofUniqueItems(true), constraints);
  }
}
