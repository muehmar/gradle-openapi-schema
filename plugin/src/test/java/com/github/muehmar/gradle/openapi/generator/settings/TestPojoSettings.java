package com.github.muehmar.gradle.openapi.generator.settings;

import java.util.Collections;

public class TestPojoSettings {
  private TestPojoSettings() {}

  public static PojoSettings defaultSettings() {
    return PojoSettings.newBuilder()
        .setJsonSupport(JsonSupport.JACKSON)
        .setPackageName("com.github.muehmar")
        .setSuffix("Dto")
        .setEnableSafeBuilder(true)
        .setEnableConstraints(true)
        .setClassTypeMappings(Collections.emptyList())
        .setFormatTypeMappings(Collections.emptyList())
        .setEnumDescriptionSettings(EnumDescriptionSettings.disabled())
        .andAllOptionals()
        .build();
  }
}
