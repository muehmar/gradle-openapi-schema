package com.github.muehmar.gradle.openapi.generator.settings;

import static com.github.muehmar.gradle.openapi.generator.settings.PojoSettingsBuilder.fullPojoSettingsBuilder;
import static com.github.muehmar.gradle.openapi.generator.settings.StagedBuilderSettingsBuilder.fullStagedBuilderSettingsBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.task.TaskIdentifier;
import java.util.Collections;
import java.util.UUID;

public class TestPojoSettings {
  private TestPojoSettings() {}

  public static PojoSettings defaultTestSettings() {
    return fullPojoSettingsBuilder()
        .jsonSupport(JsonSupport.JACKSON)
        .xmlSupport(XmlSupport.NONE)
        .packageName(PackageName.fromString("com.github.muehmar"))
        .suffix("Dto")
        .stagedBuilder(defaultStagedBuilderSettings())
        .builderMethodPrefix("set")
        .enableValidation(true)
        .nonStrictOneOfValidation(false)
        .disableUniqueItemsValidation(false)
        .allowNullableForEnums(false)
        .validationApi(ValidationApi.JAKARTA_2_0)
        .classTypeMappings(Collections.emptyList())
        .formatTypeMappings(Collections.emptyList())
        .dtoMappings(Collections.emptyList())
        .enumDescriptionSettings(EnumDescriptionSettings.disabled())
        .getterSuffixes(defaultGetterSuffixes())
        .validationMethods(defaultValidationMethods())
        .excludeSchemas(Collections.emptyList())
        .pojoNameMappings(PojoNameMappings.noMappings())
        .taskIdentifier(TaskIdentifier.fromString(UUID.randomUUID().toString()))
        .build();
  }

  public static StagedBuilderSettings defaultStagedBuilderSettings() {
    return fullStagedBuilderSettingsBuilder().enabled(true).build();
  }

  public static PList<PojoSettings> validationVariants() {
    return PList.of(
        defaultTestSettings().withEnableValidation(false),
        defaultTestSettings()
            .withEnableValidation(true)
            .withValidationApi(ValidationApi.JAKARTA_2_0),
        defaultTestSettings()
            .withEnableValidation(true)
            .withValidationApi(ValidationApi.JAKARTA_3_0));
  }

  public static GetterSuffixes defaultGetterSuffixes() {
    return GetterSuffixesBuilder.create()
        .requiredSuffix("")
        .requiredNullableSuffix("")
        .optionalSuffix("")
        .optionalNullableSuffix("")
        .build();
  }

  public static ValidationMethods defaultValidationMethods() {
    return ValidationMethodsBuilder.create()
        .modifier(JavaModifier.PRIVATE)
        .getterSuffix("Raw")
        .deprecatedAnnotation(false)
        .andAllOptionals()
        .build();
  }
}
