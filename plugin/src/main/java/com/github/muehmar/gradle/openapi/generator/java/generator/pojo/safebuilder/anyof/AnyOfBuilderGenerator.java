package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.FinalRequiredMemberBuilderGenerator.builderMethods;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredMemberBuilderGenerator.builderMethodsOfFirstRequiredMemberGenerator;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.AnyOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class AnyOfBuilderGenerator {
  private static final JavaName CONTAINER_NAME = JavaName.fromString("AnyOfContainer");

  private AnyOfBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> anyOfBuilderGenerator(
      SafeBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(
            anyOfBuilder(builderVariant),
            pojo -> AnyOfBuilderClass.fromPojo(builderVariant, pojo),
            newLine());
  }

  private static Generator<AnyOfBuilderClass, PojoSettings> anyOfBuilder(
      SafeBuilderVariant builderVariant) {
    return SingleBuilderClassGenerator.singleBuilderClassGenerator(
        AnyOfBuilderClass::builderClassName, dtoSetters(builderVariant));
  }

  private static Generator<AnyOfBuilderClass, PojoSettings> dtoSetters(
      SafeBuilderVariant builderVariant) {
    return Generator.<AnyOfBuilderClass, PojoSettings>emptyGen()
        .appendList(singleAnyOfDtoSetter(), AnyOfBuilderClass::getNameAndPojos, newLine())
        .filter(AnyOfBuilderClass::isNotLastBuilder)
        .appendSingleBlankLine()
        .append(firstPropertySetters(builderVariant))
        .appendSingleBlankLine()
        .append(anyOfContainerSetter(builderVariant));
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
        .singleArgument(m -> new Argument(m.anyOfPojo.getClassName().asString(), "dto"))
        .content(
            (m, s, w) ->
                w.println(
                    "return new %s(builder.%s(dto));",
                    m.nextBuilderClassName(),
                    m.anyOfPojo.prefixedClassNameForMethod(s.getBuilderMethodPrefix())))
        .build();
  }

  private static Generator<AnyOfBuilderClass, PojoSettings> firstPropertySetters(
      SafeBuilderVariant builderVariant) {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .append(
            builderMethodsOfFirstRequiredMemberGenerator(builderVariant)
                .filter(JavaObjectPojo::hasRequiredMembers))
        .append(builderMethods().filter(JavaObjectPojo::hasNotRequiredMembers))
        .contraMap(AnyOfBuilderClass::getPojo)
        .filter(AnyOfBuilderClass::isNotFirstBuilder);
  }

  private static Generator<AnyOfBuilderClass, PojoSettings> anyOfContainerSetter(
      SafeBuilderVariant builderVariant) {
    return MethodGenBuilder.<AnyOfBuilderClass, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(AnyOfBuilderName.last(builderVariant).currentName())
        .methodName(
            (pojo, settings) ->
                CONTAINER_NAME.prefixedMethodName(settings.getBuilderMethodPrefix()).asString())
        .singleArgument(
            pojo -> new Argument(pojo.container.getContainerName().asString(), "container"))
        .content(anyOfContainerSetterContent(builderVariant))
        .build()
        .filter(AnyOfBuilderClass::isFirstBuilder);
  }

  private static Generator<AnyOfBuilderClass, PojoSettings> anyOfContainerSetterContent(
      SafeBuilderVariant builderVariant) {
    return Generator.<AnyOfBuilderClass, PojoSettings>emptyGen()
        .appendList(
            singleContainerPropertySetter(),
            oneOfPojo -> oneOfPojo.getContainer().getComposition().getPojos(),
            newLine())
        .append(
            (p, s, w) ->
                w.println(
                    "return new %s(builder);",
                    AnyOfBuilderName.last(builderVariant).currentName()));
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

    private static PList<AnyOfBuilderClass> fromPojo(
        SafeBuilderVariant builderVariant, JavaObjectPojo parentPojo) {
      return parentPojo
          .getAnyOfContainer()
          .map(
              container ->
                  PList.of(
                          AnyOfBuilderName.first(builderVariant),
                          AnyOfBuilderName.remaining(builderVariant),
                          AnyOfBuilderName.last(builderVariant))
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

    public boolean isFirstBuilder() {
      return anyOfBuilderName.getBuilderType().equals(AnyOfBuilderName.BuilderType.FIRST_BUILDER);
    }

    public boolean isNotFirstBuilder() {
      return not(isFirstBuilder());
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
