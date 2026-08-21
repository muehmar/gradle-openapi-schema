package com.github.muehmar.gradle.openapi.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import java.util.ArrayList;
import java.util.List;
import org.gradle.api.Action;
import org.gradle.api.NamedDomainObjectContainer;
import org.gradle.api.model.ObjectFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;

class OpenApiSchemaExtensionTest {

  @Test
  void validation_when_actionExecuted_then_configuresValidation() {
    final TestExtensionSetup setup = createExtensionWithSchemas("apiV1");

    final Action<ValidationConfig> action =
        validation -> {
          validation.setEnabled(true);
          validation.setValidationApi("jakarta-3.0");
          validation.setNonStrictOneOfValidation(true);
        };

    setup.extension.validation(action);

    final PList<SingleSchemaExtension> schemas = setup.extension.getSchemaExtensions();

    assertTrue(schemas.head().getValidation().getEnabled().get());
    assertEquals("jakarta-3.0", schemas.head().getValidation().getValidationApiString().get());
    assertTrue(schemas.head().getValidation().getNonStrictOneOfValidation().get());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getSchemaExtensions_when_commonValidationEnabled_then_propagatesToSchemas(boolean enabled) {
    final TestExtensionSetup setup = createExtensionWithSchemas("apiV1", "apiV2");
    setup.extension.validation(validation -> validation.setEnabled(enabled));

    final PList<SingleSchemaExtension> schemas = setup.extension.getSchemaExtensions();

    schemas.forEach(schema -> assertEquals(enabled, schema.getValidation().getEnabledOrDefault()));
  }

  @ParameterizedTest
  @ValueSource(strings = {"jakarta-2.0", "jakarta-3.0"})
  void getSchemaExtensions_when_commonValidationApi_then_propagatesToSchemas(String api) {
    final TestExtensionSetup setup = createExtensionWithSchemas("apiV1", "apiV2");
    setup.extension.validation(validation -> validation.setValidationApi(api));

    final PList<SingleSchemaExtension> schemas = setup.extension.getSchemaExtensions();

    schemas.forEach(
        schema -> assertEquals(api, schema.getValidation().getValidationApiString().get()));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void getSchemaExtensions_when_commonNonStrictOneOfValidation_then_propagatesToSchemas(
      boolean nonStrict) {
    final TestExtensionSetup setup = createExtensionWithSchemas("apiV1", "apiV2");
    setup.extension.validation(validation -> validation.setNonStrictOneOfValidation(nonStrict));

    final PList<SingleSchemaExtension> schemas = setup.extension.getSchemaExtensions();

    schemas.forEach(
        schema ->
            assertEquals(nonStrict, schema.getValidation().getNonStrictOneOfValidationOrDefault()));
  }

  @Test
  void getSchemaExtensions_when_schemaOverridesValidation_then_usesSchemaSpecific() {
    final TestExtensionSetup setup = createExtensionWithSchemas("apiV1");

    setup.extension.validation(
        validation -> {
          validation.setEnabled(true);
          validation.setValidationApi("jakarta-3.0");
          validation.setNonStrictOneOfValidation(true);
        });

    // Override in schema before calling getSchemaExtensions
    setup
        .schemas
        .get(0)
        .validation(
            validation -> {
              validation.setEnabled(false);
              validation.setValidationApi("jakarta-2.0");
              validation.setNonStrictOneOfValidation(false);
            });

    final SingleSchemaExtension schema = setup.extension.getSchemaExtensions().head();

    // Schema-specific settings should be preserved after getSchemaExtensions applies common config
    assertFalse(schema.getValidation().getEnabledOrDefault());
    assertEquals("jakarta-2.0", schema.getValidation().getValidationApiString().get());
    assertFalse(schema.getValidation().getNonStrictOneOfValidationOrDefault());
  }

  @Test
  void getSchemaExtensions_when_commonValidationMethods_then_propagatesToSchemas() {
    final TestExtensionSetup setup = createExtensionWithSchemas("apiV1", "apiV2");

    setup.extension.validation(
        validation ->
            validation.validationMethods(
                methods -> {
                  methods.setModifier("protected");
                  methods.setGetterSuffix("Validated");
                  methods.setDeprecatedAnnotation(true);
                }));

    final PList<SingleSchemaExtension> schemas = setup.extension.getSchemaExtensions();

    schemas.forEach(
        schema -> {
          assertEquals(
              "protected", schema.getValidation().getValidationMethods().getModifier().get());
          assertEquals(
              "Validated", schema.getValidation().getValidationMethods().getGetterSuffix().get());
          assertTrue(schema.getValidation().getValidationMethods().getDeprecatedAnnotation().get());
        });
  }

  @Test
  void getSchemaExtensions_when_schemaOverridesValidationMethods_then_mergesCorrectly() {
    final TestExtensionSetup setup = createExtensionWithSchemas("apiV1");

    setup.extension.validation(
        validation ->
            validation.validationMethods(
                methods -> {
                  methods.setModifier("protected");
                  methods.setGetterSuffix("Validated");
                  methods.setDeprecatedAnnotation(true);
                }));

    // Override only modifier in schema before calling getSchemaExtensions
    setup
        .schemas
        .get(0)
        .validation(
            validation -> validation.validationMethods(methods -> methods.setModifier("public")));

    final SingleSchemaExtension schema = setup.extension.getSchemaExtensions().head();

    // Schema modifier should win, but other settings should come from common
    assertEquals("public", schema.getValidation().getValidationMethods().getModifier().get());
    assertEquals(
        "Validated", schema.getValidation().getValidationMethods().getGetterSuffix().get());
    assertTrue(schema.getValidation().getValidationMethods().getDeprecatedAnnotation().get());
  }

  @Test
  void getSchemaExtensions_when_noCommonValidation_then_schemasUseDefaults() {
    final TestExtensionSetup setup = createExtensionWithSchemas("apiV1", "apiV2");

    final PList<SingleSchemaExtension> schemas = setup.extension.getSchemaExtensions();

    schemas.forEach(
        schema -> {
          assertFalse(schema.getValidation().getEnabledOrDefault());
          assertFalse(schema.getValidation().getNonStrictOneOfValidationOrDefault());
        });
  }

  private static class TestExtensionSetup {
    final OpenApiSchemaExtension extension;
    final List<SingleSchemaExtension> schemas;

    TestExtensionSetup(OpenApiSchemaExtension extension, List<SingleSchemaExtension> schemas) {
      this.extension = extension;
      this.schemas = schemas;
    }
  }

  private TestExtensionSetup createExtensionWithSchemas(String... schemaNames) {
    final ObjectFactory objectFactory = createMockObjectFactory();
    final NamedDomainObjectContainer<SingleSchemaExtension> container =
        Mockito.mock(NamedDomainObjectContainer.class);

    final List<SingleSchemaExtension> schemas = new ArrayList<>();
    for (String name : schemaNames) {
      schemas.add(new SingleSchemaExtension(name, objectFactory));
    }

    Mockito.when(objectFactory.domainObjectContainer(SingleSchemaExtension.class))
        .thenReturn(container);
    Mockito.when(container.iterator()).thenReturn(schemas.iterator());

    return new TestExtensionSetup(new OpenApiSchemaExtension(objectFactory), schemas);
  }

  private static ObjectFactory createMockObjectFactory() {
    final ObjectFactory objectFactory = Mockito.mock(ObjectFactory.class);
    Mockito.when(objectFactory.newInstance(Mockito.any()))
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
