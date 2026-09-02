package com.github.muehmar.gradle.openapi.issues.issue266;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

/**
 * Issue 266: the string-backed representation extends into containers. An out-of-range item of an
 * array of enums must not throw during deserialisation, is reported as a constraint violation on
 * the list element and round-trips unchanged on serialisation.
 */
class Issue266EnumArrayTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void deserialize_when_allArrayItemsValid_then_noViolationsAndEnumConstantsReturned()
      throws Exception {
    final ShipmentDto dto =
        MAPPER.readValue("{\"statuses\":[\"created\",\"shipped\"]}", ShipmentDto.class);

    final Set<ConstraintViolation<ShipmentDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
    assertEquals(
        Arrays.asList(ShipmentDto.StatusesEnum.CREATED, ShipmentDto.StatusesEnum.SHIPPED),
        dto.getStatusesOpt().orElse(Collections.emptyList()));
  }

  @Test
  void deserialize_when_arrayItemOutOfRange_then_noExceptionAndViolationOnListElement()
      throws Exception {
    final ShipmentDto dto =
        assertDoesNotThrow(
            () -> MAPPER.readValue("{\"statuses\":[\"created\",\"lost\"]}", ShipmentDto.class));

    final Set<ConstraintViolation<ShipmentDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("statuses_[1].<list element> -> must match \"created|shipped\""),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void listGetter_when_arrayItemOutOfRange_then_illegalArgumentException() throws Exception {
    // Accessing the enum-typed list getter without validating first surfaces the out-of-range
    // item as an IllegalArgumentException, analogous to the getter of a plain enum property.
    final ShipmentDto dto =
        MAPPER.readValue("{\"statuses\":[\"created\",\"lost\"]}", ShipmentDto.class);

    assertThrows(IllegalArgumentException.class, dto::getStatusesOpt);
  }

  @Test
  void serialize_when_arrayItemOutOfRange_then_rawValueRoundTrips() throws Exception {
    final ShipmentDto dto =
        MAPPER.readValue("{\"statuses\":[\"created\",\"lost\"]}", ShipmentDto.class);

    assertEquals("{\"statuses\":[\"created\",\"lost\"]}", MAPPER.writeValueAsString(dto));
  }
}
