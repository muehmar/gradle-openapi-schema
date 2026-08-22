package com.github.muehmar.gradle.openapi.issues.issue381;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.lang.reflect.Method;
import org.junit.jupiter.api.Test;

/**
 * A {@code formatTypeMapping} must be matched against the format declared in the spec, not against
 * the normalized format the parser falls back to. {@code IntegerSchema}/{@code NumberSchema}
 * collapse any unknown or missing format to {@code int32}/{@code float}, so a mapping for a custom
 * numeric format was silently ignored while a mapping for {@code float}/{@code int32} was wrongly
 * applied to properties declaring a different format (or none at all).
 *
 * <p>The getter return types are asserted via reflection so this test compiles both before and
 * after the fix.
 */
class Issue381Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void getAmount_when_declaredFormatDecimalIsMapped_then_returnsCustomDecimal() throws Exception {
    assertEquals(CustomDecimal.class, returnTypeOf("getAmount"));
  }

  @Test
  void getRate_when_declaredFormatFloatIsMapped_then_returnsCustomFloat() throws Exception {
    assertEquals(CustomFloat.class, returnTypeOf("getRate"));
  }

  @Test
  void getQuantity_when_noFormatDeclared_then_noNumericMappingIsApplied() throws Exception {
    assertEquals(Float.class, returnTypeOf("getQuantity"));
  }

  @Test
  void getCount_when_declaredFormatTimestampIsMapped_then_returnsCustomTimestamp()
      throws Exception {
    assertEquals(CustomTimestamp.class, returnTypeOf("getCount"));
  }

  @Test
  void getSequence_when_noFormatDeclared_then_noIntegerMappingIsApplied() throws Exception {
    assertEquals(Integer.class, returnTypeOf("getSequence"));
  }

  @Test
  void deserialize_when_mappedCustomFormats_then_valuesConvertedToCustomTypes() throws Exception {
    final PaymentDto dto =
        MAPPER.readValue(
            "{\"amount\":12.5,\"rate\":0.25,\"quantity\":3.5,\"count\":42,\"sequence\":7}",
            PaymentDto.class);

    assertEquals(
        CustomDecimal.fromFloat(12.5f), PaymentDto.class.getMethod("getAmount").invoke(dto));
    assertEquals(CustomFloat.fromFloat(0.25f), PaymentDto.class.getMethod("getRate").invoke(dto));
    assertEquals(Float.valueOf(3.5f), PaymentDto.class.getMethod("getQuantity").invoke(dto));
    assertEquals(
        CustomTimestamp.fromInteger(42), PaymentDto.class.getMethod("getCount").invoke(dto));
    assertEquals(Integer.valueOf(7), PaymentDto.class.getMethod("getSequence").invoke(dto));
  }

  private static Class<?> returnTypeOf(String getterName) throws NoSuchMethodException {
    final Method getter = PaymentDto.class.getMethod(getterName);
    return getter.getReturnType();
  }
}
