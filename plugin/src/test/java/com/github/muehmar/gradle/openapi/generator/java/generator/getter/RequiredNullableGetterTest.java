package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static org.junit.jupiter.api.Assertions.*;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Necessity;
import com.github.muehmar.gradle.openapi.generator.data.Nullability;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaValidationRefs;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class RequiredNullableGetterTest {
  @Test
  void generator_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<PojoMember, PojoSettings> generator = RequiredNullableGetter.getter();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE,
            Necessity.REQUIRED,
            Nullability.NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(5, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_IGNORE::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_PROPERTY::equals));
    assertTrue(writer.getRefs().exists(JavaValidationRefs.ASSERT_TRUE::equals));
    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "@JsonIgnore\n"
            + "public Optional<LocalDate> getBirthdate() {\n"
            + "  return Optional.ofNullable(birthdate);\n"
            + "}\n"
            + "\n"
            + "@JsonProperty(\"birthdate\")\n"
            + "private LocalDate getBirthdateNullable() {\n"
            + "  return birthdate;\n"
            + "}\n"
            + "\n"
            + "@AssertTrue(\"birthdate is required but it is not present\")\n"
            + "private boolean isBirthdatePresent() {\n"
            + "  return isBirthdatePresent;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_disabledJackson_then_correctOutputAndRefs() {
    final Generator<PojoMember, PojoSettings> generator = RequiredNullableGetter.getter();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE.withConstraints(
                Constraints.ofPattern(Pattern.ofUnescapedString("DatePattern"))),
            Necessity.REQUIRED,
            Nullability.NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertEquals(4, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JavaValidationRefs.ASSERT_TRUE::equals));
    assertTrue(writer.getRefs().exists(JavaValidationRefs.PATTERN::equals));
    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "public Optional<LocalDate> getBirthdate() {\n"
            + "  return Optional.ofNullable(birthdate);\n"
            + "}\n"
            + "\n"
            + "@Pattern(regexp=\"DatePattern\")\n"
            + "private LocalDate getBirthdateNullable() {\n"
            + "  return birthdate;\n"
            + "}\n"
            + "\n"
            + "@AssertTrue(\"birthdate is required but it is not present\")\n"
            + "private boolean isBirthdatePresent() {\n"
            + "  return isBirthdatePresent;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_disabledJacksonAndValidation_then_correctOutputAndRefs() {
    final Generator<PojoMember, PojoSettings> generator = RequiredNullableGetter.getter();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE,
            Necessity.REQUIRED,
            Nullability.NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings()
                .withJsonSupport(JsonSupport.NONE)
                .withEnableConstraints(false),
            Writer.createDefault());

    assertEquals(2, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "public Optional<LocalDate> getBirthdate() {\n"
            + "  return Optional.ofNullable(birthdate);\n"
            + "}",
        writer.asString());
  }
}
