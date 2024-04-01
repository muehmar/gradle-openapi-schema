package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator.singleBuilderClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.SingleMemberSetterGenerator.singleMemberSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.SetterBuilder.fullSetterBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs.JAVA_UTIL_OPTIONAL;
import static io.github.muehmar.codegenerator.Generator.newLine;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.Setter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.SetterBuilderImpl.SetterType;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.SetterMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class RequiredMemberBuilderGenerator {

  private static final Setter NORMAL_SETTER =
      fullSetterBuilder()
          .type(SetterType.DEFAULT)
          .includeInBuilder(ignore -> true)
          .typeFormat("%s")
          .addRefs(writer -> writer)
          .build();
  private static final Setter OPTIONAL_SETTER =
      fullSetterBuilder()
          .type(SetterType.DEFAULT)
          .includeInBuilder(ms -> ms.getMember().isNullable())
          .typeFormat("Optional<%s>")
          .addRefs(writer -> writer.ref(JAVA_UTIL_OPTIONAL))
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
        .append(
            singleMemberSetterMethods(),
            stage -> new SetterMember(stage.getNextStage(), stage.getMember()));
  }

  private static Generator<SetterMember, PojoSettings> singleMemberSetterMethods() {
    final PList<Setter> setters =
        PList.of(
            NORMAL_SETTER,
            OPTIONAL_SETTER,
            NORMAL_SETTER.forType(SetterType.NULLABLE_ITEMS_LIST),
            OPTIONAL_SETTER.forType(SetterType.NULLABLE_ITEMS_LIST));
    return singleMemberSetterGenerator(setters);
  }

  public static Iterable<RequiredPropertyBuilderStage> stagesFromPojo(
      SafeBuilderVariant builderVariant, JavaObjectPojo pojo) {
    return BuilderStage.createStages(builderVariant, pojo)
        .toPList()
        .flatMapOptional(BuilderStage::asRequiredPropertyBuilderStage);
  }
}
