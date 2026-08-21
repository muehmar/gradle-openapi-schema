package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.multipojo;

import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliary.MultiPojoContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class MultiPojoWitherMethodsGenerator {
  private MultiPojoWitherMethodsGenerator() {}

  public static Generator<MultiPojoContainer, PojoSettings> multiPojoWitherMethodsGenerator() {
    return Generator.<MultiPojoContainer, PojoSettings>emptyGen()
        .appendList(witherMethod(), ContainerAndPojo::fromMultiPojoContainer, newLine());
  }

  private static Generator<ContainerAndPojo, PojoSettings> witherMethod() {
    final MethodGen<ContainerAndPojo, PojoSettings> method =
        MethodGenBuilder.<ContainerAndPojo, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(cap -> cap.getContainerName().asString())
            .methodName(pojos -> String.format("with%s", pojos.pojo.getSchemaName()))
            .singleArgument(
                pojos ->
                    new Argument(
                        pojos.pojo.getClassName().asString(),
                        pojos.pojo.getClassName().startLowerCase().asString()))
            .doesNotThrow()
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
    JavaName containerName;
    PList<JavaObjectPojo> containerPojos;
    JavaObjectPojo pojo;
    int pojoIdx;

    static PList<ContainerAndPojo> fromMultiPojoContainer(MultiPojoContainer container) {
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
      return containerPojos.map(p -> p.getClassName().startLowerCase()).mkString(", ");
    }
  }
}
