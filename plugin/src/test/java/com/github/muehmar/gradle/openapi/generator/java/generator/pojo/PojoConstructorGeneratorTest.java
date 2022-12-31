package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class PojoConstructorGeneratorTest {
  @Test
  void generator_when_objectPojo_then_correctOutput() {
    final Generator<JavaPojo, PojoSettings> generator = PojoConstructorGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
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

  @Test
  void generator_when_objectPojoAnd_then_correctOutput() {
    final Generator<JavaPojo, PojoSettings> generator = PojoConstructorGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
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

  @Test
  void generator_when_arrayPojo_then_correctOutput() {
    final Generator<JavaPojo, PojoSettings> generator = PojoConstructorGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "public PosologyDto(\n"
            + "    List<Double> value\n"
            + "  ) {\n"
            + "  this.value = value;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_enumPojo_then_noOutput() {
    final Generator<JavaPojo, PojoSettings> generator = PojoConstructorGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.enumPojo(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("", writer.asString());
  }
}
