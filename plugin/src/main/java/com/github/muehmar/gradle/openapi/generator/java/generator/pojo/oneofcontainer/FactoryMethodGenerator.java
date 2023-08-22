package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.oneofcontainer;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import ch.bluecare.commons.data.Pair;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

class FactoryMethodGenerator {
  private FactoryMethodGenerator() {}

  public static Generator<OneOfContainer, PojoSettings> factoryMethodGenerator() {
    return Generator.<OneOfContainer, PojoSettings>emptyGen().append(fromMethods());
  }

  private static Generator<OneOfContainer, PojoSettings> fromMethods() {
    return Generator.<OneOfContainer, PojoSettings>emptyGen()
        .appendList(fromFactoryMethod(), OneOfContainerAndPojo::fromOneOfContainer, newLine());
  }

  private static Generator<OneOfContainerAndPojo, PojoSettings> fromFactoryMethod() {
    final MethodGen<OneOfContainerAndPojo, PojoSettings> method =
        MethodGenBuilder.<OneOfContainerAndPojo, PojoSettings>create()
            .modifiers(PUBLIC, STATIC)
            .noGenericTypes()
            .returnType(cap -> cap.getOneOfContainer().getContainerName().asString())
            .methodName(pojos -> String.format("from%s", pojos.pojo.getSchemaName().asIdentifier()))
            .singleArgument(pojos -> String.format("%s dto", pojos.pojo.getClassName()))
            .content(fromMethodContent())
            .build();
    final Generator<OneOfContainerAndPojo, PojoSettings> javaDoc =
        JavaDocGenerator.javaDoc(
            (cap, s) ->
                String.format(
                    "Creates an instance of {@link %s} from a {@link %s}.",
                    cap.getOneOfContainer().getContainerName(), cap.getPojo().getClassName()));
    return javaDoc.append(method);
  }

  private static Generator<OneOfContainerAndPojo, PojoSettings> fromMethodContent() {
    return Generator.<OneOfContainerAndPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println(
                    "return new %s(%s);",
                    p.oneOfContainer.getContainerName(), p.constructorArguments()));
  }

  @Value
  private static class OneOfContainerAndPojo {
    OneOfContainer oneOfContainer;
    JavaObjectPojo pojo;
    int pojoIdx;

    static PList<OneOfContainerAndPojo> fromOneOfContainer(OneOfContainer container) {
      return container
          .getComposition()
          .getPojos()
          .zipWithIndex()
          .map(p -> new OneOfContainerAndPojo(container, p.first(), p.second()))
          .toPList();
    }

    String constructorArguments() {
      return oneOfContainer
          .getComposition()
          .getPojos()
          .toPList()
          .zipWithIndex()
          .map(Pair::second)
          .map(idx -> idx == pojoIdx ? "dto" : "null")
          .mkString(", ");
    }
  }
}
