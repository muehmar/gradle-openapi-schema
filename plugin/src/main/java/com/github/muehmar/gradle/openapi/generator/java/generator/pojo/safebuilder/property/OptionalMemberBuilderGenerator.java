package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator.singleBuilderClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.SingleMemberSetterGenerator.singleMemberSetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.SetterBuilder.fullSetterBuilder;
import static io.github.muehmar.codegenerator.Generator.newLine;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.BuilderStage;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.Setter;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.SetterBuilderImpl.SetterType;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.setter.model.SetterMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.ref.OpenApiUtilRefs;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;

public class OptionalMemberBuilderGenerator {

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
          .includeInBuilder(ms -> ms.getMember().isNotNullable())
          .typeFormat("Optional<%s>")
          .addRefs(writer -> writer.ref(JavaRefs.JAVA_UTIL_OPTIONAL))
          .build();
  private static final Setter TRISTATE_SETTER =
      fullSetterBuilder()
          .type(SetterType.DEFAULT)
          .includeInBuilder(ms -> ms.getMember().isNullable())
          .typeFormat("Tristate<%s>")
          .addRefs(writer -> writer.ref(OpenApiUtilRefs.TRISTATE))
          .build();

  private OptionalMemberBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> optionalMemberBuilderGenerator(
      SafeBuilderVariant builderVariant) {
    final Generator<OptionalPropertyBuilderStage, PojoSettings> singleBuilderClassGenerator =
        singleBuilderClassGenerator(
            OptionalPropertyBuilderStage::getName, builderMethodsOfFirstOptionalMemberGenerator());
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            singleBuilderClassGenerator,
            pojo -> OptionalMemberBuilderGenerator.stagesFromPojo(builderVariant, pojo),
            newLine());
  }

  public static Generator<OptionalPropertyBuilderStage, PojoSettings>
      builderMethodsOfFirstOptionalMemberGenerator() {
    return Generator.<OptionalPropertyBuilderStage, PojoSettings>emptyGen()
        .append(
            singleMemberSetterMethods(),
            stage -> new SetterMember(stage.getNextStage(), stage.getMember()));
  }

  private static Generator<SetterMember, PojoSettings> singleMemberSetterMethods() {
    final PList<Setter> setters =
        PList.of(
            NORMAL_SETTER,
            OPTIONAL_SETTER,
            TRISTATE_SETTER,
            NORMAL_SETTER.forType(SetterType.NULLABLE_ITEMS_LIST),
            OPTIONAL_SETTER.forType(SetterType.NULLABLE_ITEMS_LIST),
            TRISTATE_SETTER.forType(SetterType.NULLABLE_ITEMS_LIST));
    return singleMemberSetterGenerator(setters);
  }

  public static Iterable<OptionalPropertyBuilderStage> stagesFromPojo(
      SafeBuilderVariant builderVariant, JavaObjectPojo pojo) {
    return BuilderStage.createStages(builderVariant, pojo)
        .toPList()
        .flatMapOptional(BuilderStage::asOptionalPropertyBuilderStage);
  }
}
