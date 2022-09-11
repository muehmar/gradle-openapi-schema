package com.github.muehmar.gradle.openapi.dsl;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
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
    extension.withCommonClassMappings(PList.single(commonClassMapping));

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
    extension.withCommonFormatTypeMappings(PList.single(commonFormatTypeMapping));

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
}
