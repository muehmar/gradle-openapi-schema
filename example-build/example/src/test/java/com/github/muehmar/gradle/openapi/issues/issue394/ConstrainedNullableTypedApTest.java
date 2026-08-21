package com.github.muehmar.gradle.openapi.issues.issue394;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * A required additional property with a constrained nullable value schema (minLength 3, maxLength
 * 8): the constraints must be enforced for a present value, while a present but {@code null} value
 * is spec-valid and skips the constraints.
 */
public class ConstrainedNullableTypedApTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  // Builder

  @Test
  void builder_when_valueSet_then_getterReturnsValue() {
    final ConstrainedNullableTypedApDto dto =
        ConstrainedNullableTypedApDto.fullBuilder().setName("n").setReqAp("abcd").build();

    assertEquals(Optional.of("abcd"), dto.getReqAp());
  }

  // Getter

  @Test
  void getter_when_wrongTypedValue_then_empty() {
    final ConstrainedNullableTypedApDto dto =
        new ConstrainedNullableTypedApDto("n", wrongTypedAdditionalProperties());

    assertEquals(Optional.empty(), dto.getReqAp());
  }

  // Validation

  @Test
  void validate_when_valueSatisfiesConstraints_then_valid() {
    final ConstrainedNullableTypedApDto dto =
        ConstrainedNullableTypedApDto.fullBuilder().setName("n").setReqAp("abcd").build();

    assertValid(dto);
  }

  @Test
  void validate_when_valueTooShort_then_invalid() {
    final ConstrainedNullableTypedApDto dto =
        ConstrainedNullableTypedApDto.fullBuilder().setName("n").setReqAp("ab").build();

    assertInvalid(dto);
  }

  @Test
  void validate_when_valueTooLong_then_invalid() {
    final ConstrainedNullableTypedApDto dto =
        ConstrainedNullableTypedApDto.fullBuilder().setName("n").setReqAp("abcdefghij").build();

    assertInvalid(dto);
  }

  @Test
  void validate_when_presentButNull_then_valid() {
    final ConstrainedNullableTypedApDto dto =
        ConstrainedNullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.empty()).build();

    assertValid(dto);
  }

  @Test
  void validate_when_keyAbsent_then_invalid() {
    final ConstrainedNullableTypedApDto dto =
        new ConstrainedNullableTypedApDto("n", new HashMap<>());

    assertInvalid(dto);
  }

  @Test
  void validate_when_wrongTypedValue_then_invalid() {
    final ConstrainedNullableTypedApDto dto =
        new ConstrainedNullableTypedApDto("n", wrongTypedAdditionalProperties());

    assertInvalid(dto);
  }

  // Serialization

  @Test
  void serialize_when_valuePresent_then_valueInJson() throws Exception {
    final ConstrainedNullableTypedApDto dto =
        ConstrainedNullableTypedApDto.fullBuilder().setName("n").setReqAp("abcd").build();

    assertEquals("{\"name\":\"n\",\"reqAp\":\"abcd\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void serialize_when_presentButNull_then_nullInJson() throws Exception {
    final ConstrainedNullableTypedApDto dto =
        ConstrainedNullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.empty()).build();

    assertEquals("{\"name\":\"n\",\"reqAp\":null}", MAPPER.writeValueAsString(dto));
  }

  // Deserialization

  @Test
  void deserialize_when_valueViolatesConstraints_then_invalid() throws Exception {
    final ConstrainedNullableTypedApDto dto =
        MAPPER.readValue("{\"name\":\"n\",\"reqAp\":\"ab\"}", ConstrainedNullableTypedApDto.class);

    assertInvalid(dto);
  }

  @Test
  void deserialize_when_nullValue_then_validAndEmptyReturned() throws Exception {
    final ConstrainedNullableTypedApDto dto =
        MAPPER.readValue("{\"name\":\"n\",\"reqAp\":null}", ConstrainedNullableTypedApDto.class);

    assertEquals(Optional.empty(), dto.getReqAp());
    assertValid(dto);
  }

  private static Map<String, Object> wrongTypedAdditionalProperties() {
    final Map<String, Object> additionalProperties = new HashMap<>();
    additionalProperties.put("reqAp", 42);
    return additionalProperties;
  }

  /** Assert validity via bean validation and via the package-private, internally used isValid(). */
  private static void assertValid(ConstrainedNullableTypedApDto dto) {
    assertTrue(validate(dto).isEmpty(), "bean validation must report no violations");
    assertTrue(dto.isValid(), "the internal isValid() must report valid");
  }

  private static void assertInvalid(ConstrainedNullableTypedApDto dto) {
    assertFalse(validate(dto).isEmpty(), "bean validation must report a violation");
    assertFalse(dto.isValid(), "the internal isValid() must report invalid");
  }
}
