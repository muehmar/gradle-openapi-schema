package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties.additionalPropertiesName;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs.TRISTATE;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.MethodGen.Argument.argument;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.Value;

public class FinalOptionalMemberBuilderGenerator {
  private FinalOptionalMemberBuilderGenerator() {}

  private static final SingleSetterMethod STANDARD_SETTER_METHOD =
      new SingleSetterMethod(valueType -> valueType, Optional.empty(), stage -> true);

  private static final SingleSetterMethod OPTIONAL_SETTER_METHOD =
      new SingleSetterMethod(
          valueType -> String.format("Optional<%s>", valueType),
          Optional.of(JAVA_UTIL_OPTIONAL),
          stage ->
              stage
                  .getParentPojo()
                  .getAdditionalProperties()
                  .getType()
                  .getNullability()
                  .isNotNullable());

  private static final SingleSetterMethod TRISTATE_SETTER_METHOD =
      new SingleSetterMethod(
          valueType -> String.format("Tristate<%s>", valueType),
          Optional.of(TRISTATE),
          stage ->
              stage
                  .getParentPojo()
                  .getAdditionalProperties()
                  .getType()
                  .getNullability()
                  .isNullable());

  public static Generator<JavaObjectPojo, PojoSettings> finalOptionalMemberBuilderGenerator(
      StagedBuilderVariant builderVariant) {
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
    return singleAdditionalPropertySetter(STANDARD_SETTER_METHOD)
        .appendSingleBlankLine()
        .append(singleAdditionalPropertySetter(OPTIONAL_SETTER_METHOD))
        .appendSingleBlankLine()
        .append(singleAdditionalPropertySetter(TRISTATE_SETTER_METHOD))
        .appendSingleBlankLine()
        .append(allAdditionalPropertiesSetter())
        .append(
            RefsGenerator.javaTypeRefs(),
            stage -> stage.getParentPojo().getAdditionalProperties().getType())
        .filter(stage -> stage.getParentPojo().getAdditionalProperties().isAllowed());
  }

  private static Generator<LastOptionalPropertyBuilderStage, PojoSettings>
      singleAdditionalPropertySetter(SingleSetterMethod singleSetterMethod) {
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
                        singleSetterMethod
                            .getValueTypeToArgumentType()
                            .apply(
                                stage
                                    .getParentPojo()
                                    .getAdditionalProperties()
                                    .getType()
                                    .getInternalParameterizedClassName()
                                    .asString()),
                        "value")))
        .doesNotThrow()
        .content(
            stage ->
                String.format(
                    "return new %s(builder.addAdditionalProperty(key, value));", stage.getName()))
        .build()
        .append(ref(singleSetterMethod.getRef()))
        .filter(singleSetterMethod.getFilter());
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
                            .getInternalParameterizedClassName()),
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

  @Value
  private static class SingleSetterMethod {
    Function<String, String> valueTypeToArgumentType;
    Optional<String> ref;
    Predicate<LastOptionalPropertyBuilderStage> filter;
  }
}
