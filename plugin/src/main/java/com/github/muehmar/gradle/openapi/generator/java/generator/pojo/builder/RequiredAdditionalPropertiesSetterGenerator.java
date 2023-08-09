package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

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
            JavaObjectPojo::getRequiredAdditionalProperties,
            newLine());
  }

  private static Generator<JavaRequiredAdditionalProperty, PojoSettings>
      requiredAdditionalPropertiesSetter() {
    return MethodGenBuilder.<JavaRequiredAdditionalProperty, PojoSettings>create()
        .modifiers((p, s) -> JavaModifiers.of(s.isEnableSafeBuilder() ? PRIVATE : PUBLIC))
        .noGenericTypes()
        .returnType("Builder")
        .methodName(RequiredAdditionalPropertiesSetterGenerator::createMethodName)
        .singleArgument(
            rp -> String.format("%s %s", rp.getJavaType().getFullClassName(), rp.getName()))
        .content(
            rp ->
                String.format(
                    "return addAdditionalProperty(\"%s\", %s);", rp.getName(), rp.getName()))
        .build();
  }

  private static String createMethodName(JavaRequiredAdditionalProperty rp, PojoSettings settings) {
    final String prefix = settings.getBuilderMethodPrefix();
    if (prefix.isEmpty()) {
      return rp.getName().asString();
    } else {
      return String.format("%s%s", prefix, rp.getName().startUpperCase());
    }
  }
}