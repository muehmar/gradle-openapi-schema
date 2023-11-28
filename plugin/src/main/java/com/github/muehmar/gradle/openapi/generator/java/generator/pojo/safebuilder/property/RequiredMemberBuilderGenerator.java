package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator.singleBuilderClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleMemberSetterGenerator.singleMemberSetterGenerator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SetterBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleMemberSetterGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import lombok.Value;

public class RequiredMemberBuilderGenerator {

  private static final SingleMemberSetterGenerator.Setter<RequiredMember> NORMAL_SETTER =
      SetterBuilder.<RequiredMember>create()
          .includeInBuilder(ignore -> true)
          .typeFormat("%s")
          .addRefs(writer -> writer)
          .build();
  private static final SingleMemberSetterGenerator.Setter<RequiredMember> OPTIONAL_SETTER =
      SetterBuilder.<RequiredMember>create()
          .includeInBuilder(RequiredMember::isNullable)
          .typeFormat("Optional<%s>")
          .addRefs(writer -> writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL))
          .build();

  private RequiredMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> requiredMemberBuilderGenerator(
      SafeBuilderVariant builderVariant) {
    final Generator<RequiredMember, PojoSettings> singleMemberSetterMethods =
        singleMemberSetterMethods();
    final Generator<RequiredMember, PojoSettings> singleBuilderClass =
        singleBuilderClassGenerator(RequiredMember::stageClassName, singleMemberSetterMethods);
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            singleBuilderClass,
            pojo -> RequiredMember.fromObjectPojo(builderVariant, pojo),
            Generator.newLine());
  }

  public static Generator<RequiredPropertyBuilderStage, PojoSettings>
      builderMethodsOfFirstRequiredMemberGenerator() {
    return Generator.<RequiredPropertyBuilderStage, PojoSettings>emptyGen()
        .append(singleMemberSetterMethods(), RequiredMember::new);
  }

  private static Generator<RequiredMember, PojoSettings> singleMemberSetterMethods() {
    final PList<SingleMemberSetterGenerator.Setter<RequiredMember>> setters =
        PList.of(NORMAL_SETTER, OPTIONAL_SETTER);
    return singleMemberSetterGenerator(setters);
  }

  @Value
  private static class RequiredMember implements SingleMemberSetterGenerator.Member {
    RequiredPropertyBuilderStage stage;

    public static PList<RequiredMember> fromObjectPojo(
        SafeBuilderVariant builderVariant, JavaObjectPojo pojo) {
      return BuilderStage.createStages(builderVariant, pojo)
          .toPList()
          .flatMapOptional(BuilderStage::asRequiredPropertyBuilderStage)
          .map(RequiredMember::new);
    }

    @Override
    public String stageClassName() {
      return stage.getName();
    }

    @Override
    public String nextStageClassName() {
      return stage.getNextStage().getName();
    }

    @Override
    public JavaPojoMember getMember() {
      return stage.getMember();
    }

    public boolean isNullable() {
      return getMember().isNullable();
    }
  }
}
