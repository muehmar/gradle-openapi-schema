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
