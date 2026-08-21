package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.factory;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderGenerator.BUILDER_STAGES_CLASS_NAME;
import static io.github.muehmar.codegenerator.Generator.constant;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import java.util.function.Function;

class StandardFactoryMethod {
  private static final String FACTORY_JAVA_DOC =
      "Instantiates a new staged builder. Explicit properties have precedence over additional properties, i.e. an "
          + "additional property with the same name as an explicit property will be discarded.";

  static Generator<JavaObjectPojo, PojoSettings> factoryMethodsForVariant(
      StagedBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(JavaDocGenerator.ofJavaDocString(FACTORY_JAVA_DOC))
        .append(factoryMethod(builderVariant, simpleBuilderMethodName(builderVariant)))
        .appendSingleBlankLine()
        .append(JavaDocGenerator.ofJavaDocString(FACTORY_JAVA_DOC))
        .append(factoryMethod(builderVariant, pojoBuilderMethodName(builderVariant)));
  }

  private static Generator<JavaObjectPojo, PojoSettings> factoryMethod(
      StagedBuilderVariant builderVariant, Function<JavaObjectPojo, String> builderName) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (pojo, s, w) ->
                w.println(
                    "public static %s.%s %s() {",
                    BUILDER_STAGES_CLASS_NAME,
                    BuilderStage.createStages(builderVariant, pojo).head().getName(),
                    builderName.apply(pojo)))
        .append(
            (pojo, s, w) ->
                w.println(
                    "return new %s.%s(new Builder());",
                    BUILDER_STAGES_CLASS_NAME,
                    BuilderStage.createStages(builderVariant, pojo).head().getName()),
            1)
        .append(constant("}"));
  }

  static Function<JavaObjectPojo, String> simpleBuilderMethodName(
      StagedBuilderVariant builderVariant) {
    return pojo -> {
      final String prefix = builderVariant.getBuilderNamePrefix();
      if (prefix.isEmpty()) {
        return "builder";
      } else {
        return String.format("%sBuilder", prefix.toLowerCase());
      }
    };
  }

  private static Function<JavaObjectPojo, String> pojoBuilderMethodName(
      StagedBuilderVariant builderVariant) {
    return pojo -> {
      final String prefix = builderVariant.getBuilderNamePrefix();
      final JavaName className = pojo.getClassName();
      if (prefix.isEmpty()) {
        return String.format("%sBuilder", className.startLowerCase());
      } else {
        return String.format("%s%sBuilder", prefix.toLowerCase(), className);
      }
    };
  }
}
