package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

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

class HashCodeGeneratorTest {
  @Test
  void generate_when_allNecessityAndNullabilityVariants_then_correctHashCodeMethod() {
    final Generator<JavaPojo, PojoSettings> generator = NewHashCodeGenerator.hashCodeMethod();
    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OBJECTS::equals));

    assertEquals(
        "@Override\n"
            + "public int hashCode() {\n"
            + "  return Objects.hash(\n"
            + "    requiredStringVal,\n"
            + "    requiredNullableStringVal,\n"
            + "    isRequiredNullableStringValPresent,\n"
            + "    optionalStringVal,\n"
            + "    optionalNullableStringVal,\n"
            + "    isOptionalNullableStringValNull\n"
            + "  );\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_arrayPojo_then_correctHashCodeMethod() {
    final Generator<JavaPojo, PojoSettings> generator = NewHashCodeGenerator.hashCodeMethod();
    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OBJECTS::equals));

    assertEquals(
        "@Override\n"
            + "public int hashCode() {\n"
            + "  return Objects.hash(\n"
            + "    value\n"
            + "  );\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_enumPojo_then_noOutput() {
    final Generator<JavaPojo, PojoSettings> generator = NewHashCodeGenerator.hashCodeMethod();
    final Writer writer =
        generator.generate(
            JavaPojos.enumPojo(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(PList.empty(), writer.getRefs());

    assertEquals("", writer.asString());
  }
}
