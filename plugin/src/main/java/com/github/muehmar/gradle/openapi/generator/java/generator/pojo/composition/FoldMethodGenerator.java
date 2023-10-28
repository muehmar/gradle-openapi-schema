package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.AnyOf.foldAnyOfMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.OneOf.foldOneOfMethodName;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import lombok.Value;

public class FoldMethodGenerator {

  private static final String JAVA_DOC_ONE_OF_FOLD =
      "Folds the oneOf part of this instance using the given mapping functions for the DTO's. If this instance is valid "
          + "against exactly one of the specified schemas, its corresponding mapping function gets executed with the "
          + "DTO as input and its result is returned.\n\n";

  private static final String JAVA_DOC_ANY_OF_FOLD =
      "Folds the anyOf part of this instance using the given mapping functions for the DTO's. All mapping functions "
          + "gets executed with its corresponding DTO as input if this instance is valid against the corresponding "
          + "schema and the results are returned in a list. The order of the elements in the returned list is "
          + "deterministic: The order corresponds to the order of the mapping function arguments, i.e. the result of "
          + "the first mapping function will always be at the first position in the list (if the function gets "
          + "executed).\n\n";

  private static final String JAVA_DOC_EXAMPLE =
      "I.e. if the JSON was valid against the schema '%s', the mapping method {@code %s} "
          + "gets executed with the {@link %s} as argument.\n\n";

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

  public static Generator<JavaObjectPojo, PojoSettings> foldMethodGenerator() {
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

  private static String getJavaDocExample(NonEmptyList<JavaObjectPojo> pojos) {
    final JavaObjectPojo ep = pojos.head();
    return String.format(
        JAVA_DOC_EXAMPLE,
        ep.getSchemaName().getOriginalName(),
        MethodNames.Composition.dtoMappingArgumentName(ep),
        ep.getClassName());
  }

  private static String getJavaDocFullFoldString(OneOfPojo oneOf) {
    final String unsafeFoldRef =
        String.format(
            "{@link %s#%s(%s)}",
            oneOf.getPojo().getClassName(),
            foldOneOfMethodName(),
            oneOf.getMemberPojos().map(ignore -> "Function").toPList().mkString(", "));
    return String.format(JAVA_DOC_ONE_OF_FULL_FOLD, unsafeFoldRef);
  }

  private static Generator<OneOfPojo, PojoSettings> fullOneOfFoldMethod() {
    return MethodGenBuilder.<OneOfPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("T")
        .methodName(foldOneOfMethodName().asString())
        .arguments(p -> fullFoldMethodArguments(p.getComposition().getPojos()).toPList())
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
        .methodName(foldOneOfMethodName().asString())
        .arguments(pojo -> standardFoldMethodArguments(pojo.getComposition().getPojos()).toPList())
        .content(standardOneOfFoldMethodContent())
        .build()
        .append(ref(JavaRefs.JAVA_UTIL_FUNCTION));
  }

  private static Generator<AnyOfPojo, PojoSettings> standardAnyOfFoldMethod() {
    return MethodGenBuilder.<AnyOfPojo, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("List<T>")
        .methodName(foldAnyOfMethodName().asString())
        .arguments(pojo -> standardFoldMethodArguments(pojo.getComposition().getPojos()).toPList())
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
        .append(w -> w.println("return %s(", foldOneOfMethodName()))
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
        oneOfPojo.getComposition().getPojos().map(JavaPojo::getClassName).toPList().mkString(", "));
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

  private static NonEmptyList<Argument> fullFoldMethodArguments(
      NonEmptyList<JavaObjectPojo> pojos) {
    return standardFoldMethodArguments(pojos).add(new Argument("Supplier<T>", "onInvalid"));
  }

  private static NonEmptyList<Argument> standardFoldMethodArguments(
      NonEmptyList<JavaObjectPojo> pojos) {
    return pojos.map(
        pojo ->
            new Argument(
                String.format("Function<%s, T>", pojo.getClassName()),
                MethodNames.Composition.dtoMappingArgumentName(pojo).asString()));
  }

  @Value
  private static class OneOfPojo {
    JavaObjectPojo pojo;
    JavaOneOfComposition composition;

    private static Optional<OneOfPojo> fromObjectPojo(JavaObjectPojo pojo) {
      return pojo.getOneOfComposition().map(composition -> new OneOfPojo(pojo, composition));
    }

    private NonEmptyList<JavaObjectPojo> getMemberPojos() {
      return composition.getPojos();
    }

    private NonEmptyList<OneOfMemberPojo> getOneOfMembers() {
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
                      discriminator.getValueForSchemaName(
                          memberPojo.getSchemaName().getOriginalName()),
                      discriminator.getPropertyName()))
          .orElse("");
    }

    private String ifOrElseIf() {
      return oneOfPojo.getComposition().getPojos().head().equals(memberPojo) ? "if" : "else if";
    }

    private Name isValidAgainstMethodName() {
      return MethodNames.Composition.isValidAgainstMethodName(memberPojo);
    }

    private Name asConversionMethodName() {
      return MethodNames.Composition.asConversionMethodName(memberPojo);
    }

    private Name dtoMappingArgument() {
      return MethodNames.Composition.dtoMappingArgumentName(memberPojo);
    }
  }

  @Value
  private static class AnyOfPojo {
    JavaObjectPojo pojo;
    JavaAnyOfComposition composition;

    private static Optional<AnyOfPojo> fromObjectPojo(JavaObjectPojo pojo) {
      return pojo.getAnyOfComposition().map(composition -> new AnyOfPojo(pojo, composition));
    }

    private NonEmptyList<JavaObjectPojo> getMemberPojos() {
      return composition.getPojos();
    }

    private NonEmptyList<AnyOfMemberPojo> getAnyOfMembers() {
      return composition.getPojos().map(member -> new AnyOfMemberPojo(this, member));
    }
  }

  @Value
  private static class AnyOfMemberPojo {
    AnyOfPojo anyOfPojo;
    JavaObjectPojo memberPojo;

    private Name isValidAgainstMethodName() {
      return MethodNames.Composition.isValidAgainstMethodName(memberPojo);
    }

    private Name asConversionMethodName() {
      return MethodNames.Composition.asConversionMethodName(memberPojo);
    }

    private Name dtoMappingArgument() {
      return MethodNames.Composition.dtoMappingArgumentName(memberPojo);
    }
  }
}
