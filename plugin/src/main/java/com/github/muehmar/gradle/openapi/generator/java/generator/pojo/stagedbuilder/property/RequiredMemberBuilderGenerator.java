package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.SingleMemberSetterGenerator.singleMemberSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.model.SetterBuilder.fullSetterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.codegenerator.Generator.newLine;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.SingleBuilderClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.StagedBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.model.Setter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.model.SetterBuilderImpl;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.stagedbuilder.setter.model.SetterMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class RequiredMemberBuilderGenerator {

  private static final Setter NORMAL_SETTER =
      fullSetterBuilder()
          .type(SetterBuilderImpl.SetterType.DEFAULT)
          .includeInBuilder(ignore -> true)
          .typeFormat("%s")
          .addRefs(writer -> writer)
          .build();
  private static final Setter OPTIONAL_SETTER =
      fullSetterBuilder()
          .type(SetterBuilderImpl.SetterType.DEFAULT)
          .includeInBuilder(ms -> ms.getMember().isNullable())
          .typeFormat("Optional<%s>")
          .addRefs(writer -> writer.ref(JAVA_UTIL_OPTIONAL))
          .build();

  private RequiredMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> requiredMemberBuilderGenerator(
      StagedBuilderVariant builderVariant) {
    final Generator<RequiredPropertyBuilderStage, PojoSettings> singleBuilderClass =
        SingleBuilderClassGenerator.singleBuilderClassGenerator(
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
        .append(
            singleMemberSetterMethods(),
            stage -> new SetterMember(stage.getNextStage(), stage.getMember()));
  }

  private static Generator<SetterMember, PojoSettings> singleMemberSetterMethods() {
    final PList<Setter> setters =
        PList.of(
            NORMAL_SETTER,
            OPTIONAL_SETTER,
            NORMAL_SETTER.forType(SetterBuilderImpl.SetterType.NULLABLE_CONTAINER_VALUES),
            OPTIONAL_SETTER.forType(SetterBuilderImpl.SetterType.NULLABLE_CONTAINER_VALUES));
    return singleMemberSetterGenerator(setters);
  }

  public static Iterable<RequiredPropertyBuilderStage> stagesFromPojo(
      StagedBuilderVariant builderVariant, JavaObjectPojo pojo) {
    return BuilderStage.createStages(builderVariant, pojo)
        .toPList()
        .flatMapOptional(BuilderStage::asRequiredPropertyBuilderStage);
  }
}
