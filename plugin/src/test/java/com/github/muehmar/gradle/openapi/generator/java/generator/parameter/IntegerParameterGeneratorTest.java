package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class IntegerParameterGeneratorTest {

  @Test
  void generate_when_defaultValue_then_correctRendered() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(Name.ofString("limitParam"), IntegerType.formatInteger(), Optional.of(15));
    final JavaParameter limitParam = JavaParameter.wrap(param);

    final Writer writer = gen.generate(limitParam, defaultTestSettings(), javaWriter());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Integer DEFAULT = 15;\n"
            + "  public static final String DEFAULT_STR = \"15\";\n"
            + "\n"
            + "  public static boolean matchesLimits(Integer val) {\n"
            + "    return true;\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_minAndMaxAndDefaultValue_then_correctRendered() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(
            Name.ofString("limitParam"),
            IntegerType.formatInteger()
                .withConstraints(Constraints.ofMinAndMax(new Min(1), new Max(1000))),
            Optional.of(15));
    final JavaParameter limitParam = JavaParameter.wrap(param);
    final Writer writer = gen.generate(limitParam, defaultTestSettings(), javaWriter());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Integer MIN = 1;\n"
            + "  public static final Integer MAX = 1000;\n"
            + "  public static final Integer DEFAULT = 15;\n"
            + "  public static final String DEFAULT_STR = \"15\";\n"
            + "\n"
            + "  public static boolean matchesLimits(Integer val) {\n"
            + "    return MIN <= val && val <= MAX;\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_noDefaultValue_then_correctRendered() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(
            Name.ofString("limitParam"),
            IntegerType.formatInteger()
                .withConstraints(Constraints.ofMinAndMax(new Min(1), new Max(1000))),
            Optional.empty());
    final JavaParameter limitParam = JavaParameter.wrap(param);
    final Writer writer = gen.generate(limitParam, defaultTestSettings(), javaWriter());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Integer MIN = 1;\n"
            + "  public static final Integer MAX = 1000;\n"
            + "\n"
            + "  public static boolean matchesLimits(Integer val) {\n"
            + "    return MIN <= val && val <= MAX;\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_longType_then_correctJavaSuffix() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(
            Name.ofString("limitParam"),
            IntegerType.formatLong()
                .withConstraints(Constraints.ofMinAndMax(new Min(1), new Max(1000))),
            Optional.of(15L));
    final JavaParameter limitParam = JavaParameter.wrap(param);
    final Writer writer = gen.generate(limitParam, defaultTestSettings(), javaWriter());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Long MIN = 1L;\n"
            + "  public static final Long MAX = 1000L;\n"
            + "  public static final Long DEFAULT = 15L;\n"
            + "  public static final String DEFAULT_STR = \"15\";\n"
            + "\n"
            + "  public static boolean matchesLimits(Long val) {\n"
            + "    return MIN <= val && val <= MAX;\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }
}
