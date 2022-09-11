package com.github.muehmar.gradle.openapi.generator.java.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class EqualsGeneratorTest {
  @Test
  void generate_when_allNecessityAndNullabilityVariants_then_correctEqualsMethod() {
    final Generator<JavaPojo, PojoSettings> generator = NewEqualsGenerator.equalsMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OBJECTS::equals));

    assertEquals(
        "@Override\n"
            + "public boolean equals(Object obj) {\n"
            + "  if (this == obj) return true;\n"
            + "  if (obj == null || this.getClass() != obj.getClass()) return false;\n"
            + "  final NecessityAndNullabilityDto other = (NecessityAndNullabilityDto) obj;\n"
            + "  return Objects.equals(requiredStringVal, other.requiredStringVal)\n"
            + "      && Objects.equals(requiredNullableStringVal, other.requiredNullableStringVal)\n"
            + "      && Objects.equals(isRequiredNullableStringValPresent, other.isRequiredNullableStringValPresent)\n"
            + "      && Objects.equals(optionalStringVal, other.optionalStringVal)\n"
            + "      && Objects.equals(optionalNullableStringVal, other.optionalNullableStringVal)\n"
            + "      && Objects.equals(isOptionalNullableStringValNull, other.isOptionalNullableStringValNull);\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_arrayPojo_then_correctEqualsMethod() {
    final Generator<JavaPojo, PojoSettings> generator = NewEqualsGenerator.equalsMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OBJECTS::equals));

    assertEquals(
        "@Override\n"
            + "public boolean equals(Object obj) {\n"
            + "  if (this == obj) return true;\n"
            + "  if (obj == null || this.getClass() != obj.getClass()) return false;\n"
            + "  final PsologyDto other = (PsologyDto) obj;\n"
            + "  return Objects.equals(value, other.value);\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_enumPojo_then_nothingGenerated() {
    final Generator<JavaPojo, PojoSettings> generator = NewEqualsGenerator.equalsMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.enumPojo(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(PList.empty(), writer.getRefs());

    assertEquals("", writer.asString());
  }
}
