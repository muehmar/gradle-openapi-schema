package com.github.muehmar.gradle.openapi.generator.java.generator;

import static com.github.muehmar.gradle.openapi.generator.java.JacksonRefs.JSON_POJO_BUILDER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.java.generator.data.Pojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import org.junit.jupiter.api.Test;

class JacksonBuilderGeneratorTest {

  @Test
  void jacksonBuilderGen_when_samplePojo_then_correctOutputAndRefs() {
    final Generator<Pojo, PojoSettings> generator = JacksonBuilderGenerator.jacksonBuilderGen();

    final Writer writer =
        generator.generate(
            Pojos.sample(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    final String output = writer.asString();
    assertTrue(writer.getRefs().exists(JSON_POJO_BUILDER::equals));
    assertEquals(
        "@JsonPOJOBuilder(withPrefix = \"\")\n"
            + "static class Builder {\n"
            + "  private long id;\n"
            + "  private String name;\n"
            + "  private LanguageEnum language;\n"
            + "\n"
            + "  private Builder() {}\n"
            + "\n"
            + "  Builder id(long id) {\n"
            + "    this.id = id;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  Builder name(String name) {\n"
            + "    this.name = name;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  Builder language(LanguageEnum language) {\n"
            + "    this.language = language;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public UserDto build() {\n"
            + "    return new UserDto(id, name, language);\n"
            + "  }\n"
            + "}",
        output);
  }
}