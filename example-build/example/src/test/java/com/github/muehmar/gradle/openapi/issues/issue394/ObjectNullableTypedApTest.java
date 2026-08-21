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
 * A required additional property with an object-typed nullable value schema: validation must
 * cascade into the nested DTO.
 */
public class ObjectNullableTypedApTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  private static ObjectNullableTypedApPropertyDto propertyDto(String id) {
    return ObjectNullableTypedApPropertyDto.fullBuilder().setId(id).build();
  }

  // Builder

  @Test
  void builder_when_nestedDtoSet_then_getterReturnsValue() {
    final ObjectNullableTypedApDto dto =
        ObjectNullableTypedApDto.fullBuilder().setName("n").setReqAp(propertyDto("xy")).build();

    assertEquals(Optional.of(propertyDto("xy")), dto.getReqAp());
  }

  // Getter

  @Test
  void getter_when_wrongTypedValue_then_empty() {
    final ObjectNullableTypedApDto dto =
        new ObjectNullableTypedApDto("n", wrongTypedAdditionalProperties());

    assertEquals(Optional.empty(), dto.getReqAp());
  }

  // Validation

  @Test
  void validate_when_nestedDtoValid_then_valid() {
    final ObjectNullableTypedApDto dto =
        ObjectNullableTypedApDto.fullBuilder().setName("n").setReqAp(propertyDto("xy")).build();

    assertValid(dto);
  }

  @Test
  void validate_when_nestedDtoInvalid_then_invalid() {
    final ObjectNullableTypedApDto dto =
        ObjectNullableTypedApDto.fullBuilder().setName("n").setReqAp(propertyDto("x")).build();

    assertInvalid(dto);
  }

  @Test
  void validate_when_presentButNull_then_valid() {
    final ObjectNullableTypedApDto dto =
        ObjectNullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.empty()).build();

    assertValid(dto);
  }

  @Test
  void validate_when_keyAbsent_then_invalid() {
    final ObjectNullableTypedApDto dto = new ObjectNullableTypedApDto("n", new HashMap<>());

    assertInvalid(dto);
  }

  @Test
  void validate_when_wrongTypedValue_then_invalid() {
    final ObjectNullableTypedApDto dto =
        new ObjectNullableTypedApDto("n", wrongTypedAdditionalProperties());

    assertInvalid(dto);
  }

  // Serialization

  @Test
  void serialize_when_nestedDtoPresent_then_objectInJson() throws Exception {
    final ObjectNullableTypedApDto dto =
        ObjectNullableTypedApDto.fullBuilder().setName("n").setReqAp(propertyDto("xy")).build();

    assertEquals("{\"name\":\"n\",\"reqAp\":{\"id\":\"xy\"}}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void serialize_when_presentButNull_then_nullInJson() throws Exception {
    final ObjectNullableTypedApDto dto =
        ObjectNullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.empty()).build();

    assertEquals("{\"name\":\"n\",\"reqAp\":null}", MAPPER.writeValueAsString(dto));
  }

  // Deserialization

  @Test
  void deserialize_when_objectValue_then_validAndTypedDtoReturned() throws Exception {
    final ObjectNullableTypedApDto dto =
        MAPPER.readValue(
            "{\"name\":\"n\",\"reqAp\":{\"id\":\"xy\"}}", ObjectNullableTypedApDto.class);

    assertEquals(Optional.of(propertyDto("xy")), dto.getReqAp());
    assertValid(dto);
  }

  @Test
  void deserialize_when_nullValue_then_validAndEmptyReturned() throws Exception {
    final ObjectNullableTypedApDto dto =
        MAPPER.readValue("{\"name\":\"n\",\"reqAp\":null}", ObjectNullableTypedApDto.class);

    assertEquals(Optional.empty(), dto.getReqAp());
    assertValid(dto);
  }

  private static Map<String, Object> wrongTypedAdditionalProperties() {
    final Map<String, Object> additionalProperties = new HashMap<>();
    additionalProperties.put("reqAp", "not a dto");
    return additionalProperties;
  }

  /** Assert validity via bean validation and via the package-private, internally used isValid(). */
  private static void assertValid(ObjectNullableTypedApDto dto) {
    assertTrue(validate(dto).isEmpty(), "bean validation must report no violations");
    assertTrue(dto.isValid(), "the internal isValid() must report valid");
  }

  private static void assertInvalid(ObjectNullableTypedApDto dto) {
    assertFalse(validate(dto).isEmpty(), "bean validation must report a violation");
    assertFalse(dto.isValid(), "the internal isValid() must report invalid");
  }
}
