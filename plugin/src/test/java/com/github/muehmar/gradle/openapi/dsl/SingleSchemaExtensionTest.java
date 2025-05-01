package com.github.muehmar.gradle.openapi.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMappings;
import com.github.muehmar.gradle.openapi.generator.settings.XmlSupport;
import java.util.Collections;
import java.util.Optional;
import org.gradle.api.Action;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

class SingleSchemaExtensionTest {

  @Test
  void withCommonClassMappings_when_oneAdditionalClassMapping_then_concatenated() {
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
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
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
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
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
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
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
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
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");

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
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
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
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
    final Action<WarningsConfig> warningsConfigAction =
        config -> {
          config.setDisableWarnings(false);
          config.setFailOnUnsupportedValidation(false);
        };
    extension.warnings(warningsConfigAction);

    final WarningsConfig commonWarnings = new WarningsConfig();
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
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
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
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
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
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
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
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
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
  void withCommonNonStrictOneOfValidation_when_nothingSet_then_commonSettingsUsed(
      boolean commonNonStrictOneOfValidation) {
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");

    // method call
    extension.withCommonNonStrictOneOfValidation(Optional.of(commonNonStrictOneOfValidation));

    final boolean nonStrictOneOfValidation = extension.getNonStrictOneOfValidation();

    assertEquals(commonNonStrictOneOfValidation, nonStrictOneOfValidation);
  }

  @ParameterizedTest
  @ValueSource(booleans = {true, false})
  void withCommonNonStrictOneOfValidation_when_alreadySet_then_commonSettingIgnored(
      boolean nonStrictOneOfValidation) {
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
    extension.setNonStrictOneOfValidation(nonStrictOneOfValidation);

    // method call
    extension.withCommonNonStrictOneOfValidation(Optional.of(true));

    assertEquals(nonStrictOneOfValidation, extension.getNonStrictOneOfValidation());
  }

  @ParameterizedTest
  @EnumSource(value = XmlSupport.class)
  void withCommonXmlSupport_when_nothingSet_then_commonSettingsUsed(XmlSupport commonXmlSupport) {
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");

    // method call
    extension.withCommonXmlSupport(Optional.of(commonXmlSupport.getValue()));

    final XmlSupport xmlSupport = extension.getXmlSupport();

    assertEquals(commonXmlSupport, xmlSupport);
  }

  @ParameterizedTest
  @EnumSource(value = XmlSupport.class)
  void withCommonXmlSupport_when_alreadySet_then_commonSettingIgnored(XmlSupport xmlSupport) {
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
    extension.setXmlSupport(xmlSupport.getValue());

    // method call
    extension.withCommonXmlSupport(Optional.of("none"));

    assertEquals(xmlSupport, extension.getXmlSupport());
  }

  @ParameterizedTest
  @EnumSource(value = JsonSupport.class)
  void withCommonJsonSupport_when_nothingSet_then_commonSettingsUsed(
      JsonSupport commonJsonSupport) {
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");

    // method call
    extension.withCommonJsonSupport(Optional.of(commonJsonSupport.getValue()));

    final JsonSupport jsonSupport = extension.getJsonSupport();

    assertEquals(commonJsonSupport, jsonSupport);
  }

  @ParameterizedTest
  @EnumSource(value = JsonSupport.class)
  void withCommonJsonSupport_when_alreadySet_then_commonSettingIgnored(JsonSupport jsonSupport) {
    final SingleSchemaExtension extension = new SingleSchemaExtension("apiV1");
    extension.setJsonSupport(jsonSupport.getValue());

    // method call
    extension.withCommonJsonSupport(Optional.of("none"));

    assertEquals(jsonSupport, extension.getJsonSupport());
  }
}
