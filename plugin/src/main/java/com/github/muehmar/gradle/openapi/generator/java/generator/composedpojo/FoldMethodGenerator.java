package com.github.muehmar.gradle.openapi.generator.java.generator.composedpojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.JavaDocGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class FoldMethodGenerator {

  private static final String JAVA_DOC_ONE_OF_FOLD =
      "Folds the oneOf part of this instance using the given mapping functions for the DTO's. If this instance is valid "
          + "against exactly one of the specified schemas, its corresponding mapping function gets executed with the "
          + "DTO as input and its result is returned.<br><br>\n\n";

  private static final String JAVA_DOC_ANY_OF_FOLD =
      "Folds the anyOf part of this instance using the given mapping functions for the DTO's. All mapping functions "
          + "gets executed with its corresponding DTO as input if this instance is valid against the corresponding "
          + "schema and the results are returned in a list. The order of the elements in the returned list is "
          + "deterministic: The order corresponds to the order of the mapping function arguments, i.e. the result of "
          + "the first mapping function will always be at the first position in the list (if the function gets "
          + "executed).<br><br>\n\n";

  private static final String JAVA_DOC_EXAMPLE =
      "I.e. if the JSON was valid against the schema '%s', the mapping method {@code %s} "
          + "gets executed with the {@link %s} as argument.<br><br>\n\n";

  private static final String JAVA_DOC_ONE_OF_THROWS =
      "This method assumes this instance is either manually or automatically validated, i.e. the JSON is valid "
          + "against exactly one of the oneOf schemas. If it is either valid against no schema or multiple schemas, "
          + "it will throw an {@link IllegalStateException}.";

  private static final String JAVA_DOC_ANY_OF_INVALID =
      "This method assumes this instance is either manually or automatically validated, i.e. "
          + "the JSON is valid against at least one of the anyOf schemas. If it is valid against no schema, "
          + "it will simply return an empty list.";

  private static final String JAVA_DOC_ONE_OF_FULL_FOLD =
      "Unlike %s, this method accepts as last parameter a {@link Supplier} which gets called in case this instance "
          + "is not valid against exactly one of the defined oneOf schemas and its value is returned.";

  private FoldMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> generator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendOptional(oneOfFoldMethods(), OneOfPojo::fromObjectPojo)
        .appendSingleBlankLine()
        .appendOptional(anyOfFoldMethods(), AnyOfPojo::fromObjectPojo);
  }

  private static Generator<OneOfPojo, PojoSettings> oneOfFoldMethods() {
    return fullFoldOneOfJavaDoc()
        .append(fullOneOfFoldMethod())
        .appendSingleBlankLine()
        .append(standardFoldOneOfJavaDoc())
        .append(standardOneOfFoldMethod());
  }

  private static Generator<AnyOfPojo, PojoSettings> anyOfFoldMethods() {
    return standardFoldAnyOfJavaDoc().append(standardAnyOfFoldMethod());
  }

  private static Generator<AnyOfPojo, PojoSettings> standardFoldAnyOfJavaDoc() {
    return JavaDocGenerator.javaDoc(
        (p, s) ->
            JAVA_DOC_ANY_OF_FOLD + getJavaDocExample(p.getMemberPojos()) + JAVA_DOC_ANY_OF_INVALID);
  }

  private static Generator<OneOfPojo, PojoSettings> standardFoldOneOfJavaDoc() {
    return JavaDocGenerator.javaDoc(
        (p, s) ->
            JAVA_DOC_ONE_OF_FOLD + getJavaDocExample(p.getMemberPojos()) + JAVA_DOC_ONE_OF_THROWS);
  }

  private static Generator<OneOfPojo, PojoSettings> fullFoldOneOfJavaDoc() {
    return JavaDocGenerator.javaDoc(
        (p, s) ->
            JAVA_DOC_ONE_OF_FOLD
                + getJavaDocExample(p.getMemberPojos())
                + getJavaDocFullFoldString(p));
  }

  private static String getJavaDocExample(PList<JavaObjectPojo> pojos) {
    return pojos
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

  private static String getJavaDocFullFoldString(OneOfPojo oneOf) {
    final String unsafeFoldRef =
        String.format(
            "{@link %s#foldOneOf(%s)}",
            oneOf.getPojo().getClassName(),
            oneOf.getMemberPojos().map(ignore -> "Function").mkString(", "));
    return String.format(JAVA_DOC_ONE_OF_FULL_FOLD, unsafeFoldRef);
  }

  private static Generator<OneOfPojo, PojoSettings> fullOneOfFoldMethod() {
    return MethodGenBuilder.<OneOfPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("T")
        .methodName("foldOneOf")
        .arguments(p -> fullFoldMethodArguments(p.getComposition().getPojos()))
        .content(fullFoldMethodContent())
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_FUNCTION))
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_SUPPLIER));
  }

  private static Generator<OneOfPojo, PojoSettings> standardOneOfFoldMethod() {
    return MethodGenBuilder.<OneOfPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("T")
        .methodName("foldOneOf")
        .arguments(pojo -> standardFoldMethodArguments(pojo.getComposition().getPojos()))
        .content(standardOneOfFoldMethodContent())
        .build()
        .append(ref(JavaRefs.JAVA_UTIL_FUNCTION));
  }

  private static Generator<AnyOfPojo, PojoSettings> standardAnyOfFoldMethod() {
    return MethodGenBuilder.<AnyOfPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("List<T>")
        .methodName("foldAnyOf")
        .arguments(pojo -> standardFoldMethodArguments(pojo.getComposition().getPojos()))
        .content(standardAnyOfFoldMethodContent())
        .build()
        .append(ref(JavaRefs.JAVA_UTIL_FUNCTION))
        .append(ref(JavaRefs.JAVA_UTIL_LIST));
  }

  private static Generator<OneOfPojo, PojoSettings> fullFoldMethodContent() {
    return Generator.<OneOfPojo, PojoSettings>emptyGen()
        .appendList(oneOfFoldConditionAndContent(), OneOfPojo::getOneOfMembers)
        .append(constant("else {"))
        .append(constant("return onInvalid.get();"), 1)
        .append(constant("}"));
  }

  private static Generator<OneOfPojo, PojoSettings> standardOneOfFoldMethodContent() {
    return Generator.<OneOfPojo, PojoSettings>emptyGen()
        .append(constant("return fold("))
        .appendList(
            (p, s, w) -> w.tab(1).println("%s,", p.dtoMappingArgument()),
            OneOfPojo::getOneOfMembers)
        .append(
            (p, s, w) ->
                w.println(
                    "() -> {throw new IllegalStateException(\"%s\");}", getOnInvalidMessage(p)),
            1)
        .append(constant(");"));
  }

  private static Generator<AnyOfPojo, PojoSettings> standardAnyOfFoldMethodContent() {
    return Generator.<AnyOfPojo, PojoSettings>emptyGen()
        .append(constant("final List<T> result = new ArrayList<>();"))
        .appendList(singleAnyOfFold(), AnyOfPojo::getAnyOfMembers)
        .append(constant("return result;"))
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_LIST))
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_ARRAY_LIST));
  }

  private static String getOnInvalidMessage(OneOfPojo oneOfPojo) {
    return String.format(
        "Unable to fold the oneOf part of %s: Not valid against one of the schemas [%s].",
        oneOfPojo.getPojo().getClassName(),
        oneOfPojo.getComposition().getPojos().map(JavaPojo::getClassName).mkString(", "));
  }

  private static Generator<AnyOfMemberPojo, PojoSettings> singleAnyOfFold() {
    return Generator.<AnyOfMemberPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("if (%s()) {", p.isValidAgainstMethodName()))
        .append(
            (p, s, w) ->
                w.println(
                    "result.add(%s.apply(%s()));",
                    p.dtoMappingArgument(), p.asConversionMethodName()),
            1)
        .append(constant("}"));
  }

  private static Generator<OneOfMemberPojo, PojoSettings> oneOfFoldConditionAndContent() {
    return Generator.<OneOfMemberPojo, PojoSettings>emptyGen()
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

  private static PList<String> fullFoldMethodArguments(PList<JavaObjectPojo> pojos) {
    return standardFoldMethodArguments(pojos).add("Supplier<T> onInvalid");
  }

  private static PList<String> standardFoldMethodArguments(PList<JavaObjectPojo> pojos) {
    return pojos.map(
        pojo ->
            String.format(
                "Function<%s, T> %s",
                pojo.getClassName(), CompositionNames.dtoMappingArgumentName(pojo)));
  }

  @Value
  private static class OneOfPojo {
    JavaObjectPojo pojo;
    JavaOneOfComposition composition;

    private static Optional<OneOfPojo> fromObjectPojo(JavaObjectPojo pojo) {
      return pojo.getOneOfComposition().map(composition -> new OneOfPojo(pojo, composition));
    }

    private PList<JavaObjectPojo> getMemberPojos() {
      return composition.getPojos();
    }

    private PList<OneOfMemberPojo> getOneOfMembers() {
      return composition.getPojos().map(member -> new OneOfMemberPojo(this, member));
    }
  }

  @Value
  private static class OneOfMemberPojo {
    OneOfPojo oneOfPojo;
    JavaObjectPojo memberPojo;

    private String discriminatorCondition() {
      return oneOfPojo
          .getComposition()
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
      return oneOfPojo
          .getComposition()
          .getPojos()
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

  @Value
  private static class AnyOfPojo {
    JavaObjectPojo pojo;
    JavaAnyOfComposition composition;

    private static Optional<AnyOfPojo> fromObjectPojo(JavaObjectPojo pojo) {
      return pojo.getAnyOfComposition().map(composition -> new AnyOfPojo(pojo, composition));
    }

    private PList<JavaObjectPojo> getMemberPojos() {
      return composition.getPojos();
    }

    private PList<AnyOfMemberPojo> getAnyOfMembers() {
      return composition.getPojos().map(member -> new AnyOfMemberPojo(this, member));
    }
  }

  @Value
  private static class AnyOfMemberPojo {
    AnyOfPojo anyOfPojo;
    JavaObjectPojo memberPojo;

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
