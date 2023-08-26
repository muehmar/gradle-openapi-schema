package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator.singleBuilderClassGenerator;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class OneOfBuilderGenerator {
  private static final JavaName CONTAINER_NAME = JavaName.fromString("OneOfContainer");

  private OneOfBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> oneOfBuilderGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(oneOfBuilder(), OneOfPojo::fromObjectPojo);
  }

  private static Generator<NonEmptyList<OneOfPojo>, PojoSettings> oneOfBuilder() {
    return singleBuilderClassGenerator(pojos -> pojos.head().builderClassName(), setters());
  }

  private static Generator<NonEmptyList<OneOfPojo>, PojoSettings> setters() {
    return Generator.<NonEmptyList<OneOfPojo>, PojoSettings>emptyGen()
        .appendList(singleOneOfPojoSetter(), list -> list, newLine())
        .appendSingleBlankLine()
        .append(oneOfContainerSetter(), NonEmptyList::head);
  }

  private static Generator<OneOfPojo, PojoSettings> oneOfContainerSetter() {
    return MethodGenBuilder.<OneOfPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(OneOfPojo::nextPojoBuilderClassName)
        .methodName(
            (pojo, settings) ->
                CONTAINER_NAME.prefixedMethodeName(settings.getBuilderMethodPrefix()).asString())
        .singleArgument(
            pojo -> String.format("%s container", pojo.container.getContainerName().asString()))
        .content(oneOfContainerSetterContent())
        .build();
  }

  private static Generator<OneOfPojo, PojoSettings> oneOfContainerSetterContent() {
    return Generator.<OneOfPojo, PojoSettings>emptyGen()
        .appendList(
            singleContainerPropertySetter(),
            oneOfPojo -> oneOfPojo.getContainer().getComposition().getPojos(),
            newLine())
        .append((p, s, w) -> w.println("return new %s(builder);", p.nextPojoBuilderClassName()));
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

  private static Generator<OneOfPojo, PojoSettings> singleOneOfPojoSetter() {
    return MethodGenBuilder.<OneOfPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .noGenericTypes()
        .returnType(OneOfPojo::nextPojoBuilderClassName)
        .methodName(
            (oneOfPojo, settings) ->
                oneOfPojo
                    .oneOfPojo
                    .prefixedClassNameForMethod(settings.getBuilderMethodPrefix())
                    .asString())
        .singleArgument(m -> String.format("%s dto", m.oneOfPojo.getClassName()))
        .content(
            (m, s, w) ->
                w.println(
                    "return new %s(builder.%s(dto));",
                    m.nextPojoBuilderClassName(),
                    m.oneOfPojo.prefixedClassNameForMethod(s.getBuilderMethodPrefix())))
        .build();
  }

  @Value
  private static class OneOfPojo {
    OneOfBuilderName oneOfBuilderName;
    OneOfContainer container;
    JavaObjectPojo oneOfPojo;

    private static Optional<NonEmptyList<OneOfPojo>> fromObjectPojo(JavaObjectPojo parentPojo) {
      return parentPojo
          .getOneOfContainer()
          .map(container -> fromParentPojoAndOneOfContainer(parentPojo, container));
    }

    private static NonEmptyList<OneOfPojo> fromParentPojoAndOneOfContainer(
        JavaObjectPojo parentPojo, OneOfContainer oneOfContainer) {
      return oneOfContainer
          .getComposition()
          .getPojos()
          .map(
              oneOfPojo ->
                  new OneOfPojo(
                      OneOfBuilderName.of(
                          parentPojo, oneOfContainer.getComposition(), oneOfPojo, 0),
                      oneOfContainer,
                      oneOfPojo));
    }

    public String builderClassName() {
      return oneOfBuilderName.currentName();
    }

    public String nextPojoBuilderClassName() {
      return oneOfBuilderName.getNextPojoBuilderName().currentName();
    }
  }
}
