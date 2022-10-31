package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
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
import java.util.function.Function;
import org.junit.jupiter.api.Test;

class OptionalNullableGetterTest {

  @Test
  void generator_when_enabledJacksonAndDisabledValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = OptionalNullableGetter.getter();
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.OPTIONAL, Nullability.NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings().withEnableConstraints(false),
            Writer.createDefault());

    assertEquals(
        6,
        writer.getRefs().distinct(Function.identity()).size(),
        "Refs: " + writer.getRefs().mkString(", "));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.JACKSON_NULL_CONTAINER::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_IGNORE::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_PROPERTY::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_INCLUDE::equals));
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
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_disabledJacksonAndEnabledValidation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = OptionalNullableGetter.getter();
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(
            Constraints.ofPattern(Pattern.ofUnescapedString("DatePattern")),
            Necessity.OPTIONAL,
            Nullability.NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));
    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "public Tristate<LocalDate> getBirthdate() {\n"
            + "  return Tristate.ofNullableAndNullFlag(birthdate, isBirthdateNull);\n"
            + "}\n"
            + "\n"
            + "@Pattern(regexp=\"DatePattern\")\n"
            + "private LocalDate getBirthdateRaw() {\n"
            + "  return birthdate;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_optionalNullableSuffix_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = OptionalNullableGetter.getter();
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(
            Constraints.ofPattern(Pattern.ofUnescapedString("DatePattern")),
            Necessity.OPTIONAL,
            Nullability.NULLABLE);

    final GetterSuffixes getterSuffixes =
        GetterSuffixesBuilder.create()
            .requiredSuffix("")
            .requiredNullableSuffix("")
            .optionalSuffix("")
            .optionalNullableSuffix("OptNull")
            .build();

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings()
                .withJsonSupport(JsonSupport.NONE)
                .withEnableConstraints(false)
                .withGetterSuffixes(getterSuffixes),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));
    assertEquals(
        "/**\n"
            + " * Birthdate\n"
            + " */\n"
            + "public Tristate<LocalDate> getBirthdateOptNull() {\n"
            + "  return Tristate.ofNullableAndNullFlag(birthdate, isBirthdateNull);\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_deprecatedAnnotation_then_correctOutputAndRefs() {
    final Generator<JavaPojoMember, PojoSettings> generator = OptionalNullableGetter.getter();
    final JavaPojoMember pojoMember =
        JavaPojoMembers.birthdate(Necessity.OPTIONAL, Nullability.NULLABLE);

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
            + "@Deprecated\n"
            + "public LocalDate getBirthdateRaw() {\n"
            + "  return birthdate;\n"
            + "}",
        writer.asString());
  }
}
