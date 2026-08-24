package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class RequiredAdditionalPropertiesSetterGenerator {
  private RequiredAdditionalPropertiesSetterGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings>
      requiredAdditionalPropertiesSetterGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            requiredAdditionalPropertiesSetter(),
            JavaObjectPojo::getAllPermanentRequiredAdditionalProperties,
            newLine());
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings>
      requiredAdditionalPropertiesSetter() {
    return normalSetter()
        .append(
            Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
                .appendSingleBlankLine()
                .append(optionalSetter())
                .filter(JavaRequiredAdditionalProperty::isNullable));
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings> normalSetter() {
    return Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
        .append(
            MethodGenBuilder.<JavaRequiredAdditionalProperty, PojoSettings>create()
                .modifiers((p, s) -> JavaModifiers.of(s.isEnableStagedBuilder() ? PRIVATE : PUBLIC))
                .noGenericTypes()
                .returnType("Builder")
                .methodName(RequiredAdditionalPropertiesSetterGenerator::createMethodName)
                .singleArgument(
                    rp ->
                        argument(
                            rp.getJavaType().getWriteableParameterizedClassName(), rp.getName()))
                .doesNotThrow()
                .content(
                    rp ->
                        String.format(
                            "return addAdditionalProperty(\"%s\", %s);",
                            rp.getName(), rp.getName()))
                .build());
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings> optionalSetter() {
    return Generator.<JavaRequiredAdditionalProperty, PojoSettings>emptyGen()
        .append(
            MethodGenBuilder.<JavaRequiredAdditionalProperty, PojoSettings>create()
                .modifiers((p, s) -> JavaModifiers.of(s.isEnableStagedBuilder() ? PRIVATE : PUBLIC))
                .noGenericTypes()
                .returnType("Builder")
                .methodName(RequiredAdditionalPropertiesSetterGenerator::createMethodName)
                .singleArgument(
                    rp ->
                        argument(
                            String.format(
                                "Optional<%s>",
                                rp.getJavaType().getWriteableParameterizedClassName()),
                            rp.getName()))
                .doesNotThrow()
                .content(
                    rp ->
                        String.format(
                            "return addAdditionalProperty(\"%s\", %s.orElse(null));",
                            rp.getName(), rp.getName()))
                .build())
        .append(RefsGenerator.optionalRef());
  }

  private static String createMethodName(JavaRequiredAdditionalProperty rp, PojoSettings settings) {
    return rp.getName().prefixedMethodName(settings.getBuilderMethodPrefix()).asString();
  }
}
