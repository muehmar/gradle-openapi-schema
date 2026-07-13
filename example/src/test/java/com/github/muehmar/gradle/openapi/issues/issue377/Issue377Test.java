package com.github.muehmar.gradle.openapi.issues.issue377;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * {@code minLength}/{@code maxLength} on a {@code type: string, format: binary} property maps to a
 * {@code byte[]} member. Bean Validation supports {@code @Size} on arrays, but the plugin treats
 * the size constraint as unsupported for byte arrays and drops the annotation (the emitted warning
 * even claims no validation code is generated at all, although the generated {@code isValid()} deep
 * validation does enforce the bounds).
 */
public class Issue377Test {

  @Test
  void validate_when_binaryDataLongerThanMaxLength_then_violation() {
    // maxLength is 4, 10 bytes are invalid
    final byte[] tenBytes = "0123456789".getBytes(StandardCharsets.UTF_8);
    final BinaryHolderDto dto =
        BinaryHolderDto.fullBuilder().setData(Optional.of(tenBytes)).build();

    assertFalse(
        ValidationUtil.validate(dto).isEmpty(),
        "Expected a violation for data with 10 bytes > maxLength 4");
  }
}
