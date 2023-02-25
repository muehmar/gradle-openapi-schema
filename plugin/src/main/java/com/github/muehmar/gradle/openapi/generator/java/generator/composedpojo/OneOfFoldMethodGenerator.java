package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo.CompositionType.ONE_OF;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class OneOfFoldMethodGenerator {
  private OneOfFoldMethodGenerator() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    return fullFoldMethod()
        .appendSingleBlankLine()
        .append(throwingFoldMethod())
        .filter(p -> p.getCompositionType().equals(ONE_OF));
  }

  private static Generator<JavaComposedPojo, PojoSettings> fullFoldMethod() {
    return MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("T")
        .methodName("fold")
        .arguments(OneOfFoldMethodGenerator::fullFoldMethodArguments) // FIX for validation
        .content(fullFoldMethodContent())
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_FUNCTION))
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_SUPPLIER));
  }

  private static Generator<JavaComposedPojo, PojoSettings> throwingFoldMethod() {
    return MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("T")
        .methodName("fold")
        .arguments(OneOfFoldMethodGenerator::throwingFoldMethodArguments) // FIX for validation
        .content(throwingFoldMethodContent())
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_FUNCTION));
  }

  private static Generator<JavaComposedPojo, PojoSettings> fullFoldMethodContent() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .appendList(foldConditionAndContent(), ComposedAndMemberPojo::fromComposedPojo)
        .append(constant("else {"))
        .append(constant("return onInvalid.get();"), 1)
        .append(constant("}"));
  }

  private static Generator<JavaComposedPojo, PojoSettings> throwingFoldMethodContent() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .append(constant("return fold("))
        .appendList(
            (p, s, w) -> w.tab(1).println("on%s,", p.getName()), JavaComposedPojo::getJavaPojos)
        .append(
            (p, s, w) ->
                w.println(
                    "() -> {throw new IllegalStateException(\"%s\");}", getOnInvalidMessage(p)),
            1)
        .append(constant(");"));
  }

  private static String getOnInvalidMessage(JavaComposedPojo composedPojo) {
    return String.format(
        "Unable to fold %s: Not valid against one of the schemas [%s].",
        composedPojo.getName(), composedPojo.getJavaPojos().map(JavaPojo::getName).mkString(", "));
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> foldConditionAndContent() {
    return Generator.<ComposedAndMemberPojo, PojoSettings>emptyGen()
        .append(
            (pojo, s, w) ->
                w.println(
                    "%s (%sisValidAgainst%s()) {",
                    pojo.ifOrElseIf(),
                    pojo.discriminatorCondition(),
                    pojo.getMemberPojo().getName().getName()))
        .append(
            (p, s, w) ->
                w.tab(1)
                    .println(
                        "return on%s.apply(as%s());",
                        p.memberPojo.getName(), p.memberPojo.getName()))
        .append(constant("}"));
  }

  private static PList<String> fullFoldMethodArguments(JavaComposedPojo composedPojo) {
    return throwingFoldMethodArguments(composedPojo).add("Supplier<T> onInvalid");
  }

  private static PList<String> throwingFoldMethodArguments(JavaComposedPojo composedPojo) {
    return composedPojo
        .getJavaPojos()
        .map(JavaPojo::getName)
        .map(name -> String.format("Function<%s, T> on%s", name, name));
  }

  @Value
  private static class ComposedAndMemberPojo {
    JavaComposedPojo composedPojo;
    JavaPojo memberPojo;

    private static PList<ComposedAndMemberPojo> fromComposedPojo(JavaComposedPojo composedPojo) {
      return composedPojo.getJavaPojos().map(pojo -> new ComposedAndMemberPojo(composedPojo, pojo));
    }

    private String discriminatorCondition() {
      return composedPojo
          .getDiscriminator()
          .map(
              discriminator ->
                  String.format(
                      "\"%s\".equals(%s) && ",
                      discriminator.getValueForPojoName(memberPojo.getName()),
                      discriminator.getPropertyName()))
          .orElse("");
    }

    private String ifOrElseIf() {
      return composedPojo
          .getJavaPojos()
          .headOption()
          .filter(memberPojo::equals)
          .map(ignore -> "if")
          .orElse("else if");
    }
  }
}
