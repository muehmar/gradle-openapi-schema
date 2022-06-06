package com.github.muehmar.gradle.openapi.generator.settings;

import java.util.Collections;

public class TestPojoSettings {
  private TestPojoSettings() {}

  public static PojoSettings defaultSettings() {
    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("")
            .requiredNullableSuffix("")
            .optionalSuffix("")
            .optionalNullableSuffix("")
            .build();
    return PojoSettingsBuilder.create()
        .jsonSupport(JsonSupport.JACKSON)
        .packageName("com.github.muehmar")
        .suffix("Dto")
        .enableSafeBuilder(true)
        .enableConstraints(true)
        .classTypeMappings(Collections.emptyList())
        .formatTypeMappings(Collections.emptyList())
        .enumDescriptionSettings(EnumDescriptionSettings.disabled())
        .getterSuffixes(getterSuffixes)
        .andAllOptionals()
        .build();
  }
}
