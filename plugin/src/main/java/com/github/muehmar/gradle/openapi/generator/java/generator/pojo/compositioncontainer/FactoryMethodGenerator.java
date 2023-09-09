package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import ch.bluecare.commons.data.Pair;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.AnyOfContainer;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class FactoryMethodGenerator {
  private FactoryMethodGenerator() {}

  public static Generator<OneOfContainer, PojoSettings> oneOfFromFactoryMethods() {
    return Generator.<OneOfContainer, PojoSettings>emptyGen()
        .appendList(fromFactoryMethod(), ContainerAndPojo::fromOneOfContainer, newLine());
  }

  public static Generator<AnyOfContainer, PojoSettings> anyOfFromFactoryMethods() {
    return Generator.<AnyOfContainer, PojoSettings>emptyGen()
        .appendList(fromFactoryMethod(), ContainerAndPojo::fromAnyOfContainer, newLine());
  }

  private static Generator<ContainerAndPojo, PojoSettings> fromFactoryMethod() {
    final MethodGen<ContainerAndPojo, PojoSettings> method =
        MethodGenBuilder.<ContainerAndPojo, PojoSettings>create()
            .modifiers(PUBLIC, STATIC)
            .noGenericTypes()
            .returnType(cap -> cap.getContainerName().asString())
            .methodName(pojos -> String.format("from%s", pojos.pojo.getSchemaName().asIdentifier()))
            .singleArgument(pojos -> new Argument(pojos.pojo.getClassName().asString(), "dto"))
            .content(fromMethodContent())
            .build();
    final Generator<ContainerAndPojo, PojoSettings> javaDoc =
        JavaDocGenerator.javaDoc(
            (cap, s) ->
                String.format(
                    "Creates an instance of {@link %s} from a {@link %s}.",
                    cap.getContainerName(), cap.getPojo().getClassName()));
    return javaDoc.append(method);
  }

  private static Generator<ContainerAndPojo, PojoSettings> fromMethodContent() {
    return Generator.<ContainerAndPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println("return new %s(%s);", p.getContainerName(), p.constructorArguments()));
  }

  @Value
  private static class ContainerAndPojo {
    JavaIdentifier containerName;
    PList<JavaObjectPojo> containerPojos;
    JavaObjectPojo pojo;
    int pojoIdx;

    static PList<ContainerAndPojo> fromOneOfContainer(OneOfContainer container) {
      return container
          .getComposition()
          .getPojos()
          .zipWithIndex()
          .map(
              p ->
                  new ContainerAndPojo(
                      container.getContainerName(),
                      container.getComposition().getPojos().toPList(),
                      p.first(),
                      p.second()))
          .toPList();
    }

    static PList<ContainerAndPojo> fromAnyOfContainer(AnyOfContainer container) {
      return container
          .getComposition()
          .getPojos()
          .zipWithIndex()
          .map(
              p ->
                  new ContainerAndPojo(
                      container.getContainerName(),
                      container.getComposition().getPojos().toPList(),
                      p.first(),
                      p.second()))
          .toPList();
    }

    String constructorArguments() {
      return containerPojos
          .zipWithIndex()
          .map(Pair::second)
          .map(idx -> idx == pojoIdx ? "dto" : "null")
          .mkString(", ");
    }
  }
}
