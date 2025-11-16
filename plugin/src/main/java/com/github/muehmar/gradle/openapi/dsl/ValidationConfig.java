package com.github.muehmar.gradle.openapi.dsl;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.ValidationApi;
import com.github.muehmar.gradle.openapi.util.Optionals;
import groovy.lang.Closure;
import java.io.Serializable;
import java.util.Optional;
import java.util.function.Supplier;
import javax.inject.Inject;
import org.gradle.api.Action;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.model.ObjectFactory;

public class ValidationConfig implements Serializable {
  private static final boolean DEFAULT_ENABLED = false;
  private static final ValidationApi DEFAULT_VALIDATION_API = ValidationApi.JAKARTA_2_0;
  private static final boolean DEFAULT_NON_STRICT_ONE_OF_VALIDATION = false;

  private Boolean enabled;
  private String validationApi;
  private Boolean nonStrictOneOfValidation;
  private final ValidationMethods validationMethods;

  @Inject
  public ValidationConfig(ObjectFactory objectFactory) {
    this.validationMethods = objectFactory.newInstance(ValidationMethods.class);
  }

  public static ValidationConfig allUndefined() {
    return new ValidationConfig(null, null, null, ValidationMethods.allUndefined());
  }

  private ValidationConfig(
      Boolean enabled,
      String validationApi,
      Boolean nonStrictOneOfValidation,
      ValidationMethods validationMethods) {
    this.enabled = enabled;
    this.validationApi = validationApi;
    this.nonStrictOneOfValidation = nonStrictOneOfValidation;
    this.validationMethods = validationMethods;
  }

  // DSL API
  public void setEnabled(Boolean enabled) {
    this.enabled = enabled;
  }

  // DSL API
  public void setValidationApi(String validationApi) {
    this.validationApi = validationApi;
  }

  // DSL API
  public void setNonStrictOneOfValidation(Boolean nonStrictOneOfValidation) {
    this.nonStrictOneOfValidation = nonStrictOneOfValidation;
  }

  // DSL API - Nested configuration with Action
  public void validationMethods(Action<? super ValidationMethods> action) {
    action.execute(validationMethods);
  }

  // DSL API - Nested configuration with Closure (Groovy support)
  public ValidationMethods validationMethods(Closure<?> closure) {
    return org.gradle.util.internal.ConfigureUtil.configureSelf(closure, validationMethods);
  }

  ValidationMethods getValidationMethods() {
    return validationMethods;
  }

  ValidationConfig withCommonValidationConfig(ValidationConfig commonValidationConfig) {
    final ValidationMethods mergedMethods =
        this.validationMethods.withCommonRawGetter(commonValidationConfig.validationMethods);
    return new ValidationConfig(
        Optionals.or(
                Optional.ofNullable(enabled), Optional.ofNullable(commonValidationConfig.enabled))
            .orElse(null),
        Optionals.or(
                Optional.ofNullable(validationApi),
                Optional.ofNullable(commonValidationConfig.validationApi))
            .orElse(null),
        Optionals.or(
                Optional.ofNullable(nonStrictOneOfValidation),
                Optional.ofNullable(commonValidationConfig.nonStrictOneOfValidation))
            .orElse(null),
        mergedMethods);
  }

  Optional<Boolean> getEnabled() {
    return Optional.ofNullable(enabled);
  }

  boolean getEnabledOrDefault() {
    return getEnabled().orElse(DEFAULT_ENABLED);
  }

  Optional<String> getValidationApiString() {
    return Optional.ofNullable(validationApi);
  }

  ValidationApi getValidationApiOrDefault() {
    final Supplier<InvalidUserDataException> unsupportedValueException =
        () ->
            new InvalidUserDataException(
                "Unsupported value for validationApi: '"
                    + validationApi
                    + "'. Supported values are ["
                    + PList.of(ValidationApi.values()).map(ValidationApi::getValue).mkString(", ")
                    + "]");
    return Optional.ofNullable(validationApi)
        .map(support -> ValidationApi.fromString(support).orElseThrow(unsupportedValueException))
        .orElse(DEFAULT_VALIDATION_API);
  }

  Optional<Boolean> getNonStrictOneOfValidation() {
    return Optional.ofNullable(nonStrictOneOfValidation);
  }

  boolean getNonStrictOneOfValidationOrDefault() {
    return getNonStrictOneOfValidation().orElse(DEFAULT_NON_STRICT_ONE_OF_VALIDATION);
  }
}
