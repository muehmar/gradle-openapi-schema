package com.github.muehmar.gradle.openapi.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.github.muehmar.gradle.openapi.generator.settings.ValidationApi;
import org.gradle.api.Action;
import org.gradle.api.InvalidUserDataException;
import org.gradle.api.model.ObjectFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class ValidationConfigTest {

  @Test
  void allUndefined_when_called_then_allFieldsAreNull() {
    final ValidationConfig config = ValidationConfig.allUndefined();

    assertFalse(config.getEnabled().isPresent());
    assertFalse(config.getValidationApiString().isPresent());
    assertFalse(config.getNonStrictOneOfValidation().isPresent());
  }

  @Test
  void getEnabledOrDefault_when_notSet_then_returnsFalse() {
    final ValidationConfig config = ValidationConfig.allUndefined();

    assertFalse(config.getEnabledOrDefault());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getEnabledOrDefault_when_set_then_returnsValue(boolean enabled) {
    final ValidationConfig config = ValidationConfig.allUndefined();
    config.setEnabled(enabled);

    assertEquals(enabled, config.getEnabledOrDefault());
  }

  @Test
  void getValidationApiOrDefault_when_notSet_then_returnsJakarta2() {
    final ValidationConfig config = ValidationConfig.allUndefined();

    assertEquals(ValidationApi.JAKARTA_2_0, config.getValidationApiOrDefault());
  }

  @Test
  void getValidationApiOrDefault_when_setToJakarta2_then_returnsJakarta2() {
    final ValidationConfig config = ValidationConfig.allUndefined();
    config.setValidationApi("jakarta-2");

    assertEquals(ValidationApi.JAKARTA_2_0, config.getValidationApiOrDefault());
  }

  @Test
  void getValidationApiOrDefault_when_setToJakarta20_then_returnsJakarta2() {
    final ValidationConfig config = ValidationConfig.allUndefined();
    config.setValidationApi("jakarta-2.0");

    assertEquals(ValidationApi.JAKARTA_2_0, config.getValidationApiOrDefault());
  }

  @Test
  void getValidationApiOrDefault_when_setToJakarta3_then_returnsJakarta3() {
    final ValidationConfig config = ValidationConfig.allUndefined();
    config.setValidationApi("jakarta-3");

    assertEquals(ValidationApi.JAKARTA_3_0, config.getValidationApiOrDefault());
  }

  @Test
  void getValidationApiOrDefault_when_setToJakarta30_then_returnsJakarta3() {
    final ValidationConfig config = ValidationConfig.allUndefined();
    config.setValidationApi("jakarta-3.0");

    assertEquals(ValidationApi.JAKARTA_3_0, config.getValidationApiOrDefault());
  }

  @Test
  void getValidationApiOrDefault_when_invalidValue_then_throwsException() {
    final ValidationConfig config = ValidationConfig.allUndefined();
    config.setValidationApi("invalid");

    assertThrows(InvalidUserDataException.class, config::getValidationApiOrDefault);
  }

  @Test
  void getNonStrictOneOfValidationOrDefault_when_notSet_then_returnsFalse() {
    final ValidationConfig config = ValidationConfig.allUndefined();

    assertFalse(config.getNonStrictOneOfValidationOrDefault());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getNonStrictOneOfValidationOrDefault_when_set_then_returnsValue(boolean value) {
    final ValidationConfig config = ValidationConfig.allUndefined();
    config.setNonStrictOneOfValidation(value);

    assertEquals(value, config.getNonStrictOneOfValidationOrDefault());
  }

  @Test
  void validationMethods_when_actionExecuted_then_configuresValidationMethods() {
    final ValidationConfig config = ValidationConfig.allUndefined();

    final Action<ValidationMethods> action =
        methods -> {
          methods.setModifier("public");
          methods.setGetterSuffix("Internal");
          methods.setDeprecatedAnnotation(true);
        };

    config.validationMethods(action);

    assertEquals("public", config.getValidationMethods().getModifier().get());
    assertEquals("Internal", config.getValidationMethods().getGetterSuffix().get());
    assertTrue(config.getValidationMethods().getDeprecatedAnnotation().get());
  }

  @Test
  void withCommonValidationConfig_when_nothingSet_then_usesCommonValues() {
    final ValidationConfig config = ValidationConfig.allUndefined();
    final ValidationConfig commonConfig = createValidationConfig();
    commonConfig.setEnabled(true);
    commonConfig.setValidationApi("jakarta-3.0");
    commonConfig.setNonStrictOneOfValidation(true);

    final ValidationConfig result = config.withCommonValidationConfig(commonConfig);

    assertTrue(result.getEnabled().get());
    assertEquals("jakarta-3.0", result.getValidationApiString().get());
    assertTrue(result.getNonStrictOneOfValidation().get());
  }

  @Test
  void withCommonValidationConfig_when_enabledAlreadySet_then_keepsOriginalValue() {
    final ValidationConfig config = ValidationConfig.allUndefined();
    config.setEnabled(false);

    final ValidationConfig commonConfig = createValidationConfig();
    commonConfig.setEnabled(true);

    final ValidationConfig result = config.withCommonValidationConfig(commonConfig);

    assertFalse(result.getEnabled().get());
  }

  @Test
  void withCommonValidationConfig_when_validationApiAlreadySet_then_keepsOriginalValue() {
    final ValidationConfig config = ValidationConfig.allUndefined();
    config.setValidationApi("jakarta-2.0");

    final ValidationConfig commonConfig = createValidationConfig();
    commonConfig.setValidationApi("jakarta-3.0");

    final ValidationConfig result = config.withCommonValidationConfig(commonConfig);

    assertEquals("jakarta-2.0", result.getValidationApiString().get());
  }

  @Test
  void withCommonValidationConfig_when_nonStrictAlreadySet_then_keepsOriginalValue() {
    final ValidationConfig config = ValidationConfig.allUndefined();
    config.setNonStrictOneOfValidation(false);

    final ValidationConfig commonConfig = createValidationConfig();
    commonConfig.setNonStrictOneOfValidation(true);

    final ValidationConfig result = config.withCommonValidationConfig(commonConfig);

    assertFalse(result.getNonStrictOneOfValidation().get());
  }

  @Test
  void withCommonValidationConfig_when_validationMethodsSet_then_mergesCorrectly() {
    final ValidationConfig config = ValidationConfig.allUndefined();
    config.validationMethods(methods -> methods.setModifier("public"));

    final ValidationConfig commonConfig = ValidationConfig.allUndefined();
    commonConfig.validationMethods(
        methods -> {
          methods.setModifier("private");
          methods.setGetterSuffix("Common");
        });

    final ValidationConfig result = config.withCommonValidationConfig(commonConfig);

    assertEquals("public", result.getValidationMethods().getModifier().get());
    assertEquals("Common", result.getValidationMethods().getGetterSuffix().get());
  }

  @Test
  void withCommonValidationConfig_when_onlyCommonHasValidationMethods_then_usesCommonValues() {
    final ValidationConfig config = ValidationConfig.allUndefined();

    final ValidationConfig commonConfig = ValidationConfig.allUndefined();
    commonConfig.validationMethods(
        methods -> {
          methods.setModifier("protected");
          methods.setGetterSuffix("Internal");
        });

    final ValidationConfig result = config.withCommonValidationConfig(commonConfig);

    assertEquals("protected", result.getValidationMethods().getModifier().get());
    assertEquals("Internal", result.getValidationMethods().getGetterSuffix().get());
  }

  private static ValidationConfig createValidationConfig() {
    return new ValidationConfig(createMockObjectFactory());
  }

  private static ObjectFactory createMockObjectFactory() {
    final ObjectFactory objectFactory = mock(ObjectFactory.class);
    when(objectFactory.newInstance(any()))
        .thenAnswer(
            invocation -> {
              Class<?> type = invocation.getArgument(0);
              if (type.equals(ValidationConfig.class)) {
                return new ValidationConfig(createMockObjectFactory());
              } else if (type.equals(ValidationMethods.class)) {
                return new ValidationMethods();
              }
              throw new IllegalArgumentException("Unexpected type: " + type);
            });
    return objectFactory;
  }
}
