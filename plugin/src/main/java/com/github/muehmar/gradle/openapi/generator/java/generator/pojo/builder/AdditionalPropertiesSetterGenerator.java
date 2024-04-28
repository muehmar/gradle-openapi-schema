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
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

class AdditionalPropertiesSetterGenerator {
  private AdditionalPropertiesSetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesSetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(additionalPropertiesSetters(), JavaObjectPojo::getAdditionalProperties);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> additionalPropertiesSetters() {
    return singleAdditionalPropertiesSetter(true)
        .filter(JavaAdditionalProperties::isNotValueAnyType)
        .appendSingleBlankLine()
        .append(singleAdditionalPropertiesSetter(false))
        .appendSingleBlankLine()
        .append(singleOptionalAdditionalPropertiesSetter())
        .appendSingleBlankLine()
        .append(singleTristateAdditionalPropertiesSetter())
        .appendSingleBlankLine()
        .append(allAdditionalPropertiesSetter());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> singleAdditionalPropertiesSetter(
      boolean forAnyType) {
    final Generator<JavaAdditionalProperties, PojoSettings> method =
        MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
            .modifiers(
                props -> createModifiersForSingleAdditionalPropertiesSetter(props, forAnyType))
            .noGenericTypes()
            .returnType("Builder")
            .methodName("addAdditionalProperty")
            .arguments(
                props ->
                    PList.of(
                        new Argument("String", "key"),
                        new Argument(
                            forAnyType
                                ? "Object"
                                : props.getType().getInternalParameterizedClassName().asString(),
                            "value")))
            .doesNotThrow()
            .content(
                (props, s, w) ->
                    w.println("this.%s.put(key, value);", additionalPropertiesName())
                        .println("return this;"))
            .build()
            .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType);
    return JacksonAnnotationGenerator.<JavaAdditionalProperties>jsonAnySetter()
        .filter(ignore -> not(forAnyType))
        .append(method);
  }

  private static JavaModifiers createModifiersForSingleAdditionalPropertiesSetter(
      JavaAdditionalProperties props, boolean forAnyType) {
    final boolean privateMethod = forAnyType || not(props.isAllowed());
    return JavaModifiers.of(privateMethod ? PRIVATE : PUBLIC);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      singleOptionalAdditionalPropertiesSetter() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(props -> createModifiersForSingleAdditionalPropertiesSetter(props, false))
        .noGenericTypes()
        .returnType("Builder")
        .methodName("addAdditionalProperty")
        .arguments(
            props ->
                PList.of(
                    new Argument("String", "key"),
                    new Argument(
                        "Optional<"
                            + props.getType().getInternalParameterizedClassName().asString()
                            + ">",
                        "value")))
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
            constant("value.ifPresent(val -> this.%s.put(key, val));", additionalPropertiesName()))
        .append(constant("return this;"));
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      singleTristateAdditionalPropertiesSetter() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(props -> createModifiersForSingleAdditionalPropertiesSetter(props, false))
        .noGenericTypes()
        .returnType("Builder")
        .methodName("addAdditionalProperty")
        .arguments(
            props ->
                PList.of(
                    new Argument("String", "key"),
                    new Argument(
                        "Tristate<"
                            + props.getType().getInternalParameterizedClassName().asString()
                            + ">",
                        "value")))
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
        .append(constant(".onValue(val -> this.%s.put(key, val))", additionalPropertiesName()), 2)
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
                    props.getMapContainerType().getInternalParameterizedClassName(),
                    additionalPropertiesName()))
        .doesNotThrow()
        .content(
            (props, s, w) ->
                w.println(
                        "this.%s = new HashMap<>(%s);",
                        additionalPropertiesName(), additionalPropertiesName())
                    .println("return this;"))
        .build()
        .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType)
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP))
        .filter(JavaAdditionalProperties::isAllowed);
  }
}
