package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class NumericParameterGeneratorTest {

  @Test
  void generate_when_defaultValue_then_correctRendered() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(Name.ofString("limitParam"), NumericType.formatDouble(), Optional.of(15.12));
    final JavaParameter limitParam = JavaParameter.wrap(param);

    final Writer writer = gen.generate(limitParam, defaultTestSettings(), javaWriter());
    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Double DEFAULT = 15.12;\n"
            + "  public static final String DEFAULT_STR = \"15.12\";\n"
            + "\n"
            + "  public static boolean matchesLimits(Double val) {\n"
            + "    return true;\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_decimalMinAndDecimalMaxAndDefaultValue_then_correctRendered() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(
            Name.ofString("limitParam"),
            NumericType.formatDouble()
                .withConstraints(
                    Constraints.ofDecimalMinAndMax(
                        new DecimalMin("1.01", false), new DecimalMax("50.5", true))),
            Optional.of(15.12));
    final JavaParameter limitParam = JavaParameter.wrap(param);

    final Writer writer = gen.generate(limitParam, defaultTestSettings(), javaWriter());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Double MIN = 1.01;\n"
            + "  public static final boolean EXCLUSIVE_MIN = true;\n"
            + "  public static final Double MAX = 50.5;\n"
            + "  public static final boolean EXCLUSIVE_MAX = false;\n"
            + "  public static final Double DEFAULT = 15.12;\n"
            + "  public static final String DEFAULT_STR = \"15.12\";\n"
            + "\n"
            + "  public static boolean matchesLimits(Double val) {\n"
            + "    return MIN < val && val <= MAX;\n"
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
            NumericType.formatDouble()
                .withConstraints(
                    Constraints.ofDecimalMinAndMax(
                        new DecimalMin("1.01", false), new DecimalMax("50.5", true))),
            Optional.empty());

    final JavaParameter limitParam = JavaParameter.wrap(param);

    final Writer writer = gen.generate(limitParam, defaultTestSettings(), javaWriter());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Double MIN = 1.01;\n"
            + "  public static final boolean EXCLUSIVE_MIN = true;\n"
            + "  public static final Double MAX = 50.5;\n"
            + "  public static final boolean EXCLUSIVE_MAX = false;\n"
            + "\n"
            + "  public static boolean matchesLimits(Double val) {\n"
            + "    return MIN < val && val <= MAX;\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_floatType_then_correctJavaSuffix() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(
            Name.ofString("limitParam"),
            NumericType.formatFloat()
                .withConstraints(
                    Constraints.ofDecimalMinAndMax(
                        new DecimalMin("1.01", false), new DecimalMax("50.5", true))),
            Optional.of(15.12));
    final JavaParameter limitParam = JavaParameter.wrap(param);

    final Writer writer = gen.generate(limitParam, defaultTestSettings(), javaWriter());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class LimitParam {\n"
            + "  private LimitParam() {}\n"
            + "\n"
            + "  public static final Float MIN = 1.01f;\n"
            + "  public static final boolean EXCLUSIVE_MIN = true;\n"
            + "  public static final Float MAX = 50.5f;\n"
            + "  public static final boolean EXCLUSIVE_MAX = false;\n"
            + "  public static final Float DEFAULT = 15.12f;\n"
            + "  public static final String DEFAULT_STR = \"15.12\";\n"
            + "\n"
            + "  public static boolean matchesLimits(Float val) {\n"
            + "    return MIN < val && val <= MAX;\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }
}
