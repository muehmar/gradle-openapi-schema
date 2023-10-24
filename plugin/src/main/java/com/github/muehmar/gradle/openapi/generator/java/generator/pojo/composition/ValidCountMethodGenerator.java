package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.AnyOf.getAnyOfValidCountMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.OneOf.getOneOfValidCountMethodName;
import static io.github.muehmar.codegenerator.java.JavaModifier.PRIVATE;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

public class ValidCountMethodGenerator {
  private ValidCountMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> validCountMethodGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(validOneOfCountMethod(), SingleMemberPojo::fromOneOf)
        .appendSingleBlankLine()
        .appendOptional(validAnyOfCountMethod(), SingleMemberPojo::fromAnyOf);
  }

  private static Generator<NonEmptyList<SingleMemberPojo>, PojoSettings> validAnyOfCountMethod() {
    return MethodGenBuilder.<NonEmptyList<SingleMemberPojo>, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("int")
        .methodName(getAnyOfValidCountMethodName().asString())
        .noArguments()
        .content(validCountMethodContent())
        .build();
  }

  private static Generator<NonEmptyList<SingleMemberPojo>, PojoSettings> validOneOfCountMethod() {
    return MethodGenBuilder.<NonEmptyList<SingleMemberPojo>, PojoSettings>create()
        .modifiers(PRIVATE)
        .noGenericTypes()
        .returnType("int")
        .methodName(getOneOfValidCountMethodName().asString())
        .noArguments()
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

    private static Optional<NonEmptyList<SingleMemberPojo>> fromOneOf(JavaObjectPojo composedPojo) {
      return from(
          composedPojo, pojo -> pojo.getOneOfComposition().map(JavaOneOfComposition::getPojos));
    }

    private static Optional<NonEmptyList<SingleMemberPojo>> fromAnyOf(JavaObjectPojo composedPojo) {
      return from(
          composedPojo, pojo -> pojo.getAnyOfComposition().map(JavaAnyOfComposition::getPojos));
    }

    private static Optional<NonEmptyList<SingleMemberPojo>> from(
        JavaObjectPojo javaObjectPojo,
        Function<JavaObjectPojo, Optional<NonEmptyList<JavaObjectPojo>>> getMembers) {
      return getMembers
          .apply(javaObjectPojo)
          .map(
              members ->
                  members
                      .zipWithIndex()
                      .map(p -> new SingleMemberPojo(p.first(), members.size() - 1 == p.second())))
          .flatMap(NonEmptyList::fromIter);
    }

    private String validCountLine() {
      return String.format(
          "(%s() ? 1 : 0)%s",
          MethodNames.Composition.isValidAgainstMethodName(memberPojo), isLast() ? ";" : " +");
    }
  }
}
