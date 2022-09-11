package com.github.muehmar.gradle.openapi.generator.java.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class ToStringGeneratorTest {
  @Test
  void generate_when_allNecessityAndNullabilityVariants_then_correctToStringMethod() {
    final Generator<JavaPojo, PojoSettings> generator = NewToStringGenerator.toStringMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().isEmpty());

    assertEquals(
        "@Override\n"
            + "public String toString() {\n"
            + "  return \"NecessityAndNullabilityDto{\" +\n"
            + "    \"requiredStringVal=\" + requiredStringVal +\n"
            + "    \", requiredNullableStringVal=\" + requiredNullableStringVal +\n"
            + "    \", isRequiredNullableStringValPresent=\" + isRequiredNullableStringValPresent +\n"
            + "    \", optionalStringVal=\" + optionalStringVal +\n"
            + "    \", optionalNullableStringVal=\" + optionalNullableStringVal +\n"
            + "    \", isOptionalNullableStringValNull=\" + isOptionalNullableStringValNull +\n"
            + "    \"}\";\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_arrayPojo_then_correctToStringMethod() {
    final Generator<JavaPojo, PojoSettings> generator = NewToStringGenerator.toStringMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().isEmpty());

    assertEquals(
        "@Override\n"
            + "public String toString() {\n"
            + "  return \"PosologyDto{\" +\n"
            + "    \"value=\" + value +\n"
            + "    \"}\";\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_enumPojo_then_noOutput() {
    final Generator<JavaPojo, PojoSettings> generator = NewToStringGenerator.toStringMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.enumPojo(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().isEmpty());

    assertEquals("", writer.asString());
  }
}
