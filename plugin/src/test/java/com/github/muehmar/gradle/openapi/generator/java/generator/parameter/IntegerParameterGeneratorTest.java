package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class IntegerParameterGeneratorTest {

  @Test
  void generate_when_defaultValue_then_correctRendered() {
    final IntegerParameterGenerator gen = new IntegerParameterGenerator();
    final Parameter limitParam =
        new Parameter(Name.ofString("limitParam"), IntegerType.formatInteger(), Optional.of(15));

    final Writer writer =
        gen.generate(limitParam, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Integer DEFAULT = 15;\n"
            + "  public static final String DEFAULT_STR = \"15\";\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_minAndMaxAndDefaultValue_then_correctRendered() {
    final IntegerParameterGenerator gen = new IntegerParameterGenerator();
    final Parameter limitParam =
        new Parameter(
            Name.ofString("limitParam"),
            IntegerType.formatInteger()
                .withConstraints(Constraints.ofMinAndMax(new Min(1), new Max(1000))),
            Optional.of(15));
    final Writer writer =
        gen.generate(limitParam, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Integer MIN = 1;\n"
            + "  public static final Integer MAX = 1000;\n"
            + "  public static final Integer DEFAULT = 15;\n"
            + "  public static final String DEFAULT_STR = \"15\";\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_noDefaultValue_then_correctRendered() {
    final IntegerParameterGenerator gen = new IntegerParameterGenerator();
    final Parameter limitParam =
        new Parameter(
            Name.ofString("limitParam"),
            IntegerType.formatInteger()
                .withConstraints(Constraints.ofMinAndMax(new Min(1), new Max(1000))),
            Optional.empty());
    final Writer writer =
        gen.generate(limitParam, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Integer MIN = 1;\n"
            + "  public static final Integer MAX = 1000;\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_longType_then_correctJavaSuffix() {
    final IntegerParameterGenerator gen = new IntegerParameterGenerator();
    final Parameter limitParam =
        new Parameter(
            Name.ofString("limitParam"),
            IntegerType.formatLong()
                .withConstraints(Constraints.ofMinAndMax(new Min(1), new Max(1000))),
            Optional.of(15L));
    final Writer writer =
        gen.generate(limitParam, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Long MIN = 1L;\n"
            + "  public static final Long MAX = 1000L;\n"
            + "  public static final Long DEFAULT = 15L;\n"
            + "  public static final String DEFAULT_STR = \"15\";\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_noIntegerParam_then_noOutput() {
    final IntegerParameterGenerator gen = new IntegerParameterGenerator();
    final Parameter limitParam =
        new Parameter(Name.ofString("limitParam"), NumericType.formatFloat(), Optional.empty());

    final Writer writer =
        gen.generate(limitParam, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("", writer.asString());
  }
}
