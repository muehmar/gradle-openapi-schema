package com.github.muehmar.gradle.openapi.generator.java.generator;

import static org.junit.jupiter.api.Assertions.*;

import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.data.Pojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class HashCodeGeneratorTest {

  @Test
  void generate_when_allNecessityAndNullabilityVariants_then_correctHashCodeMethod() {
    final Generator<Pojo, PojoSettings> generator = HashCodeGenerator.hashCodeMethod();
    final Writer writer =
        generator.generate(
            Pojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OBJECTS::equals));

    assertEquals(
        "public int hashCode() {\n"
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
}
