package com.github.muehmar.gradle.openapi.issues.issue375;

import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.github.muehmar.gradle.openapi.util.ValidationUtil;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

/**
 * Constraints on elements of nested containers (array of arrays) must be enforced by bean
 * validation. The generated type annotations stop at the first generic level (e.g. {@code
 * List<@NotNull List<PetDto>>} without {@code @Valid} on the inner element type), so violations of
 * second-level elements are silently missed, although the generated {@code isValid()} validation
 * methods do detect them.
 */
public class Issue375Test {

  @Test
  void validate_when_nestedPetViolatesMinLength_then_violation() {
    // name has minLength 2, "x" is invalid
    final PetDto invalidPet = PetDto.fullBuilder().setName("x").build();
    final PetMatrixDto dto =
        PetMatrixDto.fullBuilder()
            .setPets(Optional.of(singletonList(singletonList(invalidPet))))
            .setMatrix(Optional.empty())
            .build();

    assertFalse(
        ValidationUtil.validate(dto).isEmpty(),
        "Expected a violation for pets[0][0].name with length < 2");
  }

  @Test
  void validate_when_nestedIntegerAboveMaximum_then_violation() {
    // matrix items have maximum 100, 101 is invalid
    final List<List<Integer>> matrix = singletonList(singletonList(101));
    final PetMatrixDto dto =
        PetMatrixDto.fullBuilder().setPets(Optional.empty()).setMatrix(Optional.of(matrix)).build();

    assertFalse(
        ValidationUtil.validate(dto).isEmpty(), "Expected a violation for matrix[0][0] > 100");
  }
}
