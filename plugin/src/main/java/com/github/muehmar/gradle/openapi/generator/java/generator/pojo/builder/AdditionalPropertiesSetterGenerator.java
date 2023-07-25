package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.jackson.JacksonAnnotationGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifiers;
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
        .append(allAdditionalPropertiesSetter());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> singleAdditionalPropertiesSetter(
      boolean forObjectType) {
    final Generator<JavaAdditionalProperties, PojoSettings> method =
        MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
            .modifiers(
                props -> createModifiersForSingleAdditionalPropertiesSetter(props, forObjectType))
            .noGenericTypes()
            .returnType("Builder")
            .methodName("addAdditionalProperty")
            .arguments(
                props ->
                    PList.of(
                        "String key",
                        String.format(
                            "%s value",
                            forObjectType ? "Object" : props.getType().getFullClassName())))
            .content(
                (props, s, w) ->
                    w.println(
                            "this.%s.put(key, value);", JavaAdditionalProperties.getPropertyName())
                        .println("return this;"))
            .build()
            .append(RefsGenerator.javaTypeRefs(), JavaAdditionalProperties::getType);
    return JacksonAnnotationGenerator.<JavaAdditionalProperties>jsonAnySetter()
        .filter(ignore -> not(forObjectType))
        .append(method);
  }

  private static JavaModifiers createModifiersForSingleAdditionalPropertiesSetter(
      JavaAdditionalProperties props, boolean forObjectType) {
    final boolean privateMethod = forObjectType || not(props.isAllowed());
    return JavaModifiers.of(privateMethod ? PRIVATE : PUBLIC);
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
