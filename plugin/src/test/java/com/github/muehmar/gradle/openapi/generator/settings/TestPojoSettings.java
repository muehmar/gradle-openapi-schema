package com.github.muehmar.gradle.openapi.generator.settings;

import ch.bluecare.commons.data.PList;
import java.util.Collections;

public class TestPojoSettings {
  private TestPojoSettings() {}

  public static PojoSettings defaultTestSettings() {
    return PojoSettingsBuilder.create()
        .jsonSupport(JsonSupport.JACKSON)
        .packageName("com.github.muehmar")
        .suffix("Dto")
        .enableSafeBuilder(true)
        .builderMethodPrefix("set")
        .enableValidation(true)
        .validationApi(ValidationApi.JAKARTA_2_0)
        .classTypeMappings(Collections.emptyList())
        .formatTypeMappings(Collections.emptyList())
        .enumDescriptionSettings(EnumDescriptionSettings.disabled())
        .getterSuffixes(defaultGetterSuffixes())
        .validationMethods(defaultValidationMethods())
        .excludeSchemas(Collections.emptyList())
        .andAllOptionals()
        .build();
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
