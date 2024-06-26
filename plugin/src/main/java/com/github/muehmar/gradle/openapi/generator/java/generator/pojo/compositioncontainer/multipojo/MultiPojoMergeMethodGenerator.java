package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.multipojo;

import static com.github.muehmar.gradle.openapi.util.Functions.firstAndTail;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.NonEmptyList;
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

public class MultiPojoMergeMethodGenerator {
  private MultiPojoMergeMethodGenerator() {}

  public static Generator<MultiPojoContainer, PojoSettings> multiPojoMergeMethodGenerator() {
    final MethodGen<MultiPojoContainer, PojoSettings> method =
        MethodGenBuilder.<MultiPojoContainer, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(container -> container.getContainerName().asString())
            .methodName("merge")
            .singleArgument(
                container -> new Argument(container.getContainerName().asString(), "other"))
            .doesNotThrow()
            .content(methodContent())
            .build();
    return javaDoc().append(method);
  }

  private static Generator<MultiPojoContainer, PojoSettings> javaDoc() {
    return JavaDocGenerator.javaDoc(
        (c, s) ->
            String.format(
                "Merges another instance of {@link %s} with this instance "
                    + "by accumulating all objects both container contains. ",
                c.getContainerName()));
  }

  private static Generator<MultiPojoContainer, PojoSettings> methodContent() {
    return Generator.<MultiPojoContainer, PojoSettings>emptyGen()
        .append((c, s, w) -> w.println("return new %s(", c.getContainerName()))
        .appendList(singleMemberSelection().indent(1), AnyOfPojo::fromContainer)
        .append(constant(");"));
  }

  private static Generator<AnyOfPojo, PojoSettings> singleMemberSelection() {
    return Generator.<AnyOfPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) ->
                w.println(
                    "%s != null ? %s : other.%s%s",
                    p.memberName(), p.memberName(), p.memberName(), p.commaOrNothing()));
  }

  @Value
  private static class AnyOfPojo {
    JavaObjectPojo pojo;
    boolean isLast;

    JavaName memberName() {
      return pojo.getClassName().startLowerCase();
    }

    String commaOrNothing() {
      return isLast ? "" : ",";
    }

    public static NonEmptyList<AnyOfPojo> fromContainer(MultiPojoContainer container) {
      return container
          .getComposition()
          .getPojos()
          .reverse()
          .zipWithIndex()
          .map(firstAndTail(p -> new AnyOfPojo(p, true), p -> new AnyOfPojo(p, false)))
          .reverse();
    }
  }
}
