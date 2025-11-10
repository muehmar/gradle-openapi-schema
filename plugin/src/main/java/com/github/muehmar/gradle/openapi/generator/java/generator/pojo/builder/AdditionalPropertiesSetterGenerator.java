package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.TRISTATE;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.FromApiTypeConversionRenderer;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

class AdditionalPropertiesSetterGenerator {
  private AdditionalPropertiesSetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesSetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(additionalPropertiesSetters(), JavaObjectPojo::getAdditionalProperties);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> additionalPropertiesSetters() {
    return singleAdditionalPropertiesSetter()
        .appendSingleBlankLine()
        .append(singleOptionalAdditionalPropertiesSetter())
        .appendSingleBlankLine()
        .append(singleTristateAdditionalPropertiesSetter())
        .appendSingleBlankLine()
        .append(allAdditionalPropertiesSetter());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      singleAdditionalPropertiesSetter() {
    final Generator<JavaAdditionalProperties, PojoSettings> method =
        MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
            .modifiers(
                AdditionalPropertiesSetterGenerator
                    ::createModifiersForSingleAdditionalPropertiesSetter)
            .noGenericTypes()
            .returnType("Builder")
            .methodName("addAdditionalProperty")
            .arguments(
                props ->
                    PList.of(
                        new Argument("String", "key"),
                        new Argument(parameterizedClassName(props), "value")))
            .doesNotThrow()
            .content(
                (props, s, w) ->
                    w.println(
                            "this.%s.put(key, %s);",
                            additionalPropertiesName(),
                            nullSafeApiTypeConversionOrValue(props, "value"))
                        .println("return this;"))
            .build()
            .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType);
    return JacksonAnnotationGenerator.<JavaAdditionalProperties>jsonAnySetter().append(method);
  }

  private static JavaModifiers createModifiersForSingleAdditionalPropertiesSetter(
      JavaAdditionalProperties props) {
    final boolean privateMethod = not(props.isAllowed());
    return JavaModifiers.of(privateMethod ? PRIVATE : PUBLIC);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      singleOptionalAdditionalPropertiesSetter() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(
            AdditionalPropertiesSetterGenerator::createModifiersForSingleAdditionalPropertiesSetter)
        .noGenericTypes()
        .returnType("Builder")
        .methodName("addAdditionalProperty")
        .arguments(
            props ->
                PList.of(
                    new Argument("String", "key"),
                    new Argument("Optional<" + parameterizedClassName(props) + ">", "value")))
        .doesNotThrow()
        .content(singleOptionalAdditionalPropertiesSetterContent())
        .build()
        .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType)
        .append(ref(JAVA_UTIL_OPTIONAL))
        .filter(props -> props.getType().getNullability().isNotNullable());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      singleOptionalAdditionalPropertiesSetterContent() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(
            (props, s, w) ->
                w.println(
                    "value.ifPresent(val -> this.%s.put(key, %s));",
                    additionalPropertiesName(), apiTypeConversionOrValue(props, "val")))
        .append(constant("return this;"));
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      singleTristateAdditionalPropertiesSetter() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(
            AdditionalPropertiesSetterGenerator::createModifiersForSingleAdditionalPropertiesSetter)
        .noGenericTypes()
        .returnType("Builder")
        .methodName("addAdditionalProperty")
        .arguments(
            props ->
                PList.of(
                    new Argument("String", "key"),
                    new Argument("Tristate<" + parameterizedClassName(props) + ">", "value")))
        .doesNotThrow()
        .content(singleTristateAdditionalPropertiesSetterContent())
        .build()
        .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType)
        .append(ref(TRISTATE))
        .filter(props -> props.getType().getNullability().isNullable());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      singleTristateAdditionalPropertiesSetterContent() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(constant("value"))
        .append(
            (props, s, w) ->
                w.println(
                    ".onValue(val -> this.%s.put(key, %s))",
                    additionalPropertiesName(), apiTypeConversionOrValue(props, "val")),
            2)
        .append(constant(".onNull(() -> this.%s.put(key, null))", additionalPropertiesName()), 2)
        .append(constant(".onAbsent(() -> null);", additionalPropertiesName()), 2)
        .append(constant("return this;"));
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> allAdditionalPropertiesSetter() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType("Builder")
        .methodName("setAdditionalProperties")
        .singleArgument(
            props ->
                argument(
                    "Map<String, " + parameterizedClassName(props) + ">",
                    additionalPropertiesName()))
        .doesNotThrow()
        .content(allAdditionalPropertiesSetterContent())
        .build()
        .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType)
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP))
        .filter(JavaAdditionalProperties::isAllowed);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      allAdditionalPropertiesSetterContent() {
    return allAdditionalPropertiesSetterContentForStandardType()
        .append(allAdditionalPropertiesSetterContentForApiType());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      allAdditionalPropertiesSetterContentForStandardType() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(
            constant(
                "this.%s = new HashMap<>(%s);",
                additionalPropertiesName(), additionalPropertiesName()))
        .append(constant("return this;"))
        .filter(props -> props.getType().hasNoApiTypeDeep());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      allAdditionalPropertiesSetterContentForApiType() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(
            (props, s, w) -> w.println("this.%s = new HashMap<>();", additionalPropertiesName()))
        .append(
            (props, s, w) ->
                w.println(
                    "%s.forEach((key, val) -> this.%s.put(key, %s));",
                    additionalPropertiesName(),
                    additionalPropertiesName(),
                    nullSafeApiTypeConversionOrValue(props, "val")))
        .append(constant("return this;"))
        .filter(props -> props.getType().hasApiTypeDeep());
  }

  private static String nullSafeApiTypeConversionOrValue(
      JavaAdditionalProperties props, String valueName) {
    return props
        .getType()
        .getApiType()
        .map(
            apiType ->
                FromApiTypeConversionRenderer.fromApiTypeConversion(
                    apiType, valueName, ConversionGenerationMode.NULL_SAFE))
        .map(Writer::asString)
        .orElse(valueName);
  }

  private static String apiTypeConversionOrValue(JavaAdditionalProperties props, String valueName) {
    return props
        .getType()
        .getApiType()
        .map(
            apiType ->
                FromApiTypeConversionRenderer.fromApiTypeConversion(
                    apiType, valueName, ConversionGenerationMode.NO_NULL_CHECK))
        .map(Writer::asString)
        .orElse(valueName);
  }

  private static String parameterizedClassName(JavaAdditionalProperties props) {
    return props.getType().getWriteableParameterizedClassName().asString();
  }
}
