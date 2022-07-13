package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.generator.data.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class CommonGetterTest {

  @Test
  void nullableGetterMethodForValidation_defaultSettings_then_correctOutput() {
    final Generator<PojoMember, PojoSettings> generator =
        CommonGetter.nullableGetterMethodForValidation();
    final Writer writer =
        generator.generate(
            PojoMembers.optionalString(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals(
        "private String getOptionalStringValForReflection() {\n"
            + "  return optionalStringVal;\n"
            + "}",
        writer.asString());
  }

  @Test
  void nullableGetterMethodForValidation_customModifierAndSuffix_then_correctOutput() {
    final Generator<PojoMember, PojoSettings> generator =
        CommonGetter.nullableGetterMethodForValidation();

    final PojoSettings settings =
        TestPojoSettings.defaultSettings()
            .withValidationGetter(
                TestPojoSettings.defaultValidationGetter()
                    .withModifier(JavaModifier.PUBLIC)
                    .withSuffix("CustomSuffix"));
    final Writer writer =
        generator.generate(PojoMembers.optionalString(), settings, Writer.createDefault());

    assertEquals(
        "public String getOptionalStringValCustomSuffix() {\n"
            + "  return optionalStringVal;\n"
            + "}",
        writer.asString());
  }
}
