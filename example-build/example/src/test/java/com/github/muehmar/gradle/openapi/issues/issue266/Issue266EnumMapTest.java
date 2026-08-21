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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

/**
 * Issue 266: the string-backed representation extends into map value types. The conversion between
 * the internal {@code String} and the enum has to be applied for every value of the map in both
 * directions, and the value validation applies to every entry.
 */
class Issue266EnumMapTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void deserialize_when_allMapValuesValid_then_noViolationsAndEnumConstantsReturned()
      throws Exception {
    final InventoryDto dto =
        MAPPER.readValue(
            "{\"stock\":{\"apple\":\"available\",\"pear\":\"sold-out\"}}", InventoryDto.class);

    final Set<ConstraintViolation<InventoryDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());

    final Map<String, InventoryDto.StockEnum> expected = new LinkedHashMap<>();
    expected.put("apple", InventoryDto.StockEnum.AVAILABLE);
    expected.put("pear", InventoryDto.StockEnum.SOLD_OUT);
    assertEquals(expected, dto.getStockOpt().orElse(Collections.emptyMap()));
  }

  @Test
  void deserialize_when_mapValueOutOfRange_then_noExceptionAndViolationOnMapValue()
      throws Exception {
    final InventoryDto dto =
        assertDoesNotThrow(
            () ->
                MAPPER.readValue(
                    "{\"stock\":{\"apple\":\"available\",\"pear\":\"reserved\"}}",
                    InventoryDto.class));

    final Set<ConstraintViolation<InventoryDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("stock[pear].<map value> -> must match \"available|sold-out\""),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void mapGetter_when_mapValueOutOfRange_then_illegalArgumentException() throws Exception {
    // Accessing the enum-typed map getter without validating first surfaces the out-of-range
    // value as an IllegalArgumentException, analogous to the getter of a plain enum property.
    final InventoryDto dto =
        MAPPER.readValue("{\"stock\":{\"pear\":\"reserved\"}}", InventoryDto.class);

    assertThrows(IllegalArgumentException.class, dto::getStockOpt);
  }

  @Test
  void serialize_when_mapSetViaBuilder_then_enumValuesSerialized() throws Exception {
    // The setter accepts the enum api type and stores the internal String per map value.
    final Map<String, InventoryDto.StockEnum> stock = new LinkedHashMap<>();
    stock.put("apple", InventoryDto.StockEnum.AVAILABLE);

    final InventoryDto dto = InventoryDto.fullInventoryDtoBuilder().setStock(stock).build();

    assertEquals(stock, dto.getStockOpt().orElse(Collections.emptyMap()));
    assertEquals("{\"stock\":{\"apple\":\"available\"}}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void serialize_when_mapValueOutOfRange_then_rawValueRoundTrips() throws Exception {
    final InventoryDto dto =
        MAPPER.readValue("{\"stock\":{\"pear\":\"reserved\"}}", InventoryDto.class);

    assertEquals("{\"stock\":{\"pear\":\"reserved\"}}", MAPPER.writeValueAsString(dto));
  }
}
