package com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class EnumGeneratorTest {
  @Test
  void generate_when_simpleTopLevelEnumPojo_then_correctOutput() {
    final EnumGenerator generator = EnumGenerator.topLevel();

    final EnumPojo enumPojo =
        EnumPojo.of(
            PojoName.ofNameAndSuffix("Gender", "Dto"),
            "Gender description",
            PList.of("MALE", "FEMALE", "UNKNOWN"));
    final JavaEnumPojo javaEnumPojo = JavaEnumPojo.wrap(enumPojo);

    final Writer writer =
        generator.generate(
            javaEnumPojo, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package com.github.muehmar;\n"
            + "\n"
            + "import com.fasterxml.jackson.annotation.JsonCreator;\n"
            + "import com.fasterxml.jackson.annotation.JsonValue;\n"
            + "import java.util.stream.Collectors;\n"
            + "import java.util.stream.Stream;\n"
            + "\n"
            + "/**\n"
            + " * Gender description\n"
            + " */\n"
            + "public enum GenderDto {\n"
            + "  MALE(\"MALE\", \"\"),\n"
            + "  FEMALE(\"FEMALE\", \"\"),\n"
            + "  UNKNOWN(\"UNKNOWN\", \"\");\n"
            + "\n"
            + "  private final String value;\n"
            + "  private final String description;\n"
            + "\n"
            + "  GenderDto(String value, String description) {\n"
            + "    this.value = value;\n"
            + "    this.description = description;\n"
            + "  }\n"
            + "\n"
            + "  @JsonValue\n"
            + "  public String getValue() {\n"
            + "    return value;\n"
            + "  }\n"
            + "\n"
            + "  @Override\n"
            + "  public String toString() {\n"
            + "    return value;\n"
            + "  }\n"
            + "\n"
            + "  @JsonCreator\n"
            + "  public static GenderDto fromValue(String value) {\n"
            + "    for (GenderDto e: GenderDto.values()) {\n"
            + "      if (e.value.equals(value)) {\n"
            + "        return e;\n"
            + "      }\n"
            + "    }\n"
            + "    final String possibleValues =\n"
            + "      Stream.of(values()).map(GenderDto::getValue).collect(Collectors.joining(\", \"));\n"
            + "    throw new IllegalArgumentException(\n"
            + "      \"Unexpected value '\"\n"
            + "        + value\n"
            + "        + \"' for GenderDto, possible values are [\"\n"
            + "        + possibleValues\n"
            + "        + \"]\");\n"
            + "  }\n"
            + "}",
        writer.asString());
  }
}
