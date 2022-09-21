package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaValidationRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixes;
import com.github.muehmar.gradle.openapi.generator.settings.GetterSuffixesBuilder;
import com.github.muehmar.gradle.openapi.generator.settings.JavaModifier;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.RawGetter;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class OptionalNotNullableGetterTest {

  @Test
  void generator_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = OptionalNotNullableGetter.getter();
    final JavaPojoMember pojoMember = JavaPojoMembers.birthdate(OPTIONAL, NOT_NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(5, writer.getRefs().toHashSet().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_IGNORE::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_PROPERTY::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_INCLUDE::equals));
    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "@JsonIgnore\n"
            + "public Optional<LocalDate> getBirthdate() {\n"
            + "  return Optional.ofNullable(birthdate);\n"
            + "}\n"
            + "\n"
            + "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "@JsonIgnore\n"
            + "public LocalDate getBirthdateOr(LocalDate defaultValue) {\n"
            + "  return birthdate == null ? defaultValue : birthdate;\n"
            + "}\n"
            + "\n"
            + "@JsonProperty(\"birthdate\")\n"
            + "@JsonInclude(JsonInclude.Include.NON_NULL)\n"
            + "private LocalDate getBirthdateRaw() {\n"
            + "  return birthdate;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_disabledJacksonAndEnabledValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = OptionalNotNullableGetter.getter();
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(
            Constraints.ofPattern(Pattern.ofUnescapedString("DatePattern")),
            OPTIONAL,
            NOT_NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertEquals(3, writer.getRefs().toHashSet().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(JavaValidationRefs.PATTERN::equals));
    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "public Optional<LocalDate> getBirthdate() {\n"
            + "  return Optional.ofNullable(birthdate);\n"
            + "}\n"
            + "\n"
            + "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "public LocalDate getBirthdateOr(LocalDate defaultValue) {\n"
            + "  return birthdate == null ? defaultValue : birthdate;\n"
            + "}\n"
            + "\n"
            + "@Pattern(regexp=\"DatePattern\")\n"
            + "private LocalDate getBirthdateRaw() {\n"
            + "  return birthdate;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_disabledJacksonAndValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = OptionalNotNullableGetter.getter();
    final JavaPojoMember pojoMember = JavaPojoMembers.birthdate(OPTIONAL, NOT_NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings()
                .withJsonSupport(JsonSupport.NONE)
                .withEnableConstraints(false),
            Writer.createDefault());

    assertEquals(2, writer.getRefs().toHashSet().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "public Optional<LocalDate> getBirthdate() {\n"
            + "  return Optional.ofNullable(birthdate);\n"
            + "}\n"
            + "\n"
            + "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "public LocalDate getBirthdateOr(LocalDate defaultValue) {\n"
            + "  return birthdate == null ? defaultValue : birthdate;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_suffixForOptionalNotNullable_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = OptionalNotNullableGetter.getter();
    final JavaPojoMember pojoMember = JavaPojoMembers.birthdate(OPTIONAL, NOT_NULLABLE);

    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("")
            .requiredNullableSuffix("")
            .optionalSuffix("Opt")
            .optionalNullableSuffix("")
            .build();

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings()
                .withJsonSupport(JsonSupport.NONE)
                .withEnableConstraints(false)
                .withGetterSuffixes(getterSuffixes),
            Writer.createDefault());

    assertEquals(2, writer.getRefs().toHashSet().size());
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "public Optional<LocalDate> getBirthdateOpt() {\n"
            + "  return Optional.ofNullable(birthdate);\n"
            + "}\n"
            + "\n"
            + "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "public LocalDate getBirthdateOr(LocalDate defaultValue) {\n"
            + "  return birthdate == null ? defaultValue : birthdate;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_deprecatedAnnotation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = OptionalNotNullableGetter.getter();
    final JavaPojoMember pojoMember = JavaPojoMembers.birthdate(OPTIONAL, NOT_NULLABLE);

    final RawGetter rawGetter =
        TestPojoSettings.defaultRawGetter()
            .withDeprecatedAnnotation(true)
            .withModifier(JavaModifier.PUBLIC);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings()
                .withJsonSupport(JsonSupport.JACKSON)
                .withRawGetter(rawGetter),
            Writer.createDefault());

    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "@JsonIgnore\n"
            + "public Optional<LocalDate> getBirthdate() {\n"
            + "  return Optional.ofNullable(birthdate);\n"
            + "}\n"
            + "\n"
            + "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "@JsonIgnore\n"
            + "public LocalDate getBirthdateOr(LocalDate defaultValue) {\n"
            + "  return birthdate == null ? defaultValue : birthdate;\n"
            + "}\n"
            + "\n"
            + "@JsonProperty(\"birthdate\")\n"
            + "@JsonInclude(JsonInclude.Include.NON_NULL)\n"
            + "@Deprecated\n"
            + "public LocalDate getBirthdateRaw() {\n"
            + "  return birthdate;\n"
            + "}",
        writer.asString());
  }
}
