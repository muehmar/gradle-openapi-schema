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
import java.util.Set;
import javax.validation.ConstraintViolation;
import org.junit.jupiter.api.Test;

/**
 * Issue 266: Improve deserialisation of enums.
 *
 * <p>An enum value which is not within the range of the defined enum constants must not throw an
 * exception during deserialisation. Instead, the value is accepted and a constraint violation is
 * raised when the DTO is validated.
 */
class Issue266Test {
  private static final JsonMapper MAPPER = MapperFactory.jsonMapper();

  @Test
  void deserialize_when_validEnumValue_then_noViolationsAndValueReturned() throws Exception {
    final OrderDto dto = MAPPER.readValue("{\"id\":\"1\",\"status\":\"shipped\"}", OrderDto.class);

    assertEquals(OrderDto.StatusEnum.SHIPPED, dto.getStatus());

    final Set<ConstraintViolation<OrderDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void deserialize_when_enumValueOutOfRange_then_noExceptionDuringDeserialisation() {
    // The central goal of issue 266: an unknown enum value must deserialize without throwing.
    assertDoesNotThrow(
        () -> MAPPER.readValue("{\"id\":\"1\",\"status\":\"cancelled\"}", OrderDto.class));
  }

  @Test
  void validate_when_enumValueOutOfRange_then_violation() throws Exception {
    final OrderDto dto =
        MAPPER.readValue("{\"id\":\"1\",\"status\":\"cancelled\"}", OrderDto.class);

    final Set<ConstraintViolation<OrderDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("statusRaw -> must match \"pending|shipped|delivered\""),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void getter_when_enumValueOutOfRange_then_illegalArgumentException() throws Exception {
    // Documented in the migration guide: if the DTO is not validated, an out-of-range value
    // surfaces as an IllegalArgumentException when the enum-typed getter is accessed.
    final OrderDto dto =
        MAPPER.readValue("{\"id\":\"1\",\"status\":\"cancelled\"}", OrderDto.class);

    assertThrows(IllegalArgumentException.class, dto::getStatus);
  }

  @Test
  void serialize_when_enumValueOutOfRange_then_rawValueRoundTrips() throws Exception {
    // The string-backed representation makes the DTO a tolerant pass-through: an out-of-range
    // value is serialized unchanged.
    final OrderDto dto =
        MAPPER.readValue("{\"id\":\"1\",\"status\":\"cancelled\"}", OrderDto.class);

    assertEquals("{\"id\":\"1\",\"status\":\"cancelled\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void validate_when_optionalEnumValueOutOfRange_then_violationWithPlainPropertyPath()
      throws Exception {
    // Optional enum properties keep the plain property name as violation path: their validation
    // getter is not suffixed since the api getter already carries the Opt suffix.
    final ReleaseDto dto =
        MAPPER.readValue("{\"version\":\"1.5\",\"codename\":\"xyz\"}", ReleaseDto.class);

    final Set<ConstraintViolation<ReleaseDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("codename -> must match \"a\\(b\""),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_referencedEnumValueValid_then_noViolations() throws Exception {
    final PaletteDto dto = MAPPER.readValue("{\"color\":\"red\"}", PaletteDto.class);

    final Set<ConstraintViolation<PaletteDto>> violations = validate(dto);

    assertEquals(0, violations.size());
    assertTrue(dto.isValid());
  }

  @Test
  void validate_when_referencedEnumValueOutOfRange_then_violation() throws Exception {
    // A referenced ($ref) enum is generated as its own top-level DTO and must be validated
    // against the allowed values like an inline enum.
    final PaletteDto dto = MAPPER.readValue("{\"color\":\"purple\"}", PaletteDto.class);

    final Set<ConstraintViolation<PaletteDto>> violations = validate(dto);

    assertEquals(
        Collections.singletonList("colorRaw -> must match \"red|green|blue\""),
        formatViolations(violations));
    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_enumMembersContainRegexMetacharacters_then_onlyLiteralValuesValid()
      throws Exception {
    // The dot in the member values must not act as a regex wildcard: "1x5" must be a violation.
    // The violation message shows the escaped member values.
    final ReleaseDto invalidDto = MAPPER.readValue("{\"version\":\"1x5\"}", ReleaseDto.class);

    final Set<ConstraintViolation<ReleaseDto>> violations = validate(invalidDto);

    assertEquals(
        Collections.singletonList("versionRaw -> must match \"1\\.5|2\\.5\""),
        formatViolations(violations));
    assertFalse(invalidDto.isValid());
  }

  @Test
  void deserialize_when_requiredEnumAdditionalProperty_then_enumTypedGetterReturnsConstant()
      throws Exception {
    // The required additional property 'severity' has an enum value schema: the getter is
    // enum-typed and converts the internally stored String via fromValue.
    final TicketDto dto =
        MAPPER.readValue("{\"severity\":\"red\",\"note\":\"blue\"}", TicketDto.class);

    assertEquals(ColorEnumDto.RED, dto.getSeverity());
  }

  @Test
  void serialize_when_requiredEnumAdditionalPropertySetViaBuilder_then_enumValueSerialized()
      throws Exception {
    // The additional-property setter accepts the enum api type and stores the internal String.
    final TicketDto dto =
        TicketDto.fullTicketDtoBuilder()
            .addAdditionalProperty("severity", ColorEnumDto.GREEN)
            .build();

    assertEquals(ColorEnumDto.GREEN, dto.getSeverity());
    assertEquals("{\"severity\":\"green\"}", MAPPER.writeValueAsString(dto));
  }

  @Test
  void additionalPropertyGetter_when_enumValueOutOfRange_then_illegalArgumentException()
      throws Exception {
    // Same contract as for regular properties: without validation, the enum-typed getter of an
    // additional property throws for an out-of-range value.
    final TicketDto dto = MAPPER.readValue("{\"severity\":\"urgent\"}", TicketDto.class);

    assertThrows(IllegalArgumentException.class, dto::getSeverity);
  }

  @Test
  void isValid_when_requiredEnumAdditionalPropertyOutOfRange_then_invalid() throws Exception {
    // The value validation of a required enum additional property runs against the internally
    // stored String, so an out-of-range value is reported as invalid instead of throwing.
    final TicketDto dto = MAPPER.readValue("{\"severity\":\"urgent\"}", TicketDto.class);

    assertFalse(dto.isValid());
  }

  @Test
  void validate_when_enumMemberContainsUnbalancedParenthesis_then_validValueHasNoViolations()
      throws Exception {
    // Without escaping, the member "a(b" yields an invalid regex and every validation throws a
    // PatternSyntaxException.
    final ReleaseDto dto =
        MAPPER.readValue("{\"version\":\"1.5\",\"codename\":\"a(b\"}", ReleaseDto.class);

    final Set<ConstraintViolation<ReleaseDto>> violations = assertDoesNotThrow(() -> validate(dto));

    assertEquals(0, violations.size());
    assertTrue(assertDoesNotThrow(dto::isValid));
  }
}
