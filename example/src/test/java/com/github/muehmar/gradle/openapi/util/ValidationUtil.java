package com.github.muehmar.gradle.openapi.util;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class ValidationUtil {
  private ValidationUtil() {}

  public static <T> Set<ConstraintViolation<T>> validate(T object) {
    try (ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory()) {
      final Validator validator = validatorFactory.getValidator();
      return validator.validate(object);
    }
  }
}
