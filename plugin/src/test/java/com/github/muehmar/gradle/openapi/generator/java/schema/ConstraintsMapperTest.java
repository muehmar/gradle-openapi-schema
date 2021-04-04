package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
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
  void getMinimumAndMaximum_when_maximumDefined_then_maxConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(new Schema<>().maximum(new BigDecimal(50)));

    assertEquals(Constraints.ofMax(new Max(50)), constraints);
  }

  @Test
  void getMinimumAndMaximum_when_bothDefined_then_minAndMaxConstraint() {
    final Constraints constraints =
        ConstraintsMapper.getMinimumAndMaximum(
            new Schema<>().minimum(new BigDecimal(10)).maximum(new BigDecimal(50)));

    assertEquals(Constraints.ofMinAndMax(new Min(10), new Max(50)), constraints);
  }
}