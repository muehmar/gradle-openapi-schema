package com.github.muehmar.gradle.openapi.generator.java.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.java.generator.data.Pojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class PojoConstructorGeneratorTest {
  @Test
  void generator_when_used_then_correctOutput() {
    final Generator<Pojo, PojoSettings> generator = PojoConstructorGenerator.generator();

    final Writer writer =
        generator.generate(
            Pojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals(
        "public NecessityAndNullabilityDto(\n"
            + "    String requiredStringVal,\n"
            + "    String requiredNullableStringVal,\n"
            + "    boolean isRequiredNullableStringValPresent,\n"
            + "    String optionalStringVal,\n"
            + "    String optionalNullableStringVal,\n"
            + "    boolean isOptionalNullableStringValNull\n"
            + "  ) {\n"
            + "  this.requiredStringVal = requiredStringVal;\n"
            + "  this.requiredNullableStringVal = requiredNullableStringVal;\n"
            + "  this.isRequiredNullableStringValPresent = isRequiredNullableStringValPresent;\n"
            + "  this.optionalStringVal = optionalStringVal;\n"
            + "  this.optionalNullableStringVal = optionalNullableStringVal;\n"
            + "  this.isOptionalNullableStringValNull = isOptionalNullableStringValNull;\n"
            + "}",
        writer.asString());
  }
}
