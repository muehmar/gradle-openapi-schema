package com.github.muehmar.gradle.openapi.generator.java.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.data.Pojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class EqualsGeneratorTest {
  @Test
  void generate_when_allNecessityAndNullabilityVariants_then_correctEqualsMethod() {
    final Generator<Pojo, PojoSettings> generator = EqualsGenerator.equalsMethod();

    final Writer writer =
        generator.generate(
            Pojos.allNecessityAndNullabilityVariants(),
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
}
