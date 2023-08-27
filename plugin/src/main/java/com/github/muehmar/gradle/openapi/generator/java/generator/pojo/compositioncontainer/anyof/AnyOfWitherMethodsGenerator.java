package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.anyof;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.AnyOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class AnyOfWitherMethodsGenerator {
  private AnyOfWitherMethodsGenerator() {}

  public static Generator<AnyOfContainer, PojoSettings> anyOfWitherMethodsGenerator() {
    return Generator.<AnyOfContainer, PojoSettings>emptyGen()
        .appendList(witherMethod(), ContainerAndPojo::fromAnyOfContainer, newLine());
  }

  private static Generator<ContainerAndPojo, PojoSettings> witherMethod() {
    final MethodGen<ContainerAndPojo, PojoSettings> method =
        MethodGenBuilder.<ContainerAndPojo, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(cap -> cap.getContainerName().asString())
            .methodName(pojos -> String.format("with%s", pojos.pojo.getSchemaName().asIdentifier()))
            .singleArgument(
                pojos ->
                    String.format(
                        "%s %s",
                        pojos.pojo.getClassName(), pojos.pojo.getClassName().startLowercase()))
            .content(fromMethodContent())
            .build();
    final Generator<ContainerAndPojo, PojoSettings> javaDoc =
        JavaDocGenerator.javaDoc(
            (cap, s) ->
                String.format(
                    "Creates a new instance of {@link %s} additionally populated with {@link %s}.",
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
      return containerPojos.map(pojo -> pojo.getClassName().startLowercase()).mkString(", ");
    }
  }
}
