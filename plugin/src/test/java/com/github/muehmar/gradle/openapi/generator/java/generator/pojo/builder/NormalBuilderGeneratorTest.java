package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

class NormalBuilderGeneratorTest {
  @Test
  void generate_when_allNecessityAndNullabilityVariants_then_correctOutput() {
    final NormalBuilderGenerator generator = new NormalBuilderGenerator();

    final Writer writer =
        generator.generate(
            (JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_POJO_BUILDER::equals));
    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_PROPERTY::equals));

    assertEquals(
        "@JsonPOJOBuilder(withPrefix = \"set\")\n"
            + "public static final class Builder {\n"
            + "\n"
            + "  private Builder() {\n"
            + "  }\n"
            + "\n"
            + "  private String requiredStringVal;\n"
            + "  private String requiredNullableStringVal;\n"
            + "  private boolean isRequiredNullableStringValPresent = false;\n"
            + "  private String optionalStringVal;\n"
            + "  private String optionalNullableStringVal;\n"
            + "  private boolean isOptionalNullableStringValNull = false;\n"
            + "\n"
            + "  /**\n"
            + "   * RequiredStringVal\n"
            + "   */\n"
            + "  @JsonProperty(\"requiredStringVal\")\n"
            + "  private Builder setRequiredStringVal(String requiredStringVal) {\n"
            + "    this.requiredStringVal = requiredStringVal;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * RequiredNullableStringVal\n"
            + "   */\n"
            + "  @JsonProperty(\"requiredNullableStringVal\")\n"
            + "  private Builder setRequiredNullableStringVal(String requiredNullableStringVal) {\n"
            + "    this.requiredNullableStringVal = requiredNullableStringVal;\n"
            + "    this.isRequiredNullableStringValPresent = true;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * RequiredNullableStringVal\n"
            + "   */\n"
            + "  private Builder setRequiredNullableStringVal(Optional<String> requiredNullableStringVal) {\n"
            + "    this.requiredNullableStringVal = requiredNullableStringVal.orElse(null);\n"
            + "    this.isRequiredNullableStringValPresent = true;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalStringVal\n"
            + "   */\n"
            + "  @JsonProperty(\"optionalStringVal\")\n"
            + "  public Builder setOptionalStringVal(String optionalStringVal) {\n"
            + "    this.optionalStringVal = optionalStringVal;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalStringVal\n"
            + "   */\n"
            + "  public Builder setOptionalStringVal(Optional<String> optionalStringVal) {\n"
            + "    this.optionalStringVal = optionalStringVal.orElse(null);\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalNullableStringVal\n"
            + "   */\n"
            + "  @JsonProperty(\"optionalNullableStringVal\")\n"
            + "  public Builder setOptionalNullableStringVal(String optionalNullableStringVal) {\n"
            + "    this.optionalNullableStringVal = optionalNullableStringVal;\n"
            + "    this.isOptionalNullableStringValNull = optionalNullableStringVal == null;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalNullableStringVal\n"
            + "   */\n"
            + "  public Builder setOptionalNullableStringVal(Tristate<String> optionalNullableStringVal) {\n"
            + "    this.optionalNullableStringVal = optionalNullableStringVal.onValue(val -> val).onNull(() -> null).onAbsent(() -> null);\n"
            + "    this.isOptionalNullableStringValNull = optionalNullableStringVal.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false);\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public NecessityAndNullabilityDto build() {\n"
            + "    return new NecessityAndNullabilityDto(requiredStringVal, requiredNullableStringVal, isRequiredNullableStringValPresent, optionalStringVal, optionalNullableStringVal, isOptionalNullableStringValNull);\n"
            + "  }\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_allNecessityAndNullabilityVariantsDisabledJackson_then_correctOutput() {
    final NormalBuilderGenerator generator = new NormalBuilderGenerator();

    final Writer writer =
        generator.generate(
            (JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));

    assertEquals(
        "public static final class Builder {\n"
            + "\n"
            + "  private Builder() {\n"
            + "  }\n"
            + "\n"
            + "  private String requiredStringVal;\n"
            + "  private String requiredNullableStringVal;\n"
            + "  private boolean isRequiredNullableStringValPresent = false;\n"
            + "  private String optionalStringVal;\n"
            + "  private String optionalNullableStringVal;\n"
            + "  private boolean isOptionalNullableStringValNull = false;\n"
            + "\n"
            + "  /**\n"
            + "   * RequiredStringVal\n"
            + "   */\n"
            + "  private Builder setRequiredStringVal(String requiredStringVal) {\n"
            + "    this.requiredStringVal = requiredStringVal;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * RequiredNullableStringVal\n"
            + "   */\n"
            + "  private Builder setRequiredNullableStringVal(String requiredNullableStringVal) {\n"
            + "    this.requiredNullableStringVal = requiredNullableStringVal;\n"
            + "    this.isRequiredNullableStringValPresent = true;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * RequiredNullableStringVal\n"
            + "   */\n"
            + "  private Builder setRequiredNullableStringVal(Optional<String> requiredNullableStringVal) {\n"
            + "    this.requiredNullableStringVal = requiredNullableStringVal.orElse(null);\n"
            + "    this.isRequiredNullableStringValPresent = true;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalStringVal\n"
            + "   */\n"
            + "  public Builder setOptionalStringVal(String optionalStringVal) {\n"
            + "    this.optionalStringVal = optionalStringVal;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalStringVal\n"
            + "   */\n"
            + "  public Builder setOptionalStringVal(Optional<String> optionalStringVal) {\n"
            + "    this.optionalStringVal = optionalStringVal.orElse(null);\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalNullableStringVal\n"
            + "   */\n"
            + "  public Builder setOptionalNullableStringVal(String optionalNullableStringVal) {\n"
            + "    this.optionalNullableStringVal = optionalNullableStringVal;\n"
            + "    this.isOptionalNullableStringValNull = optionalNullableStringVal == null;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalNullableStringVal\n"
            + "   */\n"
            + "  public Builder setOptionalNullableStringVal(Tristate<String> optionalNullableStringVal) {\n"
            + "    this.optionalNullableStringVal = optionalNullableStringVal.onValue(val -> val).onNull(() -> null).onAbsent(() -> null);\n"
            + "    this.isOptionalNullableStringValNull = optionalNullableStringVal.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false);\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public NecessityAndNullabilityDto build() {\n"
            + "    return new NecessityAndNullabilityDto(requiredStringVal, requiredNullableStringVal, isRequiredNullableStringValPresent, optionalStringVal, optionalNullableStringVal, isOptionalNullableStringValNull);\n"
            + "  }\n"
            + "}",
        writer.asString());
  }

  @Test
  void generate_when_allNecessityAndNullabilityVariantsDisabledSafeBuilder_then_correctOutput() {
    final NormalBuilderGenerator generator = new NormalBuilderGenerator();

    final Writer writer =
        generator.generate(
            (JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings().withEnableSafeBuilder(false),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OPTIONAL::equals));
    assertTrue(writer.getRefs().exists(OpenApiUtilRefs.TRISTATE::equals));

    assertEquals(
        "public static Builder newBuilder() {\n"
            + "  return new Builder();\n"
            + "}\n"
            + "\n"
            + "@JsonPOJOBuilder(withPrefix = \"set\")\n"
            + "public static final class Builder {\n"
            + "\n"
            + "  private String requiredStringVal;\n"
            + "  private String requiredNullableStringVal;\n"
            + "  private boolean isRequiredNullableStringValPresent = false;\n"
            + "  private String optionalStringVal;\n"
            + "  private String optionalNullableStringVal;\n"
            + "  private boolean isOptionalNullableStringValNull = false;\n"
            + "\n"
            + "  /**\n"
            + "   * RequiredStringVal\n"
            + "   */\n"
            + "  @JsonProperty(\"requiredStringVal\")\n"
            + "  public Builder setRequiredStringVal(String requiredStringVal) {\n"
            + "    this.requiredStringVal = requiredStringVal;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * RequiredNullableStringVal\n"
            + "   */\n"
            + "  @JsonProperty(\"requiredNullableStringVal\")\n"
            + "  public Builder setRequiredNullableStringVal(String requiredNullableStringVal) {\n"
            + "    this.requiredNullableStringVal = requiredNullableStringVal;\n"
            + "    this.isRequiredNullableStringValPresent = true;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * RequiredNullableStringVal\n"
            + "   */\n"
            + "  public Builder setRequiredNullableStringVal(Optional<String> requiredNullableStringVal) {\n"
            + "    this.requiredNullableStringVal = requiredNullableStringVal.orElse(null);\n"
            + "    this.isRequiredNullableStringValPresent = true;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalStringVal\n"
            + "   */\n"
            + "  @JsonProperty(\"optionalStringVal\")\n"
            + "  public Builder setOptionalStringVal(String optionalStringVal) {\n"
            + "    this.optionalStringVal = optionalStringVal;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalStringVal\n"
            + "   */\n"
            + "  public Builder setOptionalStringVal(Optional<String> optionalStringVal) {\n"
            + "    this.optionalStringVal = optionalStringVal.orElse(null);\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalNullableStringVal\n"
            + "   */\n"
            + "  @JsonProperty(\"optionalNullableStringVal\")\n"
            + "  public Builder setOptionalNullableStringVal(String optionalNullableStringVal) {\n"
            + "    this.optionalNullableStringVal = optionalNullableStringVal;\n"
            + "    this.isOptionalNullableStringValNull = optionalNullableStringVal == null;\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  /**\n"
            + "   * OptionalNullableStringVal\n"
            + "   */\n"
            + "  public Builder setOptionalNullableStringVal(Tristate<String> optionalNullableStringVal) {\n"
            + "    this.optionalNullableStringVal = optionalNullableStringVal.onValue(val -> val).onNull(() -> null).onAbsent(() -> null);\n"
            + "    this.isOptionalNullableStringValNull = optionalNullableStringVal.onValue(ignore -> false).onNull(() -> true).onAbsent(() -> false);\n"
            + "    return this;\n"
            + "  }\n"
            + "\n"
            + "  public NecessityAndNullabilityDto build() {\n"
            + "    return new NecessityAndNullabilityDto(requiredStringVal, requiredNullableStringVal, isRequiredNullableStringValPresent, optionalStringVal, optionalNullableStringVal, isOptionalNullableStringValNull);\n"
            + "  }\n"
            + "}",
        writer.asString());
  }
}
