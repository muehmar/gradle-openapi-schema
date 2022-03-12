package com.github.muehmar.gradle.openapi.generator.java.generator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.data.Pojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class FieldsGeneratorTest {

  @Test
  void fields_when_samplePojo_then_correctOutputAndRef() {
    final Generator<Pojo, PojoSettings> gen = FieldsGenerator.fields();

    final Writer writer =
        gen.generate(Pojos.sample(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.LOCAL_DATE::equals));

    final String output = writer.asString();
    assertEquals(
        "private final long id;\n"
            + "private final String name;\n"
            + "private final LocalDate birthdate;\n"
            + "private final LanguageEnum language;\n"
            + "private final boolean isLanguageNull;",
        output);
  }

  @Test
  void fields_when_arrayPojo_then_correctOutputAndRef() {
    final Generator<Pojo, PojoSettings> gen = FieldsGenerator.fields();

    final Writer writer =
        gen.generate(Pojos.array(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_VALUE::equals));

    final String output = writer.asString();
    assertEquals("@JsonValue\n" + "private final String name;", output);
  }
}
