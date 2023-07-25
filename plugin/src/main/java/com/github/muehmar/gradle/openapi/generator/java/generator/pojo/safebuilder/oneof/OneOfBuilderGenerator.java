package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SingleBuilderClassGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class OneOfBuilderGenerator {
  private OneOfBuilderGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> oneOfBuilderGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(oneOfBuilder(), OneOfPojo::fromObjectPojo);
  }

  private static Generator<NonEmptyList<OneOfPojo>, PojoSettings> oneOfBuilder() {
    return SingleBuilderClassGenerator.singleBuilderClassGenerator(
        pojos -> pojos.head().builderClassName(), dtoSetters());
  }

  private static Generator<NonEmptyList<OneOfPojo>, PojoSettings> dtoSetters() {
    return Generator.<NonEmptyList<OneOfPojo>, PojoSettings>emptyGen()
        .appendList(singleOneOfPojoSetter(), list -> list, newLine());
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
    JavaObjectPojo oneOfPojo;

    private static Optional<NonEmptyList<OneOfPojo>> fromObjectPojo(JavaObjectPojo parentPojo) {
      return parentPojo
          .getOneOfComposition()
          .map(oneOfComposition -> fromParentPojoAndOneOfComposition(parentPojo, oneOfComposition));
    }

    private static NonEmptyList<OneOfPojo> fromParentPojoAndOneOfComposition(
        JavaObjectPojo parentPojo, JavaOneOfComposition oneOfComposition) {
      return oneOfComposition
          .getPojos()
          .map(
              oneOfPojo ->
                  new OneOfPojo(
                      OneOfBuilderName.of(parentPojo, oneOfComposition, oneOfPojo, 0), oneOfPojo));
    }

    public String builderClassName() {
      return oneOfBuilderName.currentName();
    }

    public String nextPojoBuilderClassName() {
      return oneOfBuilderName.getNextPojoBuilderName().currentName();
    }
  }
}
