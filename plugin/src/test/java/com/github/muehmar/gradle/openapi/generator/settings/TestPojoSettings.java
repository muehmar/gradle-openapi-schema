package com.github.muehmar.gradle.openapi.generator.settings;

import java.util.Collections;

public class TestPojoSettings {
  private TestPojoSettings() {}

  public static PojoSettings defaultSettings() {
    return PojoSettingsBuilder.create()
        .jsonSupport(JsonSupport.JACKSON)
        .packageName("com.github.muehmar")
        .suffix("Dto")
        .enableSafeBuilder(true)
        .enableConstraints(true)
        .classTypeMappings(Collections.emptyList())
        .formatTypeMappings(Collections.emptyList())
        .enumDescriptionSettings(EnumDescriptionSettings.disabled())
        .getterSuffixes(defaultGetterSuffixes())
        .validationGetter(defaultValidationGetter())
        .andAllOptionals()
        .build();
  }

  public static GetterSuffixes defaultGetterSuffixes() {
    return GetterSuffixesBuilder.create()
        .requiredSuffix("")
        .requiredNullableSuffix("")
        .optionalSuffix("")
        .optionalNullableSuffix("")
        .build();
  }

  public static ValidationGetter defaultValidationGetter() {
    return ValidationGetterBuilder.create()
        .modifier(JavaModifier.PRIVATE)
        .suffix("ForReflection")
        .deprecatedAnnotation(false)
        .andAllOptionals()
        .build();
  }
}
