package com.github.muehmar.gradle.openapi.identifiers;

import static com.github.muehmar.gradle.openapi.util.ValidationUtil.validate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import openapischema.example.api.identifiers.model.User1;
import openapischema.example.api.identifiers.model.User1OrUser2;
import openapischema.example.api.identifiers.model.User2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestValidation {
  @ParameterizedTest
  @ValueSource(strings = {"n", "ne", "new"})
  void validate_when_differentStringLength_then_violationsForSmallerThanThree(String value) {
    final User1 dto =
        User1.builder().setNew(value).andAllOptionals().setInterface(Optional.empty()).build();

    final Set<ConstraintViolation<User1>> constraintViolations = validate(dto);

    assertEquals(value.length() < 3, constraintViolations.size() == 1);
  }

  @ParameterizedTest
  @ValueSource(strings = {"publ", "publi", "public"})
  void validate_when_differentStringLength_then_violationsForSmallerThanSix(String value) {
    final User2 dto2 =
        User2.builder().setPublic(value).andAllOptionals().setNull(Tristate.ofNull()).build();
    final User1OrUser2 dto = User1OrUser2.builder().setUser2(dto2).build();

    final Set<ConstraintViolation<User1OrUser2>> constraintViolations = validate(dto);

    assertEquals(value.length() < 6, constraintViolations.size() == 1);
  }
}
