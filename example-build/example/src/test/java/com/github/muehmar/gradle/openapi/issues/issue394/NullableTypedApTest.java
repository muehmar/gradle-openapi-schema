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
 * A required additional property with a plain nullable value schema: 'required' means the key must
 * be present, while a present but {@code null} value is spec-valid. The getter returns {@code
 * Optional<String>} and never a bare null.
 */
public class NullableTypedApTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  // Builder

  @Test
  void builder_when_valueSet_then_getterReturnsValue() {
    final NullableTypedApDto dto =
        NullableTypedApDto.fullBuilder().setName("n").setReqAp("v").build();

    assertEquals(Optional.of("v"), dto.getReqAp());
  }

  @Test
  void builder_when_optionalValueSet_then_getterReturnsValue() {
    final NullableTypedApDto dto =
        NullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.of("v")).build();

    assertEquals(Optional.of("v"), dto.getReqAp());
  }

  @Test
  void builder_when_emptyOptionalSet_then_getterReturnsEmpty() {
    final NullableTypedApDto dto =
        NullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.empty()).build();

    assertEquals(Optional.empty(), dto.getReqAp());
  }

  // Getter

  @Test
  void getter_when_wrongTypedValue_then_empty() {
    final NullableTypedApDto dto = new NullableTypedApDto("n", wrongTypedAdditionalProperties());

    assertEquals(Optional.empty(), dto.getReqAp());
  }

  // Validation

  @Test
  void validate_when_valuePresent_then_valid() {
    final NullableTypedApDto dto =
        NullableTypedApDto.fullBuilder().setName("n").setReqAp("v").build();

    assertValid(dto);
  }

  @Test
  void validate_when_presentButNull_then_valid() {
    final NullableTypedApDto dto =
        NullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.empty()).build();

    assertValid(dto);
  }

  @Test
  void validate_when_keyAbsent_then_invalid() {
    final NullableTypedApDto dto = new NullableTypedApDto("n", new HashMap<>());

    assertInvalid(dto);
  }

  @Test
  void validate_when_wrongTypedValue_then_invalid() {
    final NullableTypedApDto dto = new NullableTypedApDto("n", wrongTypedAdditionalProperties());

    assertInvalid(dto);
  }

  // Serialization

  @Test
  void serialize_when_valuePresent_then_valueInJson() throws Exception {
    final NullableTypedApDto dto =
        NullableTypedApDto.fullBuilder().setName("n").setReqAp("v").build();

    assertEquals("{\"name\":\"n\",\"reqAp\":\"v\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void serialize_when_presentButNull_then_nullInJson() throws Exception {
    final NullableTypedApDto dto =
        NullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.empty()).build();

    assertEquals("{\"name\":\"n\",\"reqAp\":null}", MAPPER.writeValueAsString(dto));
  }

  // Deserialization

  @Test
  void deserialize_when_valuePresent_then_validAndValueReturned() throws Exception {
    final NullableTypedApDto dto =
        MAPPER.readValue("{\"name\":\"n\",\"reqAp\":\"v\"}", NullableTypedApDto.class);

    assertEquals(Optional.of("v"), dto.getReqAp());
    assertValid(dto);
  }

  @Test
  void deserialize_when_nullValue_then_validAndEmptyReturned() throws Exception {
    final NullableTypedApDto dto =
        MAPPER.readValue("{\"name\":\"n\",\"reqAp\":null}", NullableTypedApDto.class);

    assertEquals(Optional.empty(), dto.getReqAp());
    assertValid(dto);
  }

  @Test
  void deserialize_when_keyAbsent_then_invalid() throws Exception {
    final NullableTypedApDto dto = MAPPER.readValue("{\"name\":\"n\"}", NullableTypedApDto.class);

    assertInvalid(dto);
  }

  private static Map<String, Object> wrongTypedAdditionalProperties() {
    final Map<String, Object> additionalProperties = new HashMap<>();
    additionalProperties.put("reqAp", 42);
    return additionalProperties;
  }

  /** Assert validity via bean validation and via the package-private, internally used isValid(). */
  private static void assertValid(NullableTypedApDto dto) {
    assertTrue(validate(dto).isEmpty(), "bean validation must report no violations");
    assertTrue(dto.isValid(), "the internal isValid() must report valid");
  }

  private static void assertInvalid(NullableTypedApDto dto) {
    assertFalse(validate(dto).isEmpty(), "bean validation must report a violation");
    assertFalse(dto.isValid(), "the internal isValid() must report invalid");
  }
}
