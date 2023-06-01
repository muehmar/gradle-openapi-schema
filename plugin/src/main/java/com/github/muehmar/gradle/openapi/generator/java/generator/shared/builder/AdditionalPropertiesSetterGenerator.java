package com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.builder.NormalBuilderGenerator.NormalBuilderContent;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

class AdditionalPropertiesSetterGenerator {
  private AdditionalPropertiesSetterGenerator() {}

  public static Generator<NormalBuilderContent, PojoSettings> generator() {
    return Generator.<NormalBuilderContent, PojoSettings>emptyGen()
        .appendOptional(
            additionalPropertiesSetters(), NormalBuilderContent::getAdditionalProperties);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> additionalPropertiesSetters() {
    return singleAdditionalPropertiesSetter()
        .appendSingleBlankLine()
        .append(allAdditionalPropertiesSetter());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      singleAdditionalPropertiesSetter() {
    final Generator<JavaAdditionalProperties, PojoSettings> method =
        MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
            .modifiers(props -> JavaModifiers.of(props.isAllowed() ? PUBLIC : PRIVATE))
            .noGenericTypes()
            .returnType("Builder")
            .methodName("addAdditionalProperty")
            .arguments(
                props ->
                    PList.of(
                        "String key",
                        String.format("%s value", props.getType().getFullClassName())))
            .content(
                (props, s, w) ->
                    w.println(
                            "this.%s.put(key, value);", JavaAdditionalProperties.getPropertyName())
                        .println("return this;"))
            .build()
            .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType);
    return JacksonAnnotationGenerator.<JavaAdditionalProperties>jsonAnySetter().append(method);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> allAdditionalPropertiesSetter() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType("Builder")
        .methodName("setAdditionalProperties")
        .singleArgument(
            props ->
                String.format(
                    "Map<String, %s> %s",
                    props.getType().getFullClassName(), JavaAdditionalProperties.getPropertyName()))
        .content(
            (props, s, w) ->
                w.println(
                        "this.%s = new HashMap<>(%s);",
                        JavaAdditionalProperties.getPropertyName(),
                        JavaAdditionalProperties.getPropertyName())
                    .println("return this;"))
        .build()
        .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType)
        .append(ref(JavaRefs.JAVA_UTIL_MAP))
        .append(ref(JavaRefs.JAVA_UTIL_HASH_MAP))
        .filter(JavaAdditionalProperties::isAllowed);
  }
}
