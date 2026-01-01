package com.github.muehmar.gradle.openapi.typemappingwithconversion;

import static com.github.muehmar.gradle.openapi.Lists.list;
import static com.github.muehmar.gradle.openapi.Maps.map;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.CustomStrings.customString;
import static com.github.muehmar.gradle.openapi.typemappingwithconversion.MapObjectDto.fullMapObjectDtoBuilder;
import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static com.github.muehmar.gradle.openapi.util.ViolationFormatter.formatViolations;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.util.JsonMapper;
import com.github.muehmar.gradle.openapi.util.MapperFactory;
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

public class MapObjectValidationTest {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void validate_when_everythingOk_then_noViolations() {
    final MapObjectDto mapObjectDto =
        fullMapObjectDtoBuilder()
            .setIdsMap(map("id-k-1", customString("id-v-1")))
            .setUsernamesMap(map("usernames-k-1", customString("names-v-1")))
            .setEmailsMap(map("emails-k-1", customString("mail-1234")))
            .setPhonesMap(map("phones-k-1", customString("phone-1234")))
            .build();

    final Set<ConstraintViolation<MapObjectDto>> violations = validate(mapObjectDto);

    assertEquals(0, violations.size());
    assertTrue(mapObjectDto.isValid());
  }

  @Test
  void validate_when_usernamesMapMissing_then_violation() throws Exception {
    final MapObjectDto mapObjectDto = MAPPER.readValue("{\"idsMap\":{}}", MapObjectDto.class);

    final Set<ConstraintViolation<MapObjectDto>> violations = validate(mapObjectDto);

    assertEquals(
        list("usernamesMapPresent -> usernamesMap is required but it is not present"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(mapObjectDto.isValid());
  }

  @Test
  void validate_when_emailsNull_then_violation() throws Exception {
    final MapObjectDto mapObjectDto =
        MAPPER.readValue(
            "{\"idsMap\":{},\"usernamesMap\":null,\"emailsMap\":null}", MapObjectDto.class);

    final Set<ConstraintViolation<MapObjectDto>> violations = validate(mapObjectDto);

    assertEquals(
        list("emailsMapNotNull -> emailsMap is required to be non-null but is null"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(mapObjectDto.isValid());
  }

  @Test
  void validate_when_idsValueTooShort_then_violation() throws Exception {
    final MapObjectDto mapObjectDto =
        MAPPER.readValue(
            "{\"idsMap\":{\"ids-k-1\":\"short\"},\"usernamesMap\":null}", MapObjectDto.class);

    final Set<ConstraintViolation<MapObjectDto>> violations = validate(mapObjectDto);

    assertEquals(
        list("idsMapRaw[ids-k-1].<map value> -> size must be between 6 and 10"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(mapObjectDto.isValid());
  }

  @Test
  void validate_when_usernamesValueTooShort_then_violation() throws Exception {
    final MapObjectDto mapObjectDto =
        MAPPER.readValue(
            "{\"idsMap\":{},\"usernamesMap\":{\"usernames-k-1\":\"short\"}}", MapObjectDto.class);

    final Set<ConstraintViolation<MapObjectDto>> violations = validate(mapObjectDto);

    assertEquals(
        list("usernamesMap[usernames-k-1].<map value> -> size must be between 9 and 12"),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(mapObjectDto.isValid());
  }

  @Test
  void validate_when_emailsValueDoesNotMatchPattern_then_violation() throws Exception {
    final MapObjectDto mapObjectDto =
        MAPPER.readValue(
            "{\"idsMap\":{},\"usernamesMap\":null,\"emailsMap\":{\"mail-k-1\":\"wrongPattern\"}}",
            MapObjectDto.class);

    final Set<ConstraintViolation<MapObjectDto>> violations = validate(mapObjectDto);

    assertEquals(
        list("emailsMap[mail-k-1].<map value> -> must match \"[a-z]+-[0-9]{4}\""),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(mapObjectDto.isValid());
  }

  @Test
  void validate_when_phonesValueDoesNotMatchPattern_then_violation() throws Exception {
    final MapObjectDto mapObjectDto =
        MAPPER.readValue(
            "{\"idsMap\":{},\"usernamesMap\":null,\"phonesMap\":{\"mail-k-1\":\"wrongPattern\"}}",
            MapObjectDto.class);

    final Set<ConstraintViolation<MapObjectDto>> violations = validate(mapObjectDto);

    assertEquals(
        list("phonesMap[mail-k-1].<map value> -> must match \"phone-[0-9]{4}\""),
        formatViolations(violations),
        String.join("\n", formatViolations(violations)));
    assertFalse(mapObjectDto.isValid());
  }
}
