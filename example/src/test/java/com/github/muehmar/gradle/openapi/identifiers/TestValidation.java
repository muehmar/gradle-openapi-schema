package com.github.muehmar.gradle.openapi.identifiers;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.openapi.util.Tristate;
import java.util.Optional;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import openapischema.example.api.identifiers.model.User1;
import openapischema.example.api.identifiers.model.User1OrUser2;
import openapischema.example.api.identifiers.model.User2;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class TestValidation {
  @ParameterizedTest
  @ValueSource(strings = {"n", "ne", "new"})
  void validate_when_differentStringLength_then_violationsForSmallerThanThree(String value) {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();

    final User1 dto =
        User1.newBuilder().setNew(value).andAllOptionals().setInterface(Optional.empty()).build();

    final Set<ConstraintViolation<User1>> constraintViolations = validator.validate(dto);

    assertEquals(value.length() < 3, constraintViolations.size() == 1);
  }

  @ParameterizedTest
  @ValueSource(strings = {"publ", "publi", "public"})
  void validate_when_differentStringLength_then_violationsForSmallerThanSix(String value) {
    final ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    final Validator validator = validatorFactory.getValidator();
    final User2 dto2 =
        User2.newBuilder().setPublic(value).andAllOptionals().setNull(Tristate.ofNull()).build();
    final User1OrUser2 dto = User1OrUser2.fromUser2(dto2);

    final Set<ConstraintViolation<User1OrUser2>> constraintViolations = validator.validate(dto);

    assertEquals(value.length() < 6, constraintViolations.size() == 1);
  }
}
