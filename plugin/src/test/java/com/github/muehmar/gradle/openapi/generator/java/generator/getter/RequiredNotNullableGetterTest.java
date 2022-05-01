package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Necessity;
import com.github.muehmar.gradle.openapi.generator.data.Nullability;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaValidationRefs;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class RequiredNotNullableGetterTest {
  @Test
  void generator_when_requiredAndNotNullableField_then_correctOutputAndRefs() {
    final Generator<PojoMember, PojoSettings> generator = RequiredNotNullableGetter.getter();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE,
            Necessity.REQUIRED,
            Nullability.NOT_NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaValidationRefs.NOT_NULL::equals));
    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "@NotNull\n"
            + "public LocalDate getBirthdate() {\n"
            + "  return birthdate;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_validationDisabled_then_correctOutputAndRefs() {
    final Generator<PojoMember, PojoSettings> generator = RequiredNotNullableGetter.getter();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE,
            Necessity.REQUIRED,
            Nullability.NOT_NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings().withEnableConstraints(false),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "public LocalDate getBirthdate() {\n"
            + "  return birthdate;\n"
            + "}",
        writer.asString());
  }
}
