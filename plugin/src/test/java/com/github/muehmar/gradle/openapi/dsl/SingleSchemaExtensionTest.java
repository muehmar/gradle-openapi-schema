package com.github.muehmar.gradle.openapi.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMappings;
import com.github.muehmar.gradle.openapi.generator.settings.XmlSupport;
import java.util.Collections;
import java.util.Optional;
import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class SingleSchemaExtensionTest {

  @Test
  void withCommonClassMappings_when_oneAdditionalClassMapping_then_concatenated() {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<ClassMapping> classMappingAction =
        mapping -> {
          mapping.setFromClass("List");
          mapping.setToClass("java.util.ArrayList");
        };
    extension.classMapping(classMappingAction);

    final ClassMapping commonClassMapping = new ClassMapping();
    commonClassMapping.setFromClass("String");
    commonClassMapping.setToClass("package.SuperString");

    // method call
    extension.withCommonClassMappings(Collections.singletonList(commonClassMapping));

    final PList<ClassMapping> classMappings = extension.getClassMappings();

    final PList<ClassMapping> expected = PList.of(new ClassMapping(), commonClassMapping);
    classMappingAction.execute(expected.apply(0));

    assertEquals(expected, classMappings);
  }

  @Test
  void withCommonFormatTypeMappings_when_oneAdditionalFormatTypeMapping_then_concatenated() {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<FormatTypeMapping> formatTypeMappingAction =
        mapping -> {
          mapping.setFormatType("Username");
          mapping.setClassType("package.CustomUsername");
        };
    extension.formatTypeMapping(formatTypeMappingAction);

    final FormatTypeMapping commonFormatTypeMapping = new FormatTypeMapping();
    commonFormatTypeMapping.setFormatType("Password");
    commonFormatTypeMapping.setClassType("package.CustomPassword");

    // method call
    extension.withCommonFormatTypeMappings(Collections.singletonList(commonFormatTypeMapping));

    final PList<FormatTypeMapping> formatTypeMappings = extension.getFormatTypeMappings();

    final PList<FormatTypeMapping> expected =
        PList.of(new FormatTypeMapping(), commonFormatTypeMapping);
    formatTypeMappingAction.execute(expected.apply(0));

    assertEquals(expected, formatTypeMappings);
  }

  @Test
  void withCommonEnumDescription_when_specificExtensionPresent_then_commonIgnored() {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<EnumDescriptionExtension> enumDescriptionExtensionAction =
        enumDescription -> {
          enumDescription.setEnabled(true);
          enumDescription.setFailOnIncompleteDescriptions(true);
          enumDescription.setPrefixMatcher("__ENUM__->");
        };
    extension.enumDescriptionExtraction(enumDescriptionExtensionAction);

    final EnumDescriptionExtension commonEnumDescriptionExtension = new EnumDescriptionExtension();
    commonEnumDescriptionExtension.setFailOnIncompleteDescriptions(false);
    commonEnumDescriptionExtension.setPrefixMatcher("* __ENUM__:");

    // method call
    extension.withCommonEnumDescription(Optional.of(commonEnumDescriptionExtension));

    final Optional<EnumDescriptionExtension> enumDescriptionExtension =
        extension.getEnumDescriptionExtension();

    final EnumDescriptionExtension expected = new EnumDescriptionExtension();
    enumDescriptionExtensionAction.execute(expected);
    assertEquals(Optional.of(expected), enumDescriptionExtension);
  }

  @Test
  void withCommonEnumDescription_when_noCommonExtensionPresent_then_specificUsed() {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<EnumDescriptionExtension> enumDescriptionExtensionAction =
        enumDescription -> {
          enumDescription.setEnabled(true);
          enumDescription.setFailOnIncompleteDescriptions(true);
          enumDescription.setPrefixMatcher("__ENUM__->");
        };
    extension.enumDescriptionExtraction(enumDescriptionExtensionAction);

    // method call
    extension.withCommonEnumDescription(Optional.empty());

    final Optional<EnumDescriptionExtension> enumDescriptionExtension =
        extension.getEnumDescriptionExtension();

    final EnumDescriptionExtension expected = new EnumDescriptionExtension();
    enumDescriptionExtensionAction.execute(expected);
    assertEquals(Optional.of(expected), enumDescriptionExtension);
  }

  @Test
  void withCommonEnumDescription_when_specificExtensionNotPresent_then_commonIgnored() {
    final SingleSchemaExtension extension = createExtension("apiV1");

    final EnumDescriptionExtension commonEnumDescriptionExtension = new EnumDescriptionExtension();
    commonEnumDescriptionExtension.setFailOnIncompleteDescriptions(false);
    commonEnumDescriptionExtension.setPrefixMatcher("* __ENUM__:");

    // method call
    extension.withCommonEnumDescription(Optional.of(commonEnumDescriptionExtension));

    final Optional<EnumDescriptionExtension> enumDescriptionExtension =
        extension.getEnumDescriptionExtension();

    assertEquals(Optional.of(commonEnumDescriptionExtension), enumDescriptionExtension);
  }

  @Test
  void withCommonConstantSchemaNameMappings_when_oneAdditionalMappingPresent_then_concatenated() {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<ConstantSchemaNameMapping> constantSchemaNameMappingAction =
        mapping -> {
          mapping.setConstant("User");
          mapping.setReplacement("Person");
        };
    extension.constantSchemaNameMapping(constantSchemaNameMappingAction);

    final ConstantSchemaNameMapping commonConstantSchemaNameMapping =
        new ConstantSchemaNameMapping();
    commonConstantSchemaNameMapping.setConstant("Password");
    commonConstantSchemaNameMapping.setReplacement("*****");

    // method call
    extension.withCommonConstantSchemaNameMappings(
        Collections.singletonList(commonConstantSchemaNameMapping));

    final PojoNameMappings pojoNameMappings = extension.getPojoNameMappings();

    final ConstantSchemaNameMapping existingMapping = new ConstantSchemaNameMapping();
    constantSchemaNameMappingAction.execute(existingMapping);
    assertEquals(
        PList.of(existingMapping, commonConstantSchemaNameMapping)
            .map(ConstantSchemaNameMapping::toConstantNameMapping)
            .toArrayList(),
        pojoNameMappings.getConstantNameMappings());
  }

  @Test
  void withCommonWarnings_when_oneAdditionalMappingPresent_then_concatenated() {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<WarningsConfig> warningsConfigAction =
        config -> {
          config.setDisableWarnings(false);
          config.setFailOnUnsupportedValidation(false);
        };
    extension.warnings(warningsConfigAction);

    final WarningsConfig commonWarnings = WarningsConfig.allUndefined();
    commonWarnings.setDisableWarnings(true);
    commonWarnings.setFailOnWarnings(true);
    commonWarnings.setFailOnUnsupportedValidation(true);

    // method call
    extension.withCommonWarnings(commonWarnings);

    final WarningsConfig resultingWarnings = extension.getWarnings();

    assertFalse(resultingWarnings.getDisableWarnings());
    assertTrue(resultingWarnings.getFailOnWarnings());
    assertFalse(resultingWarnings.getFailOnUnsupportedValidation());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void withCommonStagedBuilder_when_nothingSet_then_commonSettingsUsed(boolean enabled) {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<StagedBuilder> stagedBuilderAction = config -> {};
    extension.stagedBuilder(stagedBuilderAction);

    final StagedBuilder commonStagedBuilder = StagedBuilder.allUndefined();
    commonStagedBuilder.setEnabled(enabled);

    // method call
    extension.withCommonStagedBuilder(commonStagedBuilder);

    final StagedBuilder stagedBuilder = extension.getStagedBuilder();

    assertEquals(commonStagedBuilder.getEnabled(), stagedBuilder.getEnabled());
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void withCommonStagedBuilder_when_alreadySet_then_commonSettingsDiscarded(boolean enabled) {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<StagedBuilder> stagedBuilderAction =
        config -> {
          config.setEnabled(true);
        };
    extension.stagedBuilder(stagedBuilderAction);

    final StagedBuilder commonStagedBuilder = StagedBuilder.allUndefined();
    commonStagedBuilder.setEnabled(enabled);

    // method call
    extension.withCommonStagedBuilder(commonStagedBuilder);

    final StagedBuilder stagedBuilder = extension.getStagedBuilder();

    assertEquals(Optional.of(true), stagedBuilder.getEnabled());
  }

  @Test
  void classMapping_when_invalidConversionConfig_then_throwsOpenApiGeneratorException() {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<ClassMapping> classMappingAction =
        mapping -> {
          mapping.setFromClass("List");
          mapping.setToClass("java.util.ArrayList");
          mapping.getConversion().setToCustomType("value");
        };

    assertThrows(OpenApiGeneratorException.class, () -> extension.classMapping(classMappingAction));
  }

  @Test
  void formatTypeMapping_when_invalidConversionConfig_then_throwsOpenApiGeneratorException() {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<FormatTypeMapping> formatTypeMappingAction =
        mapping -> {
          mapping.setClassType("List");
          mapping.setFormatType("list");
          mapping.getConversion().setFromCustomType("value");
        };

    assertThrows(
        OpenApiGeneratorException.class,
        () -> extension.formatTypeMapping(formatTypeMappingAction));
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void withCommonValidation_when_nothingSet_then_commonSettingsUsed(
      boolean commonNonStrictOneOfValidation) {
    final SingleSchemaExtension extension = createExtension("apiV1");

    final ValidationConfig commonValidation = createValidationConfig();
    commonValidation.setNonStrictOneOfValidation(commonNonStrictOneOfValidation);

    // method call
    extension.withCommonValidation(commonValidation);

    final boolean nonStrictOneOfValidation =
        extension.getValidation().getNonStrictOneOfValidationOrDefault();

    assertEquals(commonNonStrictOneOfValidation, nonStrictOneOfValidation);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void withCommonValidation_when_alreadySet_then_useSchemaSpecificSetting(
      boolean nonStrictOneOfValidation) {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<ValidationConfig> validationAction =
        validation -> validation.setNonStrictOneOfValidation(nonStrictOneOfValidation);
    extension.validation(validationAction);

    final ValidationConfig commonValidation = createValidationConfig();
    commonValidation.setNonStrictOneOfValidation(!nonStrictOneOfValidation);

    // method call
    extension.withCommonValidation(commonValidation);

    assertEquals(
        nonStrictOneOfValidation, extension.getValidation().getNonStrictOneOfValidationOrDefault());
  }

  @Test
  void withCommonValidation_when_enabledSetInCommon_then_usesCommonSetting() {
    final SingleSchemaExtension extension = createExtension("apiV1");

    final ValidationConfig commonValidation = createValidationConfig();
    commonValidation.setEnabled(true);

    // method call
    extension.withCommonValidation(commonValidation);

    assertTrue(extension.getValidation().getEnabledOrDefault());
  }

  @Test
  void withCommonValidation_when_validationApiSetInCommon_then_usesCommonSetting() {
    final SingleSchemaExtension extension = createExtension("apiV1");

    final ValidationConfig commonValidation = createValidationConfig();
    commonValidation.setValidationApi("jakarta-3.0");

    // method call
    extension.withCommonValidation(commonValidation);

    assertEquals("jakarta-3.0", extension.getValidation().getValidationApiString().get());
  }

  @Test
  void withCommonValidation_when_schemaSpecificOverridesAll_then_usesSchemaSpecific() {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<ValidationConfig> validationAction =
        validation -> {
          validation.setEnabled(false);
          validation.setValidationApi("jakarta-2.0");
          validation.setNonStrictOneOfValidation(false);
        };
    extension.validation(validationAction);

    final ValidationConfig commonValidation = createValidationConfig();
    commonValidation.setEnabled(true);
    commonValidation.setValidationApi("jakarta-3.0");
    commonValidation.setNonStrictOneOfValidation(true);

    // method call
    extension.withCommonValidation(commonValidation);

    assertFalse(extension.getValidation().getEnabledOrDefault());
    assertEquals("jakarta-2.0", extension.getValidation().getValidationApiString().get());
    assertFalse(extension.getValidation().getNonStrictOneOfValidationOrDefault());
  }

  @Test
  void withCommonValidation_when_validationMethodsInBoth_then_mergesCorrectly() {
    final SingleSchemaExtension extension = createExtension("apiV1");
    final Action<ValidationConfig> validationAction =
        validation ->
            validation.validationMethods(
                methods -> {
                  methods.setModifier("public");
                });
    extension.validation(validationAction);

    final ValidationConfig commonValidation = createValidationConfig();
    commonValidation.validationMethods(
        methods -> {
          methods.setModifier("private");
          methods.setGetterSuffix("Internal");
          methods.setDeprecatedAnnotation(true);
        });

    // method call
    extension.withCommonValidation(commonValidation);

    assertEquals("public", extension.getValidation().getValidationMethods().getModifier().get());
    assertEquals(
        "Internal", extension.getValidation().getValidationMethods().getGetterSuffix().get());
    assertTrue(extension.getValidation().getValidationMethods().getDeprecatedAnnotation().get());
  }

  @ParameterizedTest
  @EnumSource(value = XmlSupport.class)
  void withCommonXmlSupport_when_nothingSet_then_commonSettingsUsed(XmlSupport commonXmlSupport) {
    final SingleSchemaExtension extension = createExtension("apiV1");

    // method call
    extension.withCommonXmlSupport(Optional.of(commonXmlSupport.getValue()));

    final XmlSupport xmlSupport = extension.getXmlSupport();

    assertEquals(commonXmlSupport, xmlSupport);
  }

  @ParameterizedTest
  @EnumSource(value = XmlSupport.class)
  void withCommonXmlSupport_when_alreadySet_then_commonSettingIgnored(XmlSupport xmlSupport) {
    final SingleSchemaExtension extension = createExtension("apiV1");
    extension.setXmlSupport(xmlSupport.getValue());

    // method call
    extension.withCommonXmlSupport(Optional.of("none"));

    assertEquals(xmlSupport, extension.getXmlSupport());
  }

  @ParameterizedTest
  @EnumSource(value = JsonSupport.class)
  void withCommonJsonSupport_when_nothingSet_then_commonSettingsUsed(
      JsonSupport commonJsonSupport) {
    final SingleSchemaExtension extension = createExtension("apiV1");

    // method call
    extension.withCommonJsonSupport(Optional.of(commonJsonSupport.getValue()));

    final JsonSupport jsonSupport = extension.getJsonSupport();

    assertEquals(commonJsonSupport, jsonSupport);
  }

  @ParameterizedTest
  @EnumSource(value = JsonSupport.class)
  void withCommonJsonSupport_when_alreadySet_then_commonSettingIgnored(JsonSupport jsonSupport) {
    final SingleSchemaExtension extension = createExtension("apiV1");
    extension.setJsonSupport(jsonSupport.getValue());

    // method call
    extension.withCommonJsonSupport(Optional.of("none"));

    assertEquals(jsonSupport, extension.getJsonSupport());
  }

  private static SingleSchemaExtension createExtension(String name) {
    final ObjectFactory objectFactory = createMockObjectFactory();
    return new SingleSchemaExtension(name, objectFactory);
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
