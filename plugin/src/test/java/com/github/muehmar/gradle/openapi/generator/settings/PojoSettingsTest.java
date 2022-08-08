package com.github.muehmar.gradle.openapi.generator.settings;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.generator.data.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import org.junit.jupiter.api.Test;

class PojoSettingsTest {
  private final PojoSettings SETTINGS =
      TestPojoSettings.defaultSettings()
          .withGetterSuffixes(
              GetterSuffixesBuilder.create()
                  .requiredSuffix("Req")
                  .requiredNullableSuffix("ReqNull")
                  .optionalSuffix("Opt")
                  .optionalNullableSuffix("OptNull")
                  .build());

  @Test
  void suffixForField_when_requiredField_then_requiredSuffixReturned() {
    final PojoMember required = PojoMembers.requiredBirthdate();
    final String suffix = SETTINGS.suffixForField(required);

    assertEquals(SETTINGS.getGetterSuffixes().getRequiredSuffix(), suffix);
  }

  @Test
  void suffixForField_when_optionalField_then_optionalSuffixReturned() {
    final PojoMember optional = PojoMembers.optionalString();
    final String suffix = SETTINGS.suffixForField(optional);

    assertEquals(SETTINGS.getGetterSuffixes().getOptionalSuffix(), suffix);
  }

  @Test
  void suffixForField_when_requiredNullableField_then_requiredNullableSuffixReturned() {
    final PojoMember requiredNullable =
        PojoMembers.requiredBirthdate().withNullability(Nullability.NULLABLE);
    final String suffix = SETTINGS.suffixForField(requiredNullable);

    assertEquals(SETTINGS.getGetterSuffixes().getRequiredNullableSuffix(), suffix);
  }

  @Test
  void suffixForField_when_optionalNullableField_then_optionalNullableSuffixReturned() {
    final PojoMember optionalNullable = PojoMembers.optionalNullableString();
    final String suffix = SETTINGS.suffixForField(optionalNullable);

    assertEquals(SETTINGS.getGetterSuffixes().getOptionalNullableSuffix(), suffix);
  }
}
