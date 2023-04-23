package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import lombok.Value;

public class FoldMethodGenerator {

  private static final String JAVA_DOC_ONE_OF_FOLD =
      "Folds this instance using the given mapping functions for the DTO's. If this instance is valid against exactly"
          + " one of the specified schemas, its corresponding mapping function gets executed with the DTO as input and"
          + " its result is returned.<br><br>\n\n";

  private static final String JAVA_DOC_ANY_OF_FOLD =
      "Folds this instance using the given mapping functions for the DTO's. All mapping functions gets executed with its"
          + " corresponding DTO as input if this instance is valid against the corresponding schema and the results"
          + " are returned in a list. The order of the elements in the returned list is deterministic: The order"
          + " corresponds to the order of the mapping function arguments, i.e. the result of the first mapping function"
          + " will always be at the first position in the list (if the function gets executed).<br><br>\n\n";

  private static final String JAVA_DOC_EXAMPLE =
      "I.e. if the JSON was valid against the schema '%s', the mapping method {@code %s} "
          + "gets executed with the {@link %s} as argument.<br><br>\n\n";

  private static final String JAVA_DOC_ONE_OF_THROWS =
      "This method assumes this instance is either manually or automatically validated, i.e. "
          + "the JSON is valid against exactly one of the schemas. If it is either valid against no schema or multiple schemas, "
          + "it will throw an {@link IllegalStateException}.";

  private static final String JAVA_DOC_ANY_OF_INVALID =
      "This method assumes this instance is either manually or automatically validated, i.e. "
          + "the JSON is valid against at least one of the schemas. If it is valid against no schema, "
          + "it will simply return an empty list.";

  private static final String JAVA_DOC_ONE_OF_FULL_FOLD =
      "Unlike %s, this method accepts as last parameter a {@link Supplier}"
          + " which gets called in case this instance is not valid against exactly one of the defined schemas and"
          + " its value is returned.";

  private FoldMethodGenerator() {}

  public static Generator<JavaComposedPojo, PojoSettings> generator() {
    return fullFoldOneOfJavaDoc()
        .append(fullFoldMethod())
        .appendSingleBlankLine()
        .append(standardFoldAnyOfJavaDoc())
        .append(standardFoldOneOfJavaDoc())
        .append(standardFoldMethod());
  }

  public static Generator<JavaComposedPojo, PojoSettings> standardFoldAnyOfJavaDoc() {
    return JavaDocGenerator.<JavaComposedPojo, PojoSettings>javaDoc(
            (p, s) -> JAVA_DOC_ANY_OF_FOLD + getJavaDocExample(p) + JAVA_DOC_ANY_OF_INVALID)
        .filter(JavaComposedPojo::isAnyOf);
  }

  public static Generator<JavaComposedPojo, PojoSettings> standardFoldOneOfJavaDoc() {
    return JavaDocGenerator.<JavaComposedPojo, PojoSettings>javaDoc(
            (p, s) -> JAVA_DOC_ONE_OF_FOLD + getJavaDocExample(p) + JAVA_DOC_ONE_OF_THROWS)
        .filter(JavaComposedPojo::isOneOf);
  }

  public static Generator<JavaComposedPojo, PojoSettings> fullFoldOneOfJavaDoc() {
    return JavaDocGenerator.<JavaComposedPojo, PojoSettings>javaDoc(
            (p, s) -> JAVA_DOC_ONE_OF_FOLD + getJavaDocExample(p) + getJavaDocFullFoldString(p))
        .filter(JavaComposedPojo::isOneOf);
  }

  private static String getJavaDocExample(JavaComposedPojo pojo) {
    return pojo.getJavaPojos()
        .headOption()
        .map(
            ep ->
                String.format(
                    JAVA_DOC_EXAMPLE,
                    ep.getSchemaName(),
                    CompositionNames.dtoMappingArgumentName(ep),
                    ep.getClassName()))
        .orElse("");
  }

  private static String getJavaDocFullFoldString(JavaComposedPojo pojo) {
    final String unsafeFoldRef =
        String.format(
            "{@link %s#fold(%s)}",
            pojo.getClassName(), pojo.getJavaPojos().map(ignore -> "Function").mkString(", "));
    return String.format(JAVA_DOC_ONE_OF_FULL_FOLD, unsafeFoldRef);
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
            (p, s, w) -> w.tab(1).println("%s,", CompositionNames.dtoMappingArgumentName(p)),
            JavaComposedPojo::getJavaPojos)
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
        composedPojo.getClassName(),
        composedPojo.getJavaPojos().map(JavaPojo::getClassName).mkString(", "));
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> singleAnyOfFold() {
    return Generator.<ComposedAndMemberPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("if (%s()) {", p.isValidAgainstMethodName()))
        .append(
            (p, s, w) ->
                w.println(
                    "result.add(%s.apply(%s()));",
                    p.dtoMappingArgument(), p.asConversionMethodName()),
            1)
        .append(constant("}"));
  }

  private static Generator<ComposedAndMemberPojo, PojoSettings> oneOfFoldConditionAndContent() {
    return Generator.<ComposedAndMemberPojo, PojoSettings>emptyGen()
        .append(
            (pojo, s, w) ->
                w.println(
                    "%s (%s%s()) {",
                    pojo.ifOrElseIf(),
                    pojo.discriminatorCondition(),
                    pojo.isValidAgainstMethodName()))
        .append(
            (p, s, w) ->
                w.tab(1)
                    .println(
                        "return %s.apply(%s());",
                        p.dtoMappingArgument(), p.asConversionMethodName()))
        .append(constant("}"));
  }

  private static PList<String> fullFoldMethodArguments(JavaComposedPojo composedPojo) {
    return standardFoldMethodArguments(composedPojo).add("Supplier<T> onInvalid");
  }

  private static PList<String> standardFoldMethodArguments(JavaComposedPojo composedPojo) {
    return composedPojo
        .getJavaPojos()
        .map(
            pojo ->
                String.format(
                    "Function<%s, T> %s",
                    pojo.getClassName(), CompositionNames.dtoMappingArgumentName(pojo)));
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
                      discriminator.getValueForSchemaName(memberPojo.getSchemaName().asName()),
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

    private Name isValidAgainstMethodName() {
      return CompositionNames.isValidAgainstMethodName(memberPojo);
    }

    private Name asConversionMethodName() {
      return CompositionNames.asConversionMethodName(memberPojo);
    }

    private Name dtoMappingArgument() {
      return CompositionNames.dtoMappingArgumentName(memberPojo);
    }
  }
}
