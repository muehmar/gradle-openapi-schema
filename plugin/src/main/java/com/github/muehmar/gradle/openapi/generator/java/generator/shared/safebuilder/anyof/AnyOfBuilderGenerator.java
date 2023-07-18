package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.anyof;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.SingleBuilderClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.property.FinalRequiredMemberBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder.property.RequiredMemberBuilderGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class AnyOfBuilderGenerator {
  private AnyOfBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> anyOfBuilderGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(anyOfBuilder(), AnyOfBuilderClass::fromPojo, newLine());
  }

  private static Generator<AnyOfBuilderClass, PojoSettings> anyOfBuilder() {
    return SingleBuilderClassGenerator.singleBuilderClassGenerator(
        AnyOfBuilderClass::builderClassName, dtoSetters());
  }

  private static Generator<AnyOfBuilderClass, PojoSettings> dtoSetters() {
    return Generator.<AnyOfBuilderClass, PojoSettings>emptyGen()
        .appendList(singleAnyOfDtoSetter(), AnyOfBuilderClass::getNameAndPojos, newLine())
        .appendSingleBlankLine()
        .append(firstPropertySetters());
  }

  private static Generator<AnyOfPojoAndBuilderName, PojoSettings> singleAnyOfDtoSetter() {
    return MethodGenBuilder.<AnyOfPojoAndBuilderName, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(AnyOfPojoAndBuilderName::nextBuilderClassName)
        .methodName(
            (anyOfPojoAndBuilderName, settings) ->
                anyOfPojoAndBuilderName
                    .anyOfPojo
                    .prefixedClassNameForMethod(settings.getBuilderMethodPrefix())
                    .asString())
        .singleArgument(m -> String.format("%s dto", m.anyOfPojo.getClassName()))
        .content(
            (m, s, w) ->
                w.println(
                    "return new %s(builder.%s(dto));",
                    m.nextBuilderClassName(),
                    m.anyOfPojo.prefixedClassNameForMethod(s.getBuilderMethodPrefix())))
        .build();
  }

  private static Generator<AnyOfBuilderClass, PojoSettings> firstPropertySetters() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            RequiredMemberBuilderGenerator.builderMethodsOfFirstRequiredMemberGenerator()
                .filter(JavaObjectPojo::hasRequiredMembers))
        .append(
            FinalRequiredMemberBuilderGenerator.builderMethods()
                .filter(JavaObjectPojo::hasNotRequiredMembers))
        .contraMap(AnyOfBuilderClass::getPojo)
        .filter(AnyOfBuilderClass::isRemainingBuilder);
  }

  @Value
  private static class AnyOfBuilderClass {
    AnyOfBuilderName anyOfBuilderName;
    JavaAnyOfComposition anyOfComposition;
    JavaObjectPojo pojo;

    private static PList<AnyOfBuilderClass> fromPojo(JavaObjectPojo parentPojo) {
      return parentPojo
          .getAnyOfComposition()
          .map(
              anyOfComposition ->
                  PList.of(
                          AnyOfBuilderName.first(parentPojo),
                          AnyOfBuilderName.remaining(parentPojo))
                      .map(
                          builderName ->
                              new AnyOfBuilderClass(builderName, anyOfComposition, parentPojo)))
          .orElseGet(PList::empty);
    }

    public PList<AnyOfPojoAndBuilderName> getNameAndPojos() {
      return anyOfComposition
          .getPojos()
          .map(anyOfPojo -> new AnyOfPojoAndBuilderName(anyOfBuilderName, anyOfPojo))
          .toPList();
    }

    public String builderClassName() {
      return anyOfBuilderName.currentName();
    }

    public JavaObjectPojo getPojo() {
      return pojo;
    }

    public boolean isRemainingBuilder() {
      return anyOfBuilderName
          .getBuilderType()
          .equals(AnyOfBuilderName.BuilderType.REMAINING_BUILDER);
    }
  }

  @Value
  private static class AnyOfPojoAndBuilderName {
    AnyOfBuilderName builderName;
    JavaObjectPojo anyOfPojo;

    String nextBuilderClassName() {
      return builderName.getNextBuilderName().currentName();
    }
  }
}
