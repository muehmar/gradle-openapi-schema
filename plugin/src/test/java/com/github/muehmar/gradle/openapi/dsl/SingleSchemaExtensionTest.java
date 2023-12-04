package com.github.muehmar.gradle.openapi.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMappings;
import java.util.Collections;
import java.util.Optional;
import org.gradle.api.Action;
import org.junit.jupiter.api.Test;

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
}
