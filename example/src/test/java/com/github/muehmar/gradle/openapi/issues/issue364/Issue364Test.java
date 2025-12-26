package com.github.muehmar.gradle.openapi.issues.issue364;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

class Issue364Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_validAdditionalProperty_then_noViolationsAndValueReturned() throws Exception {
    final TypeMappedAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"id\":123,\"message\":\"hello\"}", TypeMappedAdditionalPropertiesDto.class);

    assertEquals(Optional.of(CustomString.create("hello")), dto.getAdditionalProperty("message"));

    final Set<ConstraintViolation<TypeMappedAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_tooShortAdditionalProperty_then_violation() throws Exception {
    final TypeMappedAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"id\":123,\"message\":\"hi\"}", TypeMappedAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<TypeMappedAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "size must be between 3 and 20", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_tooLongAdditionalProperty_then_violation() throws Exception {
    final TypeMappedAdditionalPropertiesDto dto =
        MAPPER.readValue(
            "{\"id\":123,\"message\":\"this-is-a-very-long-message-that-exceeds-max-length\"}",
            TypeMappedAdditionalPropertiesDto.class);

    final Set<ConstraintViolation<TypeMappedAdditionalPropertiesDto>> violations = validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "size must be between 3 and 20", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void serialize_when_dtoWithTypeMappedAdditionalProperties_then_correctJson() throws Exception {
    final TypeMappedAdditionalPropertiesDto dto =
        TypeMappedAdditionalPropertiesDto.builder()
            .setId(456)
            .andAllOptionals()
            .addAdditionalProperty("greeting", CustomString.create("hello"))
            .addAdditionalProperty("farewell", CustomString.create("goodbye"))
            .build();
    final String json = MAPPER.writeValueAsString(dto);

    assertEquals("{\"id\":456,\"greeting\":\"hello\",\"farewell\":\"goodbye\"}", json);
  }

  @Test
  void deserialize_when_jsonWithAdditionalProperties_then_correctDto() throws Exception {
    final String json = "{\"id\":789,\"greeting\":\"hello\",\"farewell\":\"goodbye\"}";

    final TypeMappedAdditionalPropertiesDto dto =
        MAPPER.readValue(json, TypeMappedAdditionalPropertiesDto.class);

    final TypeMappedAdditionalPropertiesDto expectedDto =
        TypeMappedAdditionalPropertiesDto.builder()
            .setId(789)
            .andAllOptionals()
            .addAdditionalProperty("greeting", CustomString.create("hello"))
            .addAdditionalProperty("farewell", CustomString.create("goodbye"))
            .build();

    assertEquals(expectedDto, dto);
  }

  // Tests for TypeMappedAdditionalPropertiesWithNullable

  @Test
  void validate_when_validNullableAdditionalProperty_then_noViolationsAndValueReturned()
      throws Exception {
    final TypeMappedAdditionalPropertiesWithNullableDto dto =
        MAPPER.readValue(
            "{\"name\":\"test\",\"message\":\"hello\"}",
            TypeMappedAdditionalPropertiesWithNullableDto.class);

    assertEquals(
        Tristate.ofValue(CustomString.create("hello")), dto.getAdditionalProperty("message"));

    final Set<ConstraintViolation<TypeMappedAdditionalPropertiesWithNullableDto>> violations =
        validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_nullableAdditionalPropertyIsNull_then_noViolations() throws Exception {
    final TypeMappedAdditionalPropertiesWithNullableDto dto =
        MAPPER.readValue(
            "{\"name\":\"test\",\"message\":null}",
            TypeMappedAdditionalPropertiesWithNullableDto.class);

    assertEquals(Tristate.ofNull(), dto.getAdditionalProperty("message"));

    final Set<ConstraintViolation<TypeMappedAdditionalPropertiesWithNullableDto>> violations =
        validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_nullableAdditionalPropertyAbsent_then_noViolations() {
    final TypeMappedAdditionalPropertiesWithNullableDto dto =
        TypeMappedAdditionalPropertiesWithNullableDto.builder()
            .setName(CustomString.create("test"))
            .build();

    assertEquals(Tristate.ofAbsent(), dto.getAdditionalProperty("message"));

    final Set<ConstraintViolation<TypeMappedAdditionalPropertiesWithNullableDto>> violations =
        validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_nullableAdditionalPropertyTooShort_then_violation() throws Exception {
    final TypeMappedAdditionalPropertiesWithNullableDto dto =
        MAPPER.readValue(
            "{\"name\":\"test\",\"message\":\"x\"}",
            TypeMappedAdditionalPropertiesWithNullableDto.class);

    final Set<ConstraintViolation<TypeMappedAdditionalPropertiesWithNullableDto>> violations =
        validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "size must be between 2 and 15", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_nullableAdditionalPropertyTooLong_then_violation() throws Exception {
    final TypeMappedAdditionalPropertiesWithNullableDto dto =
        MAPPER.readValue(
            "{\"name\":\"test\",\"message\":\"this-is-too-long-message\"}",
            TypeMappedAdditionalPropertiesWithNullableDto.class);

    final Set<ConstraintViolation<TypeMappedAdditionalPropertiesWithNullableDto>> violations =
        validate(dto);

    assertEquals(1, violations.size());
    assertEquals(
        "size must be between 2 and 15", violations.stream().findFirst().get().getMessage());
    assertFalse(dto.isValid());
  }

  @Test
  void serialize_when_nullableAdditionalPropertiesWithValue_then_correctJson() throws Exception {
    final TypeMappedAdditionalPropertiesWithNullableDto dto =
        TypeMappedAdditionalPropertiesWithNullableDto.builder()
            .setName(CustomString.create("test"))
            .andAllOptionals()
            .addAdditionalProperty("key1", CustomString.create("value1"))
            .addAdditionalProperty("key2", Tristate.ofValue(CustomString.create("value2")))
            .build();
    final String json = MAPPER.writeValueAsString(dto);

    assertEquals("{\"name\":\"test\",\"key1\":\"value1\",\"key2\":\"value2\"}", json);
  }

  @Test
  void serialize_when_nullableAdditionalPropertiesWithNull_then_correctJson() throws Exception {
    final TypeMappedAdditionalPropertiesWithNullableDto dto =
        TypeMappedAdditionalPropertiesWithNullableDto.builder()
            .setName(CustomString.create("test"))
            .andAllOptionals()
            .addAdditionalProperty("key1", CustomString.create("value1"))
            .addAdditionalProperty("key2", Tristate.ofNull())
            .build();
    final String json = MAPPER.writeValueAsString(dto);

    assertEquals("{\"name\":\"test\",\"key1\":\"value1\",\"key2\":null}", json);
  }

  @Test
  void deserialize_when_jsonWithNullableAdditionalProperties_then_correctDto() throws Exception {
    final String json = "{\"name\":\"test\",\"key1\":\"value1\",\"key2\":null}";

    final TypeMappedAdditionalPropertiesWithNullableDto dto =
        MAPPER.readValue(json, TypeMappedAdditionalPropertiesWithNullableDto.class);

    final TypeMappedAdditionalPropertiesWithNullableDto expectedDto =
        TypeMappedAdditionalPropertiesWithNullableDto.builder()
            .setName(CustomString.create("test"))
            .andAllOptionals()
            .addAdditionalProperty("key1", CustomString.create("value1"))
            .addAdditionalProperty("key2", Tristate.ofNull())
            .build();

    assertEquals(expectedDto, dto);
  }

  @Test
  void getAdditionalProperty_when_nullablePropertyExists_then_returnTristate() {
    final TypeMappedAdditionalPropertiesWithNullableDto dto =
        TypeMappedAdditionalPropertiesWithNullableDto.builder()
            .setName(CustomString.create("test"))
            .andAllOptionals()
            .addAdditionalProperty("key", CustomString.create("value"))
            .build();

    final Tristate<CustomString> result = dto.getAdditionalProperty("key");

    assertEquals(Tristate.ofValue(CustomString.create("value")), result);
  }

  @Test
  void getAdditionalProperty_when_nullablePropertyIsNull_then_returnTristateNull() {
    final TypeMappedAdditionalPropertiesWithNullableDto dto =
        TypeMappedAdditionalPropertiesWithNullableDto.builder()
            .setName(CustomString.create("test"))
            .andAllOptionals()
            .addAdditionalProperty("key", Tristate.ofNull())
            .build();

    final Tristate<CustomString> result = dto.getAdditionalProperty("key");

    assertEquals(Tristate.ofNull(), result);
  }

  @Test
  void getAdditionalProperty_when_nullablePropertyDoesNotExist_then_returnTristateAbsent() {
    final TypeMappedAdditionalPropertiesWithNullableDto dto =
        TypeMappedAdditionalPropertiesWithNullableDto.builder()
            .setName(CustomString.create("test"))
            .build();

    final Tristate<CustomString> result = dto.getAdditionalProperty("nonexistent");

    assertEquals(Tristate.ofAbsent(), result);
  }
}
