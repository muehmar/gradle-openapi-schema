package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.FinalOptionalMemberBuilderGenerator.finalOptionalMemberBuilderSetterMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredMemberBuilderGenerator.builderMethodsOfFirstRequiredMemberGenerator;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.FinalRequiredMemberBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.AnyOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class AnyOfBuilderGenerator {
  private static final JavaName CONTAINER_NAME = JavaName.fromString("AnyOfContainer");

  private AnyOfBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> anyOfBuilderGenerator(
      SafeBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            anyOfBuilder(),
            pojo ->
                BuilderStage.createStages(builderVariant, pojo)
                    .toPList()
                    .flatMapOptional(BuilderStage::asAnyOfBuilderStage),
            newLine());
  }

  private static Generator<AnyOfBuilderStage, PojoSettings> anyOfBuilder() {
    return SingleBuilderClassGenerator.singleBuilderClassGenerator(
        AnyOfBuilderStage::getName, dtoSetters());
  }

  private static Generator<AnyOfBuilderStage, PojoSettings> dtoSetters() {
    return Generator.<AnyOfStageWrapper, PojoSettings>emptyGen()
        .appendList(singleAnyOfDtoSetter(), AnyOfStageWrapper::getNameAndPojos, newLine())
        .filter(AnyOfStageWrapper::isNotLastStage)
        .appendSingleBlankLine()
        .append(firstPropertySetters())
        .appendSingleBlankLine()
        .append(anyOfContainerSetter())
        .contraMap(AnyOfStageWrapper::new);
  }

  private static Generator<SingleAnyOfSubPojoWrapper, PojoSettings> singleAnyOfDtoSetter() {
    return MethodGenBuilder.<SingleAnyOfSubPojoWrapper, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(SingleAnyOfSubPojoWrapper::nextStageClassName)
        .methodName(
            (pojoWrapper, settings) ->
                pojoWrapper
                    .anyOfPojo
                    .prefixedClassNameForMethod(settings.getBuilderMethodPrefix())
                    .asString())
        .singleArgument(m -> new Argument(m.anyOfPojo.getClassName().asString(), "dto"))
        .doesNotThrow()
        .content(
            (pojoWrapper, s, w) ->
                w.println(
                    "return new %s(builder.%s(dto));",
                    pojoWrapper.nextStageClassName(),
                    pojoWrapper.anyOfPojo.prefixedClassNameForMethod(s.getBuilderMethodPrefix())))
        .build();
  }

  private static Generator<AnyOfStageWrapper, PojoSettings> firstPropertySetters() {
    return Generator.<AnyOfStageWrapper, PojoSettings>emptyGen()
        .appendOptional(
            builderMethodsOfFirstRequiredMemberGenerator(),
            stageWrapper ->
                stageWrapper
                    .getStage()
                    .getNextPropertyBuilderStage()
                    .asRequiredPropertyBuilderStage())
        .appendOptional(
            FinalRequiredMemberBuilderGenerator.builderMethodsForLastRequiredPropertyBuilderStage(),
            stageWrapper ->
                stageWrapper
                    .getStage()
                    .getNextPropertyBuilderStage()
                    .asLastRequiredPropertyBuilderStage())
        .appendOptional(
            finalOptionalMemberBuilderSetterMethods(),
            stageWrapper ->
                stageWrapper
                    .getStage()
                    .getNextPropertyBuilderStage()
                    .asLastOptionalPropertyBuilderStage())
        .filter(AnyOfStageWrapper::isNotFirstStage);
  }

  private static Generator<AnyOfStageWrapper, PojoSettings> anyOfContainerSetter() {
    return MethodGenBuilder.<AnyOfStageWrapper, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(stageWrapper -> stageWrapper.getStage().getLastStage().getName())
        .methodName(
            (stageWrapper, settings) ->
                CONTAINER_NAME.prefixedMethodName(settings.getBuilderMethodPrefix()).asString())
        .singleArgument(pojo -> new Argument(pojo.getContainerName(), "container"))
        .doesNotThrow()
        .content(anyOfContainerSetterContent())
        .build()
        .filter(AnyOfStageWrapper::isFirstStage);
  }

  private static Generator<AnyOfStageWrapper, PojoSettings> anyOfContainerSetterContent() {
    return Generator.<AnyOfStageWrapper, PojoSettings>emptyGen()
        .appendList(
            singleContainerPropertySetter(),
            stageWrapper -> stageWrapper.getStage().getAnyOfComposition().getPojos(),
            newLine())
        .append(
            (stageWrapper, s, w) ->
                w.println(
                    "return new %s(builder);", stageWrapper.getStage().getLastStage().getName()));
  }

  private static Generator<JavaObjectPojo, PojoSettings> singleContainerPropertySetter() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println("if (container.%s() != null) {", p.prefixedClassNameForMethod("get")))
        .append(
            (p, s, w) ->
                w.println(
                    "builder.%s(container.%s());",
                    p.prefixedClassNameForMethod(s.getBuilderMethodPrefix()),
                    p.prefixedClassNameForMethod("get")),
            1)
        .append(constant("}"));
  }

  @Value
  private static class AnyOfStageWrapper {
    AnyOfBuilderStage stage;

    public PList<SingleAnyOfSubPojoWrapper> getNameAndPojos() {
      return stage
          .getAnyOfComposition()
          .getPojos()
          .map(anyOfPojo -> new SingleAnyOfSubPojoWrapper(stage, anyOfPojo))
          .toPList();
    }

    public String getContainerName() {
      return new AnyOfContainer(
              stage.getParentPojo().getJavaPojoName(), stage.getAnyOfComposition())
          .getContainerName()
          .asString();
    }

    public boolean isFirstStage() {
      return stage.getStageType().equals(AnyOfBuilderStage.StageType.FIRST_STAGE);
    }

    public boolean isNotFirstStage() {
      return not(isFirstStage());
    }

    public boolean isLastStage() {
      return stage.getStageType().equals(AnyOfBuilderStage.StageType.LAST_STAGE);
    }

    public boolean isNotLastStage() {
      return not(isLastStage());
    }
  }

  @Value
  private static class SingleAnyOfSubPojoWrapper {
    AnyOfBuilderStage stage;
    JavaObjectPojo anyOfPojo;

    String nextStageClassName() {
      return stage.getNextAnyOfStage().getName();
    }
  }
}
