package com.github.muehmar.gradle.openapi.issues.issue381;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/**
 * A formatTypeMapping for a custom numeric format ({@code type: number, format: decimal}) must be
 * applied to properties declaring that format. The parser normalizes any unknown number format to
 * "float" and discards the declared format string, so the mapping for "decimal" is silently ignored
 * (and a mapping for "float" would even be wrongly applied to {@code format: decimal} properties).
 *
 * <p>The test uses reflection so it compiles both before and after the fix.
 */
public class Issue381Test {

  @Test
  void getAmount_when_decimalFormatMappedToCustomType_then_returnsCustomDecimal()
      throws NoSuchMethodException {
    final Method getAmount = PaymentDto.class.getMethod("getAmount");

    assertEquals(
        CustomDecimal.class,
        getAmount.getReturnType(),
        "formatTypeMapping for 'decimal' should be applied to the amount property");
  }
}
