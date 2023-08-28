package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.anyof;

import static com.github.muehmar.gradle.openapi.util.Functions.firstAndTail;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.AnyOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class AnyOfMergeMethodGenerator {
  private AnyOfMergeMethodGenerator() {}

  public static Generator<AnyOfContainer, PojoSettings> anyOfMergeMethodGenerator() {
    final MethodGen<AnyOfContainer, PojoSettings> method =
        MethodGenBuilder.<AnyOfContainer, PojoSettings>create()
            .modifiers(PUBLIC)
            .noGenericTypes()
            .returnType(container -> container.getContainerName().asString())
            .methodName("merge")
            .singleArgument(container -> String.format("%s other", container.getContainerName()))
            .content(methodContent())
            .build();
    return javaDoc().append(method);
  }

  private static Generator<AnyOfContainer, PojoSettings> javaDoc() {
    return JavaDocGenerator.javaDoc(
        (c, s) ->
            String.format(
                "Merges another instance of {@link %s} with this instance "
                    + "by accumulating all objects both container contains. ",
                c.getContainerName()));
  }

  private static Generator<AnyOfContainer, PojoSettings> methodContent() {
    return Generator.<AnyOfContainer, PojoSettings>emptyGen()
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

    JavaIdentifier memberName() {
      return pojo.getClassName().startLowercase();
    }

    String commaOrNothing() {
      return isLast ? "" : ",";
    }

    public static NonEmptyList<AnyOfPojo> fromContainer(AnyOfContainer container) {
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
