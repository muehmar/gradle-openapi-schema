package com.github.muehmar.gradle.openapi.issues.issue394;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.HashMap;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * A required additional property with an untyped (any-type) nullable value schema: any value is
 * allowed, the key must be present and a present but {@code null} value is spec-valid.
 */
public class AnyNullableTypedApTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  // Builder

  @Test
  void builder_when_arbitraryValueSet_then_getterReturnsValue() {
    final AnyNullableTypedApDto dto =
        AnyNullableTypedApDto.fullBuilder().setName("n").setReqAp(42).build();

    assertEquals(Optional.of(42), dto.getReqAp());
  }

  @Test
  void builder_when_emptyOptionalSet_then_getterReturnsEmpty() {
    final AnyNullableTypedApDto dto =
        AnyNullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.empty()).build();

    assertEquals(Optional.empty(), dto.getReqAp());
  }

  // Validation

  @Test
  void validate_when_arbitraryValuePresent_then_valid() {
    final AnyNullableTypedApDto dto =
        AnyNullableTypedApDto.fullBuilder().setName("n").setReqAp(42).build();

    assertValid(dto);
  }

  @Test
  void validate_when_presentButNull_then_valid() {
    final AnyNullableTypedApDto dto =
        AnyNullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.empty()).build();

    assertValid(dto);
  }

  @Test
  void validate_when_keyAbsent_then_invalid() {
    final AnyNullableTypedApDto dto = new AnyNullableTypedApDto("n", new HashMap<>());

    assertInvalid(dto);
  }

  // Serialization

  @Test
  void serialize_when_valuePresent_then_valueInJson() throws Exception {
    final AnyNullableTypedApDto dto =
        AnyNullableTypedApDto.fullBuilder().setName("n").setReqAp(42).build();

    assertEquals("{\"name\":\"n\",\"reqAp\":42}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void serialize_when_presentButNull_then_nullInJson() throws Exception {
    final AnyNullableTypedApDto dto =
        AnyNullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.empty()).build();

    assertEquals("{\"name\":\"n\",\"reqAp\":null}", MAPPER.writeValueAsString(dto));
  }

  // Deserialization

  @Test
  void deserialize_when_valuePresent_then_validAndValueReturned() throws Exception {
    final AnyNullableTypedApDto dto =
        MAPPER.readValue("{\"name\":\"n\",\"reqAp\":42}", AnyNullableTypedApDto.class);

    assertEquals(Optional.of(42), dto.getReqAp());
    assertValid(dto);
  }

  @Test
  void deserialize_when_nullValue_then_validAndEmptyReturned() throws Exception {
    final AnyNullableTypedApDto dto =
        MAPPER.readValue("{\"name\":\"n\",\"reqAp\":null}", AnyNullableTypedApDto.class);

    assertEquals(Optional.empty(), dto.getReqAp());
    assertValid(dto);
  }

  @Test
  void deserialize_when_keyAbsent_then_invalid() throws Exception {
    final AnyNullableTypedApDto dto =
        MAPPER.readValue("{\"name\":\"n\"}", AnyNullableTypedApDto.class);

    assertInvalid(dto);
  }

  /** Assert validity via bean validation and via the package-private, internally used isValid(). */
  private static void assertValid(AnyNullableTypedApDto dto) {
    assertTrue(validate(dto).isEmpty(), "bean validation must report no violations");
    assertTrue(dto.isValid(), "the internal isValid() must report valid");
  }

  private static void assertInvalid(AnyNullableTypedApDto dto) {
    assertFalse(validate(dto).isEmpty(), "bean validation must report a violation");
    assertFalse(dto.isValid(), "the internal isValid() must report invalid");
  }
}
