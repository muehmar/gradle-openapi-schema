package com.github.muehmar.gradle.openapi.generator.java.generator.jackson;

import static com.github.muehmar.gradle.openapi.generator.java.generator.data.VoidData.noData;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Necessity;
import com.github.muehmar.gradle.openapi.generator.data.Nullability;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class JacksonAnnotationGeneratorTest {

  @Test
  void jsonIgnore_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonIgnore();

    final Writer writer =
        generator.generate(noData(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(1, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_IGNORE::equals));
    assertEquals("@JsonIgnore", writer.asString());
  }

  @Test
  void jsonIgnore_when_disabledJackson_then_noOutput() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonIgnore();

    final Writer writer =
        generator.generate(
            noData(),
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertTrue(writer.getRefs().isEmpty());
    assertEquals("", writer.asString());
  }

  @Test
  void jsonProperty_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<PojoMember, PojoSettings> generator = JacksonAnnotationGenerator.jsonProperty();
    final PojoMember pojoMember =
        new PojoMember(
            Name.of("birthdate"),
            "Birthdate",
            JavaTypes.LOCAL_DATE,
            Necessity.REQUIRED,
            Nullability.NULLABLE);

    final Writer writer =
        generator.generate(pojoMember, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(1, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_PROPERTY::equals));
    assertEquals("@JsonProperty(\"birthdate\")", writer.asString());
  }

  @Test
  void jsonProperty_when_disabledJackson_then_noOutput() {
    final Generator<PojoMember, PojoSettings> generator = JacksonAnnotationGenerator.jsonProperty();
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
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertTrue(writer.getRefs().isEmpty());
    assertEquals("", writer.asString());
  }

  @Test
  void jsonIncludeNonNull_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonIncludeNonNull();

    final Writer writer =
        generator.generate(noData(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(1, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_INCLUDE::equals));
    assertEquals("@JsonInclude(JsonInclude.Include.NON_NULL)", writer.asString());
  }

  @Test
  void jsonIncludeNonNull_when_disabledJackson_then_noOutput() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonIncludeNonNull();

    final Writer writer =
        generator.generate(
            noData(),
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertTrue(writer.getRefs().isEmpty());
    assertEquals("", writer.asString());
  }

  @Test
  void jsonValue_when_enabledJackson_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonValue();

    final Writer writer =
        generator.generate(noData(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(1, writer.getRefs().size());
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_VALUE::equals));
    assertEquals("@JsonValue", writer.asString());
  }

  @Test
  void jsonValue_when_disabledJackson_then_correctOutputAndRefs() {
    final Generator<Void, PojoSettings> generator = JacksonAnnotationGenerator.jsonValue();

    final Writer writer =
        generator.generate(
            noData(),
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertTrue(writer.getRefs().isEmpty());
    assertEquals("", writer.asString());
  }
}
