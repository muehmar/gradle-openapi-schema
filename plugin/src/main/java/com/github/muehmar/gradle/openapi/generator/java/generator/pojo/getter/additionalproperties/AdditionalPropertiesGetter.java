package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.FrameworkAdditionalPropertiesGetter.frameworkAdditionalPropertiesGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.GetAdditionalPropertiesList.getAdditionalPropertiesListGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.additionalproperties.SingleAdditionalPropertyGetter.singleAdditionalPropertyGetterGenerator;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class AdditionalPropertiesGetter {
  static final String CAST_ADDITIONAL_PROPERTY_METHOD_NAME = "castAdditionalProperty";

  private AdditionalPropertiesGetter() {}

  public static Generator<JavaObjectPojo, PojoSettings> additionalPropertiesGetterGenerator() {
    return frameworkAdditionalPropertiesGetterGenerator()
        .appendSingleBlankLine()
        .append(getAdditionalPropertiesListGenerator())
        .appendSingleBlankLine()
        .append(singleAdditionalPropertyGetterGenerator())
        .appendSingleBlankLine()
        .append(additionalPropertyCastMethod(), JavaObjectPojo::getAdditionalProperties)
        .filter(pojo -> pojo.getAdditionalProperties().isAllowed());
  }

  private static Generator<JavaAdditionalProperties, PojoSettings> additionalPropertyCastMethod() {
    return MethodGenBuilder.<JavaAdditionalProperties, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType(p -> String.format("Optional<%s>", p.getType().getParameterizedClassName()))
        .methodName(CAST_ADDITIONAL_PROPERTY_METHOD_NAME)
        .singleArgument(p -> new Argument("Object", "property"))
        .doesNotThrow()
        .content(additionalPropertyCastMethodContent())
        .build()
        .append(ref(JavaRefs.JAVA_UTIL_OPTIONAL))
        .filter(AdditionalPropertiesGetter::isNotObjectAdditionalPropertiesType);
  }

  private static Generator<JavaAdditionalProperties, PojoSettings>
      additionalPropertyCastMethodContent() {
    return Generator.<JavaAdditionalProperties, PojoSettings>emptyGen()
        .append(constant("try {"))
        .append(
            (p, s, w) ->
                w.println(
                    "return Optional.of((%s) property);", p.getType().getParameterizedClassName()),
            1)
        .append(constant("} catch (ClassCastException e) {"))
        .append(constant("return Optional.empty();"), 1)
        .append(constant("}"))
        .append(ref(JavaRefs.JAVA_UTIL_OPTIONAL));
  }

  private static boolean isObjectAdditionalPropertiesType(
      JavaAdditionalProperties additionalProperties) {
    return additionalProperties.getType().isAnyType();
  }

  private static boolean isNotObjectAdditionalPropertiesType(
      JavaAdditionalProperties additionalProperties) {
    return not(isObjectAdditionalPropertiesType(additionalProperties));
  }
}
