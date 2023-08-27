package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.FinalRequiredMemberBuilderGenerator.builderMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredMemberBuilderGenerator.builderMethodsOfFirstRequiredMemberGenerator;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.AnyOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class AnyOfBuilderGenerator {
  private static final JavaName CONTAINER_NAME = JavaName.fromString("AnyOfContainer");

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
        .filter(AnyOfBuilderClass::isNotLastBuilder)
        .appendSingleBlankLine()
        .append(firstPropertySetters())
        .appendSingleBlankLine()
        .append(anyOfContainerSetter());
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
            builderMethodsOfFirstRequiredMemberGenerator()
                .filter(JavaObjectPojo::hasRequiredMembers))
        .append(builderMethods().filter(JavaObjectPojo::hasNotRequiredMembers))
        .contraMap(AnyOfBuilderClass::getPojo)
        .filter(AnyOfBuilderClass::isNotFirstBuilder);
  }

  private static Generator<AnyOfBuilderClass, PojoSettings> anyOfContainerSetter() {
    return MethodGenBuilder.<AnyOfBuilderClass, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(AnyOfBuilderName.last().currentName())
        .methodName(
            (pojo, settings) ->
                CONTAINER_NAME.prefixedMethodeName(settings.getBuilderMethodPrefix()).asString())
        .singleArgument(
            pojo -> String.format("%s container", pojo.container.getContainerName().asString()))
        .content(anyOfContainerSetterContent())
        .build()
        .filter(AnyOfBuilderClass::isFirstBuilder);
  }

  private static Generator<AnyOfBuilderClass, PojoSettings> anyOfContainerSetterContent() {
    return Generator.<AnyOfBuilderClass, PojoSettings>emptyGen()
        .appendList(
            singleContainerPropertySetter(),
            oneOfPojo -> oneOfPojo.getContainer().getComposition().getPojos(),
            newLine())
        .append(
            (p, s, w) ->
                w.println("return new %s(builder);", AnyOfBuilderName.last().currentName()));
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
  private static class AnyOfBuilderClass {
    AnyOfBuilderName anyOfBuilderName;
    AnyOfContainer container;
    JavaObjectPojo pojo;

    private static PList<AnyOfBuilderClass> fromPojo(JavaObjectPojo parentPojo) {
      return parentPojo
          .getAnyOfContainer()
          .map(
              container ->
                  PList.of(
                          AnyOfBuilderName.first(),
                          AnyOfBuilderName.remaining(),
                          AnyOfBuilderName.last())
                      .map(
                          builderName -> new AnyOfBuilderClass(builderName, container, parentPojo)))
          .orElseGet(PList::empty);
    }

    public PList<AnyOfPojoAndBuilderName> getNameAndPojos() {
      return container
          .getComposition()
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

    public boolean isFirstBuilder() {
      return anyOfBuilderName.getBuilderType().equals(AnyOfBuilderName.BuilderType.FIRST_BUILDER);
    }

    public boolean isNotFirstBuilder() {
      return not(isFirstBuilder());
    }

    public boolean isRemainingBuilder() {
      return anyOfBuilderName
          .getBuilderType()
          .equals(AnyOfBuilderName.BuilderType.REMAINING_BUILDER);
    }

    public boolean isLastBuilder() {
      return anyOfBuilderName.getBuilderType().equals(AnyOfBuilderName.BuilderType.LAST_BUILDER);
    }

    public boolean isNotLastBuilder() {
      return not(isLastBuilder());
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
