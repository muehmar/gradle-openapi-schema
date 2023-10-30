package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class CommonGetterTest {

  @Test
  void rawGetterMethod_when_defaultSettings_then_correctOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = CommonGetter.rawGetterMethod();
    final Writer writer =
        generator.generate(JavaPojoMembers.optionalString(), defaultTestSettings(), javaWriter());

    assertEquals(
        "private String getOptionalStringValRaw() {\n" + "  return optionalStringVal;\n" + "}",
        writer.asString());
  }

  @Test
  void rawGetterMethod_when_customModifierAndSuffix_then_correctOutput() {
    final Generator<JavaPojoMember, PojoSettings> generator = CommonGetter.rawGetterMethod();

    final PojoSettings settings =
        defaultTestSettings()
            .withValidationMethods(
                TestPojoSettings.defaultValidationMethods()
                    .withModifier(JavaModifier.PUBLIC)
                    .withGetterSuffix("CustomSuffix"));
    final Writer writer =
        generator.generate(JavaPojoMembers.optionalString(), settings, javaWriter());

    assertEquals(
        "public String getOptionalStringValCustomSuffix() {\n"
            + "  return optionalStringVal;\n"
            + "}",
        writer.asString());
  }
}
