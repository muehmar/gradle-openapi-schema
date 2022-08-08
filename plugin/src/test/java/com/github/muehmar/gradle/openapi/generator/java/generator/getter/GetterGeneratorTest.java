package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class GetterGeneratorTest {

  @Test
  void generator_when_requiredAndNotNullableField_then_correctOutput() {
    final Generator<PojoMember, PojoSettings> generator = GetterGenerator.generator();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE,
            Necessity.REQUIRED,
            Nullability.NOT_NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

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
  void generator_when_requiredAndNullableField_then_correctOutput() {
    final Generator<PojoMember, PojoSettings> generator = GetterGenerator.generator();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE,
            Necessity.REQUIRED,
            Nullability.NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

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
            + "private LocalDate getBirthdateRaw() {\n"
            + "  return birthdate;\n"
            + "}\n"
            + "\n"
            + "@AssertTrue(message = \"birthdate is required but it is not present\")\n"
            + "private boolean isBirthdatePresent() {\n"
            + "  return isBirthdatePresent;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_optionalAndNotNullableField_then_correctOutput() {
    final Generator<PojoMember, PojoSettings> generator = GetterGenerator.generator();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE,
            Necessity.OPTIONAL,
            Nullability.NOT_NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

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
  void generator_when_optionalAndNullableField_then_correctOutputAnd() {
    final Generator<PojoMember, PojoSettings> generator = GetterGenerator.generator();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE,
            Necessity.OPTIONAL,
            Nullability.NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "@JsonIgnore\n"
            + "public Tristate<LocalDate> getBirthdate() {\n"
            + "  return Tristate.ofNullableAndNullFlag(birthdate, isBirthdateNull);\n"
            + "}\n"
            + "\n"
            + "@JsonProperty(\"birthdate\")\n"
            + "@JsonInclude(JsonInclude.Include.NON_NULL)\n"
            + "private Object getBirthdateJackson() {\n"
            + "  return isBirthdateNull ? new JacksonNullContainer<>(birthdate) : birthdate;\n"
            + "}\n"
            + "\n"
            + "private LocalDate getBirthdateRaw() {\n"
            + "  return birthdate;\n"
            + "}",
        writer.asString());
  }
}
