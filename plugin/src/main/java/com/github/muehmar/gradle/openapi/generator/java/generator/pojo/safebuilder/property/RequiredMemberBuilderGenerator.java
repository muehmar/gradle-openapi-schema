package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator.singleBuilderClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.SingleMemberSetterGenerator.singleMemberSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.SetterBuilder.fullSetterBuilder;
import static io.github.muehmar.codegenerator.Generator.newLine;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.DefaultSetterMember;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.NullableListItemsSetterMember;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.Setter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.SetterMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.Optional;

public class RequiredMemberBuilderGenerator {

  private static final Setter<SetterMember> NORMAL_SETTER =
      fullSetterBuilder()
          .includeInBuilder(ignore -> true)
          .typeFormat("%s")
          .addRefs(writer -> writer)
          .build();
  private static final Setter<SetterMember> OPTIONAL_SETTER =
      fullSetterBuilder()
          .includeInBuilder(ms -> ms.getMember().isNullable())
          .typeFormat("Optional<%s>")
          .addRefs(writer -> writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL))
          .build();

  private RequiredMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> requiredMemberBuilderGenerator(
      SafeBuilderVariant builderVariant) {
    final Generator<RequiredPropertyBuilderStage, PojoSettings> singleBuilderClass =
        singleBuilderClassGenerator(
            RequiredPropertyBuilderStage::getName, builderMethodsOfFirstRequiredMemberGenerator());
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            singleBuilderClass,
            pojo -> RequiredMemberBuilderGenerator.stagesFromPojo(builderVariant, pojo),
            newLine());
  }

  public static Generator<RequiredPropertyBuilderStage, PojoSettings>
      builderMethodsOfFirstRequiredMemberGenerator() {
    return Generator.<RequiredPropertyBuilderStage, PojoSettings>emptyGen()
        .appendList(
            singleMemberSetterMethods(),
            RequiredMemberBuilderGenerator::setterMembersFromStage,
            newLine());
  }

  private static Generator<SetterMember, PojoSettings> singleMemberSetterMethods() {
    final PList<Setter<SetterMember>> setters = PList.of(NORMAL_SETTER, OPTIONAL_SETTER);
    return singleMemberSetterGenerator(setters);
  }

  public static Iterable<RequiredPropertyBuilderStage> stagesFromPojo(
      SafeBuilderVariant builderVariant, JavaObjectPojo pojo) {
    return BuilderStage.createStages(builderVariant, pojo)
        .toPList()
        .flatMapOptional(BuilderStage::asRequiredPropertyBuilderStage);
  }

  private static PList<SetterMember> setterMembersFromStage(RequiredPropertyBuilderStage stage) {
    final SetterMember defaultSetterMember =
        new DefaultSetterMember() {

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
        };
    final Optional<SetterMember> nullableListItemsSetterMember =
        Optional.<SetterMember>of(
                new NullableListItemsSetterMember() {
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
                })
            .filter(sm -> sm.getMember().getJavaType().isNullableItemsArrayType());

    return PList.of(defaultSetterMember).concat(PList.fromOptional(nullableListItemsSetterMember));
  }
}
