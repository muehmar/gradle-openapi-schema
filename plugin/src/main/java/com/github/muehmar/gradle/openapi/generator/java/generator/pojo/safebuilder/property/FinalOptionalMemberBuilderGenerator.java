package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;

public class FinalOptionalMemberBuilderGenerator {
  private FinalOptionalMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> finalOptionalMemberBuilderGenerator(
      SafeBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            finalOptionalMemberBuilderGenerator(),
            pojo ->
                BuilderStage.createStages(builderVariant, pojo)
                    .toPList()
                    .flatMapOptional(BuilderStage::asLastOptionalPropertyBuilderStage),
            newLine());
  }

  public static Generator<LastOptionalPropertyBuilderStage, PojoSettings>
      finalOptionalMemberBuilderGenerator() {
    return Generator.<LastOptionalPropertyBuilderStage, PojoSettings>emptyGen()
        .append((stage, s, w) -> w.println("public static final class %s {", stage.getName()))
        .append(constant("private final Builder builder;"), 1)
        .appendNewLine()
        .append((stage, s, w) -> w.println("private %s(Builder builder) {", stage.getName()), 1)
        .append(constant("this.builder = builder;"), 2)
        .append(constant("}"), 1)
        .append(finalOptionalMemberBuilderSetterMethods().indent(1))
        .append(constant("}"));
  }

  public static Generator<LastOptionalPropertyBuilderStage, PojoSettings>
      finalOptionalMemberBuilderSetterMethods() {
    return Generator.<LastOptionalPropertyBuilderStage, PojoSettings>emptyGen()
        .appendSingleBlankLine()
        .append(additionalPropertiesSetters())
        .appendSingleBlankLine()
        .append(
            (stage, s, w) -> w.println("public %s build(){", stage.getParentPojo().getClassName()))
        .append(constant("return builder.build();"), 1)
        .append(constant("}"));
  }

  private static Generator<LastOptionalPropertyBuilderStage, PojoSettings>
      additionalPropertiesSetters() {
    return singleAdditionalPropertySetter()
        .appendSingleBlankLine()
        .append(allAdditionalPropertiesSetter())
        .append(
            RefsGenerator.javaTypeRefs(),
            stage -> stage.getParentPojo().getAdditionalProperties().getType())
        .filter(stage -> stage.getParentPojo().getAdditionalProperties().isAllowed());
  }

  private static Generator<LastOptionalPropertyBuilderStage, PojoSettings>
      singleAdditionalPropertySetter() {
    return MethodGenBuilder.<LastOptionalPropertyBuilderStage, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(LastOptionalPropertyBuilderStage::getName)
        .methodName("addAdditionalProperty")
        .arguments(
            stage ->
                PList.of(
                    argument("String", "key"),
                    argument(
                        stage
                            .getParentPojo()
                            .getAdditionalProperties()
                            .getType()
                            .getParameterizedClassName(),
                        "value")))
        .doesNotThrow()
        .content(
            stage ->
                String.format(
                    "return new %s(builder.addAdditionalProperty(key, value));", stage.getName()))
        .build();
  }

  private static Generator<LastOptionalPropertyBuilderStage, PojoSettings>
      allAdditionalPropertiesSetter() {
    return MethodGenBuilder.<LastOptionalPropertyBuilderStage, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(LastOptionalPropertyBuilderStage::getName)
        .methodName("setAdditionalProperties")
        .singleArgument(
            stage ->
                new Argument(
                    String.format(
                        "Map<String, %s>",
                        stage
                            .getParentPojo()
                            .getAdditionalProperties()
                            .getType()
                            .getParameterizedClassName()),
                    additionalPropertiesName().asString()))
        .doesNotThrow()
        .content(
            stage ->
                String.format(
                    "return new %s(builder.setAdditionalProperties(%s));",
                    stage.getName(), additionalPropertiesName()))
        .build()
        .append(ref(JavaRefs.JAVA_UTIL_MAP));
  }
}
