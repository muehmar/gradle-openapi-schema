package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.oneof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.SingleBuilderClassGenerator.singleBuilderClassGenerator;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliary.SinglePojoContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class OneOfBuilderGenerator {
  private static final JavaName CONTAINER_NAME = JavaName.fromString("OneOfContainer");

  private OneOfBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> oneOfBuilderGenerator(
      StagedBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            oneOfBuilder(),
            pojo ->
                BuilderStage.createStages(builderVariant, pojo)
                    .toPList()
                    .flatMapOptional(BuilderStage::asOneOfBuilderStage),
            newLine());
  }

  private static Generator<OneOfBuilderStage, PojoSettings> oneOfBuilder() {
    return singleBuilderClassGenerator(OneOfBuilderStage::getName, setters());
  }

  private static Generator<OneOfBuilderStage, PojoSettings> setters() {
    return Generator.<OneOfBuilderStage, PojoSettings>emptyGen()
        .appendList(singleOneOfPojoSetter(), SingleOneOfPojoWrapper::fromStage, newLine())
        .appendSingleBlankLine()
        .append(oneOfContainerSetter(), OneOfStageWrapper::wrap);
  }

  private static Generator<OneOfStageWrapper, PojoSettings> oneOfContainerSetter() {
    return MethodGenBuilder.<OneOfStageWrapper, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(OneOfStageWrapper::getNextStageName)
        .methodName(
            (pojo, settings) ->
                CONTAINER_NAME.prefixedMethodName(settings.getBuilderMethodPrefix()).asString())
        .singleArgument(stage -> new Argument(stage.getContainerName(), "container"))
        .doesNotThrow()
        .content(oneOfContainerSetterContent())
        .build();
  }

  private static Generator<OneOfStageWrapper, PojoSettings> oneOfContainerSetterContent() {
    return Generator.<OneOfStageWrapper, PojoSettings>emptyGen()
        .appendList(singleContainerPropertySetter(), OneOfStageWrapper::getAllPojos, newLine())
        .append((stage, s, w) -> w.println("return new %s(builder);", stage.getNextStageName()));
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

  private static Generator<SingleOneOfPojoWrapper, PojoSettings> singleOneOfPojoSetter() {
    return MethodGenBuilder.<SingleOneOfPojoWrapper, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(SingleOneOfPojoWrapper::nextStageClassName)
        .methodName(SingleOneOfPojoWrapper::getPrefixedClassNameForMethod)
        .singleArgument(m -> new Argument(m.oneOfSubPojo.getClassName().asString(), "dto"))
        .doesNotThrow()
        .content(
            (pojoWrapper, s, w) ->
                w.println(
                    "return new %s(builder.%s(dto));",
                    pojoWrapper.nextStageClassName(), pojoWrapper.getPrefixedClassNameForMethod(s)))
        .build();
  }

  @Value
  private static class OneOfStageWrapper {
    OneOfBuilderStage stage;

    static OneOfStageWrapper wrap(OneOfBuilderStage stage) {
      return new OneOfStageWrapper(stage);
    }

    PList<JavaObjectPojo> getAllPojos() {
      return stage.getOneOfComposition().getPojos().toPList();
    }

    String getNextStageName() {
      return stage.getNextStage().getName();
    }

    String getContainerName() {
      return new SinglePojoContainer(
              stage.getParentPojo().getJavaPojoName(), stage.getOneOfComposition())
          .getContainerName()
          .asString();
    }
  }

  @Value
  private static class SingleOneOfPojoWrapper {
    OneOfBuilderStage stage;
    JavaObjectPojo oneOfSubPojo;

    static PList<SingleOneOfPojoWrapper> fromStage(OneOfBuilderStage stage) {
      return stage
          .getOneOfComposition()
          .getPojos()
          .map(oneOfSubPojo -> new SingleOneOfPojoWrapper(stage, oneOfSubPojo))
          .toPList();
    }

    public String nextStageClassName() {
      return stage.getNextStage().getName();
    }

    public String getPrefixedClassNameForMethod(PojoSettings settings) {
      return oneOfSubPojo.prefixedClassNameForMethod(settings.getBuilderMethodPrefix()).asString();
    }
  }
}
