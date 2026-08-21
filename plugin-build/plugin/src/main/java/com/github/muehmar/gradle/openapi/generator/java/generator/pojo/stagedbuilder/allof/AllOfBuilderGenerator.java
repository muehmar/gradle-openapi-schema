package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.allof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.SingleMemberSetterGenerator.singleMemberSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.model.SetterBuilder.fullSetterBuilder;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.SingleBuilderClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.model.Setter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.model.SetterBuilderImpl.SetterType;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.model.SetterMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class AllOfBuilderGenerator {
  private AllOfBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> allOfBuilderGenerator(
      StagedBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            allOfStageGenerator(),
            pojo ->
                BuilderStage.createStages(builderVariant, pojo)
                    .toPList()
                    .flatMapOptional(BuilderStage::asAllOfBuilderStage),
            newLine());
  }

  private static Generator<AllOfBuilderStage, PojoSettings> allOfStageGenerator() {
    final PList<Generator<AllOfBuilderStage, PojoSettings>> content =
        PList.of(normalSetter(), optionalSetter(), tristateSetter(), dtoSetter());
    return SingleBuilderClassGenerator.singleBuilderClassGenerator(
        AllOfBuilderStage::getName, content);
  }

  private static Generator<AllOfBuilderStage, PojoSettings> normalSetter() {
    final Setter setter =
        fullSetterBuilder()
            .type(SetterType.DEFAULT)
            .includeInBuilder(ignore -> true)
            .typeFormat("%s")
            .addRefs(writer -> writer)
            .build();
    return Generator.<AllOfBuilderStage, PojoSettings>emptyGen()
        .appendOptional(
            singleMemberSetterGenerator(
                PList.of(setter, setter.forType(SetterType.NULLABLE_CONTAINER_VALUES))),
            AllOfBuilderGenerator::standardSetterMemberFromStage);
  }

  private static Generator<AllOfBuilderStage, PojoSettings> optionalSetter() {
    final Setter setter =
        fullSetterBuilder()
            .type(SetterType.DEFAULT)
            .includeInBuilder(AllOfBuilderGenerator::isJavaOptional)
            .typeFormat("Optional<%s>")
            .addRefs(writer -> writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL))
            .build();
    return Generator.<AllOfBuilderStage, PojoSettings>emptyGen()
        .appendOptional(
            singleMemberSetterGenerator(
                PList.of(setter, setter.forType(SetterType.NULLABLE_CONTAINER_VALUES))),
            AllOfBuilderGenerator::standardSetterMemberFromStage);
  }

  private static Generator<AllOfBuilderStage, PojoSettings> tristateSetter() {
    final Setter setter =
        fullSetterBuilder()
            .type(SetterType.DEFAULT)
            .includeInBuilder(m -> m.getMember().isOptionalAndNullable())
            .typeFormat("Tristate<%s>")
            .addRefs(writer -> writer.ref(OpenApiUtilRefs.TRISTATE))
            .build();
    return Generator.<AllOfBuilderStage, PojoSettings>emptyGen()
        .appendOptional(
            singleMemberSetterGenerator(
                PList.of(setter, setter.forType(SetterType.NULLABLE_CONTAINER_VALUES))),
            AllOfBuilderGenerator::standardSetterMemberFromStage);
  }

  private static Generator<AllOfBuilderStage, PojoSettings> dtoSetter() {
    final MethodGen<AllOfPojoStage, PojoSettings> generator =
        MethodGenBuilder.<AllOfPojoStage, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(stage -> stage.getNextStage().getName())
            .methodName(
                (stage, settings) ->
                    stage
                        .getAllOfBuilderStage()
                        .getAllOfSubPojo()
                        .prefixedClassNameForMethod(settings.getBuilderMethodPrefix())
                        .asString())
            .singleArgument(
                stage ->
                    new MethodGen.Argument(
                        stage.getAllOfBuilderStage().getAllOfSubPojo().getClassName().asString(),
                        "dto"))
            .doesNotThrow()
            .content(
                (stage, s, w) ->
                    w.println(
                        "return new %s(builder.%s(dto));",
                        stage.getNextStage().getName(),
                        stage
                            .getAllOfBuilderStage()
                            .getAllOfSubPojo()
                            .prefixedClassNameForMethod(s.getBuilderMethodPrefix())))
            .build();
    return Generator.<AllOfBuilderStage, PojoSettings>emptyGen()
        .appendOptional(generator, AllOfPojoStage::fromAllOfBuilderStage);
  }

  @Value
  private static class AllOfPojoStage {

    AllOfBuilderStage allOfBuilderStage;
    BuilderStage nextStage;

    static Optional<AllOfPojoStage> fromAllOfBuilderStage(AllOfBuilderStage stage) {
      return stage
          .getSubPojoStageObjects()
          .map(
              subPojoStageObjects -> new AllOfPojoStage(stage, subPojoStageObjects.getNextStage()));
    }
  }

  private static boolean isJavaOptional(SetterMember setterMember) {
    return setterMember.getMember().isRequiredAndNullable()
        || setterMember.getMember().isOptionalAndNotNullable();
  }

  private static Optional<SetterMember> standardSetterMemberFromStage(AllOfBuilderStage stage) {
    return stage
        .getMemberStageObjects()
        .map(
            memberStageObjects ->
                new SetterMember(
                    memberStageObjects.getNextStage(), memberStageObjects.getMember()));
  }
}
