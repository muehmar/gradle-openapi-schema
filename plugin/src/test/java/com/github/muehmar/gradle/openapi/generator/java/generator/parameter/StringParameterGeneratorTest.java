package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class StringParameterGeneratorTest {

  @Test
  void generate_when_noSize_then_correctRendered() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(Name.ofString("stringParam"), StringType.noFormat(), Optional.of("mode"));
    final JavaParameter limitParam = JavaParameter.wrap(param);

    final Writer writer =
        gen.generate(limitParam, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class StringParam {\n"
            + "  private StringParam() {}\n"
            + "\n"
            + "  public static final String DEFAULT = \"mode\";\n"
            + "\n"
            + "  public static boolean matchesLimits(String val) {\n"
            + "    return true;\n"
            + "  }\n"
            + "\n"
            + "  public static boolean matchesPattern(String val) {\n"
            + "    return true;\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_minSize_then_correctRendered() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(
            Name.ofString("stringParam"),
            StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMin(1))),
            Optional.empty());
    final JavaParameter limitParam = JavaParameter.wrap(param);

    final Writer writer =
        gen.generate(limitParam, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class StringParam {\n"
            + "  private StringParam() {}\n"
            + "\n"
            + "  public static final int MIN_LENGTH = 1;\n"
            + "\n"
            + "  public static boolean matchesLimits(String val) {\n"
            + "    return MIN_LENGTH <= val.length();\n"
            + "  }\n"
            + "\n"
            + "  public static boolean matchesPattern(String val) {\n"
            + "    return true;\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_maxSize_then_correctRendered() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(
            Name.ofString("stringParam"),
            StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMax(20))),
            Optional.empty());
    final JavaParameter limitParam = JavaParameter.wrap(param);

    final Writer writer =
        gen.generate(limitParam, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class StringParam {\n"
            + "  private StringParam() {}\n"
            + "\n"
            + "  public static final int MAX_LENGTH = 20;\n"
            + "\n"
            + "  public static boolean matchesLimits(String val) {\n"
            + "    return val.length() <= MAX_LENGTH;\n"
            + "  }\n"
            + "\n"
            + "  public static boolean matchesPattern(String val) {\n"
            + "    return true;\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_minAndMaxAndDefault_then_correctRendered() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(
            Name.ofString("stringParam"),
            StringType.noFormat().withConstraints(Constraints.ofSize(Size.of(1, 20))),
            Optional.of("mode"));
    final JavaParameter limitParam = JavaParameter.wrap(param);

    final Writer writer =
        gen.generate(limitParam, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class StringParam {\n"
            + "  private StringParam() {}\n"
            + "\n"
            + "  public static final int MIN_LENGTH = 1;\n"
            + "  public static final int MAX_LENGTH = 20;\n"
            + "  public static final String DEFAULT = \"mode\";\n"
            + "\n"
            + "  public static boolean matchesLimits(String val) {\n"
            + "    return MIN_LENGTH <= val.length() && val.length() <= MAX_LENGTH;\n"
            + "  }\n"
            + "\n"
            + "  public static boolean matchesPattern(String val) {\n"
            + "    return true;\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_pattern_then_correctRendered() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(
            Name.ofString("stringParam"),
            StringType.noFormat()
                .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("[A-Za-z]\\d"))),
            Optional.empty());
    final JavaParameter limitParam = JavaParameter.wrap(param);

    final Writer writer =
        gen.generate(limitParam, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "import java.util.regex.Pattern;\n"
            + "\n"
            + "public final class StringParam {\n"
            + "  private StringParam() {}\n"
            + "\n"
            + "  public static final Pattern PATTERN = Pattern.compile(\"[A-Za-z]\\\\d\");\n"
            + "  public static final String PATTERN_STR = \"[A-Za-z]\\\\d\";\n"
            + "\n"
            + "  public static boolean matchesLimits(String val) {\n"
            + "    return true;\n"
            + "  }\n"
            + "\n"
            + "  public static boolean matchesPattern(String val) {\n"
            + "    return PATTERN.matcher(val).matches();\n"
            + "  }\n"
            + "\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_dateFormat_then_correctRendered() {
    final ParameterGenerator gen = new ParameterGenerator();
    final Parameter param =
        new Parameter(
            Name.ofString("dateParam"),
            StringType.ofFormat(StringType.Format.DATE),
            Optional.empty());
    final JavaParameter limitParam = JavaParameter.wrap(param);

    final Writer writer =
        gen.generate(limitParam, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals(
        "package com.github.muehmar.parameter;\n"
            + "\n"
            + "\n"
            + "public final class DateParam {\n"
            + "  private DateParam() {}\n"
            + "\n"
            + "\n"
            + "}",
        writer.asString());
  }
}
