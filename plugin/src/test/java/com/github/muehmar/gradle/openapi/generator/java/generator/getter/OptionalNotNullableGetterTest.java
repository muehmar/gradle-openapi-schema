package com.github.muehmar.gradle.openapi.generator.java.generator.getter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Necessity;
import com.github.muehmar.gradle.openapi.generator.data.Nullability;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class OptionalNotNullableGetterTest {

  @Test
  void generator_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<PojoMember, PojoSettings> generator = OptionalNotNullableGetter.getter();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE,
            Necessity.OPTIONAL,
            Nullability.NOT_NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertEquals(
        "@JsonIgnore\n"
            + "public Optional<LocalDate> getBirthdate() {\n"
            + "  return Optional.ofNullable(birthdate);\n"
            + "}\n"
            + "\n"
            + "@JsonProperty(\"birthdate\")\n"
            + "@JsonInclude(JsonInclude.Include.NON_NULL)\n"
            + "private LocalDate getBirthdateNullable() {\n"
            + "  return birthdate;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generator_when_disabledJackson_then_correctOutputAndRefs() {
    final Generator<PojoMember, PojoSettings> generator = OptionalNotNullableGetter.getter();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE,
            Necessity.OPTIONAL,
            Nullability.NOT_NULLABLE);

    final Writer writer =
        generator.generate(
            pojoMember,
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_TIME_LOCAL_DATE::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertEquals(
        "public Optional<LocalDate> getBirthdate() {\n"
            + "  return Optional.ofNullable(birthdate);\n"
            + "}",
        writer.asString());
  }
}
