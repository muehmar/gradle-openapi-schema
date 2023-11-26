package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleMemberSetterGenerator.singleMemberSetterGenerator;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SetterBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleMemberSetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
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
      SafeBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            allOfMemberGenerator(),
            pojo ->
                BuilderStage.createStages(builderVariant, pojo)
                    .toPList()
                    .flatMapOptional(BuilderStage::asAllOfBuilderStage),
            newLine());
  }

  private static Generator<AllOfBuilderStage, PojoSettings> allOfMemberGenerator() {
    final PList<Generator<AllOfBuilderStage, PojoSettings>> content =
        PList.of(normalSetter(), optionalSetter(), tristateSetter(), dtoSetter());
    return SingleBuilderClassGenerator.singleBuilderClassGenerator(
        AllOfBuilderStage::getName, content);
  }

  private static Generator<AllOfBuilderStage, PojoSettings> normalSetter() {
    final SingleMemberSetterGenerator.Setter<AllOfMember> setter =
        SetterBuilder.<AllOfMember>create()
            .includeInBuilder(ignore -> true)
            .typeFormat("%s")
            .addRefs(writer -> writer)
            .build();
    return Generator.<AllOfBuilderStage, PojoSettings>emptyGen()
        .appendOptional(singleMemberSetterGenerator(setter), AllOfMember::fromStage);
  }

  private static Generator<AllOfBuilderStage, PojoSettings> optionalSetter() {
    final SingleMemberSetterGenerator.Setter<AllOfMember> setter =
        SetterBuilder.<AllOfMember>create()
            .includeInBuilder(AllOfMember::isJavaOptional)
            .typeFormat("Optional<%s>")
            .addRefs(writer -> writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL))
            .build();
    return Generator.<AllOfBuilderStage, PojoSettings>emptyGen()
        .appendOptional(singleMemberSetterGenerator(setter), AllOfMember::fromStage);
  }

  private static Generator<AllOfBuilderStage, PojoSettings> tristateSetter() {
    final SingleMemberSetterGenerator.Setter<AllOfMember> setter =
        SetterBuilder.<AllOfMember>create()
            .includeInBuilder(AllOfMember::isJavaTristate)
            .typeFormat("Tristate<%s>")
            .addRefs(writer -> writer.ref(OpenApiUtilRefs.TRISTATE))
            .build();
    return Generator.<AllOfBuilderStage, PojoSettings>emptyGen()
        .appendOptional(singleMemberSetterGenerator(setter), AllOfMember::fromStage);
  }

  private static Generator<AllOfBuilderStage, PojoSettings> dtoSetter() {
    return MethodGenBuilder.<AllOfBuilderStage, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(stage -> stage.getNextStage().getName())
        .methodName(
            (stage, settings) ->
                stage
                    .getAllOfSubPojo()
                    .prefixedClassNameForMethod(settings.getBuilderMethodPrefix())
                    .asString())
        .singleArgument(
            stage ->
                new MethodGen.Argument(stage.getAllOfSubPojo().getClassName().asString(), "dto"))
        .content(
            (stage, s, w) ->
                w.println(
                    "return new %s(builder.%s(dto));",
                    stage.getNextStage().getName(),
                    stage.getAllOfSubPojo().prefixedClassNameForMethod(s.getBuilderMethodPrefix())))
        .build()
        .filter(stage -> stage.getMemberIndex() == 0);
  }

  @Value
  private static class AllOfMember implements SingleMemberSetterGenerator.Member {
    AllOfBuilderStage stage;
    JavaPojoMember member;

    static Optional<AllOfMember> fromStage(AllOfBuilderStage stage) {
      return stage.getMember().map(member -> new AllOfMember(stage, member));
    }

    @Override
    public String stageClassName() {
      return stage.getName();
    }

    @Override
    public String nextStageClassName() {
      return stage.getNextMemberStage().getName();
    }

    public boolean isJavaOptional() {
      return member.isOptionalAndNotNullable() || member.isRequiredAndNullable();
    }

    public boolean isJavaTristate() {
      return member.isOptionalAndNullable();
    }
  }
}
