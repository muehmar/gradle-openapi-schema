package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

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

public class FoldMethodGenerator {
  private FoldMethodGenerator() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    return fullFoldMethod().appendSingleBlankLine().append(standardFoldMethod());
  }

  private static Generator<JavaComposedPojo, PojoSettings> fullFoldMethod() {
    return MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("T")
        .methodName("fold")
        .arguments(FoldMethodGenerator::fullFoldMethodArguments)
        .content(fullFoldMethodContent())
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_FUNCTION))
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_SUPPLIER))
        .filter(JavaComposedPojo::isOneOf);
  }

  private static Generator<JavaComposedPojo, PojoSettings> standardFoldMethod() {
    return MethodGenBuilder.<JavaComposedPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType(FoldMethodGenerator::standardFoldMethodReturnType)
        .methodName("fold")
        .arguments(FoldMethodGenerator::standardFoldMethodArguments)
        .content(standardOneOfFoldMethodContent().append(standardAnyOfFoldMethodContent()))
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_FUNCTION));
  }

  private static String standardFoldMethodReturnType(JavaComposedPojo composedPojo) {
    return composedPojo.isAnyOf() ? "List<T>" : "T";
  }

  private static Generator<JavaComposedPojo, PojoSettings> fullFoldMethodContent() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .appendList(oneOfFoldConditionAndContent(), ComposedAndMemberPojo::fromComposedPojo)
        .append(constant("else {"))
        .append(constant("return onInvalid.get();"), 1)
        .append(constant("}"));
  }

  private static Generator<JavaComposedPojo, PojoSettings> standardOneOfFoldMethodContent() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .append(constant("return fold("))
        .appendList(
            (p, s, w) -> w.tab(1).println("on%s,", p.getName()), JavaComposedPojo::getJavaPojos)
        .append(
            (p, s, w) ->
                w.println(
                    "() -> {throw new IllegalStateException(\"%s\");}", getOnInvalidMessage(p)),
            1)
        .append(constant(");"))
        .filter(JavaComposedPojo::isOneOf);
  }

  private static Generator<JavaComposedPojo, PojoSettings> standardAnyOfFoldMethodContent() {
    return Generator.<JavaComposedPojo, PojoSettings>emptyGen()
        .append(constant("final List<T> result = new ArrayList<>();"))
        .appendList(singleAnyOfFold(), ComposedAndMemberPojo::fromComposedPojo)
        .append(constant("return result;"))
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_LIST))
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_ARRAY_LIST))
        .filter(JavaComposedPojo::isAnyOf);
  }

  private static String getOnInvalidMessage(JavaComposedPojo composedPojo) {
    return String.format(
        "Unable to fold %s: Not valid against one of the schemas [%s].",
        composedPojo.getName(), composedPojo.getJavaPojos().map(JavaPojo::getName).mkString(", "));
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> singleAnyOfFold() {
    return Generator.<ComposedAndMemberPojo, PojoSettings>emptyGen()
        .append(
            (p, s, w) -> w.println("if (isValidAgainst%s()) {", p.memberPojo.getName().getName()))
        .append(
            (p, s, w) ->
                w.println(
                    "result.add(on%s.apply(as%s()));",
                    p.memberPojo.getName(), p.memberPojo.getName()),
            1)
        .append(constant("}"));
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> oneOfFoldConditionAndContent() {
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
    return standardFoldMethodArguments(composedPojo).add("Supplier<T> onInvalid");
  }

  private static PList<String> standardFoldMethodArguments(JavaComposedPojo composedPojo) {
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
