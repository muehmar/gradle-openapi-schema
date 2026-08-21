package com.github.muehmar.gradle.openapi.issues.issue394;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Wraps a DTO with a required nullable additional property: the wrapper's deep validation calls the
 * nested {@code isValid()} directly (bypassing the nested DTO's bean-validation annotations), so
 * all outcomes must also hold one level deep.
 */
public class NestedApWrapperTest {

  private static NestedApWrapperDto wrap(NullableTypedApDto nested) {
    return NestedApWrapperDto.fullBuilder().setNested(nested).build();
  }

  @Test
  void validate_when_nestedValuePresent_then_valid() {
    final NullableTypedApDto nested =
        NullableTypedApDto.fullBuilder().setName("n").setReqAp("v").build();

    assertValid(wrap(nested));
  }

  @Test
  void validate_when_nestedPresentButNull_then_valid() {
    final NullableTypedApDto nested =
        NullableTypedApDto.fullBuilder().setName("n").setReqAp(Optional.empty()).build();

    assertValid(wrap(nested));
  }

  @Test
  void validate_when_nestedKeyAbsent_then_invalid() {
    final NullableTypedApDto nested = new NullableTypedApDto("n", new HashMap<>());

    assertInvalid(wrap(nested));
  }

  @Test
  void validate_when_nestedWrongTypedValue_then_invalid() {
    final Map<String, Object> additionalProperties = new HashMap<>();
    additionalProperties.put("reqAp", 42);
    final NullableTypedApDto nested = new NullableTypedApDto("n", additionalProperties);

    assertInvalid(wrap(nested));
  }

  /** Assert validity via bean validation and via the package-private, internally used isValid(). */
  private static void assertValid(NestedApWrapperDto dto) {
    assertTrue(validate(dto).isEmpty(), "bean validation must report no violations");
    assertTrue(dto.isValid(), "the internal isValid() must report valid");
  }

  private static void assertInvalid(NestedApWrapperDto dto) {
    assertFalse(validate(dto).isEmpty(), "bean validation must report a violation");
    assertFalse(dto.isValid(), "the internal isValid() must report invalid");
  }
}
