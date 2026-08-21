package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.getCompositionValidCountMethodName;
import static io.github.muehmar.codegenerator.Generator.newLine;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class ValidCountMethodGenerator {
  private ValidCountMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> validCountMethodGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(validCountMethod(), SingleMemberPojo::fromObjectPojo, newLine());
  }

  private static Generator<NonEmptyList<SingleMemberPojo>, PojoSettings> validCountMethod() {
    return MethodGenBuilder.<NonEmptyList<SingleMemberPojo>, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("int")
        .methodName(
            members ->
                getCompositionValidCountMethodName(members.head().getCompositionType()).asString())
        .noArguments()
        .doesNotThrow()
        .content(validCountMethodContent())
        .build();
  }

  private static Generator<NonEmptyList<SingleMemberPojo>, PojoSettings> validCountMethodContent() {
    final Generator<SingleMemberPojo, PojoSettings> composedMemberPojoGen =
        (mp, s, w) -> w.println(mp.validCountLine());
    return Generator.<NonEmptyList<SingleMemberPojo>, PojoSettings>ofWriterFunction(
            w -> w.println("return"))
        .appendList(composedMemberPojoGen.indent(1), pojos -> pojos);
  }

  @Value
  private static class SingleMemberPojo {
    JavaPojo memberPojo;
    boolean isLast;
    DiscriminatableJavaComposition.Type compositionType;

    private static PList<NonEmptyList<SingleMemberPojo>> fromObjectPojo(
        JavaObjectPojo composedPojo) {
      return composedPojo.getDiscriminatableCompositions().map(SingleMemberPojo::fromComposition);
    }

    private static NonEmptyList<SingleMemberPojo> fromComposition(
        DiscriminatableJavaComposition composition) {
      return composition
          .getPojos()
          .zipWithIndex()
          .map(
              p ->
                  new SingleMemberPojo(
                      p.first(),
                      composition.getPojos().size() - 1 == p.second(),
                      composition.getType()));
    }

    private String validCountLine() {
      return String.format(
          "(%s() ? 1 : 0)%s",
          MethodNames.Composition.isValidAgainstMethodName(memberPojo), isLast() ? ";" : " +");
    }
  }
}
