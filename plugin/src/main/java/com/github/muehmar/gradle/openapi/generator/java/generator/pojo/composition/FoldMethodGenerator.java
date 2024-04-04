package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator.ref;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.FoldMethodJavaDocGenerator.multiResultFoldJavaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.FoldMethodJavaDocGenerator.singleResultFoldJavaDoc;
import static com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition.Type.ANY_OF;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames.Composition.foldCompositionMethodName;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.ref.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.MethodGen.Argument;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

public class FoldMethodGenerator {
  private FoldMethodGenerator() {}

  public static Generator<JavaObjectPojo, PojoSettings> foldMethodGenerator() {
    return Generator.<JavaObjectPojo, PojoSettings>emptyGen()
        .appendList(singleResultFoldMethods(), SingleResultFoldContainer::fromObjectPojo)
        .appendSingleBlankLine()
        .appendOptional(multiResultFoldMethods(), MultiResultFoldContainer::fromObjectPojo);
  }

  private static Generator<SingleResultFoldContainer, PojoSettings> singleResultFoldMethods() {
    return singleResultFoldJavaDoc(true)
        .append(fullSingleResultFoldMethod())
        .appendSingleBlankLine()
        .append(singleResultFoldJavaDoc(false))
        .append(standardSingleResultFoldMethod());
  }

  private static Generator<MultiResultFoldContainer, PojoSettings> multiResultFoldMethods() {
    return multiResultFoldJavaDoc().append(standardMultipleResultFoldMethod());
  }

  private static Generator<SingleResultFoldContainer, PojoSettings> fullSingleResultFoldMethod() {
    return MethodGenBuilder.<SingleResultFoldContainer, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("T")
        .methodName(FoldMethodGenerator::singleResultFoldMethodName)
        .arguments(p -> fullFoldMethodArguments(p.getComposition().getPojos()).toPList())
        .doesNotThrow()
        .content(fullSingleResultFoldMethodContent())
        .build()
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_FUNCTION))
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_SUPPLIER));
  }

  private static Generator<SingleResultFoldContainer, PojoSettings>
      standardSingleResultFoldMethod() {
    return MethodGenBuilder.<SingleResultFoldContainer, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("T")
        .methodName(FoldMethodGenerator::singleResultFoldMethodName)
        .arguments(pojo -> standardFoldMethodArguments(pojo.getComposition().getPojos()).toPList())
        .doesNotThrow()
        .content(standardSingleResultFoldMethodContent())
        .build()
        .append(ref(JavaRefs.JAVA_UTIL_FUNCTION));
  }

  static String singleResultFoldMethodName(SingleResultFoldContainer container) {
    return foldCompositionMethodName(container.getType()).asString();
  }

  private static Generator<MultiResultFoldContainer, PojoSettings>
      standardMultipleResultFoldMethod() {
    return MethodGenBuilder.<MultiResultFoldContainer, PojoSettings>create()
        .modifiers(PUBLIC)
        .genericTypes("T")
        .returnType("List<T>")
        .methodName(foldCompositionMethodName(ANY_OF).asString())
        .arguments(pojo -> standardFoldMethodArguments(pojo.getComposition().getPojos()).toPList())
        .doesNotThrow()
        .content(standardMultipleResultFoldMethodContent())
        .build()
        .append(ref(JavaRefs.JAVA_UTIL_FUNCTION))
        .append(ref(JavaRefs.JAVA_UTIL_LIST));
  }

  private static Generator<SingleResultFoldContainer, PojoSettings>
      fullSingleResultFoldMethodContent() {
    return Generator.<SingleResultFoldContainer, PojoSettings>emptyGen()
        .appendList(singleResultFoldConditionAndContent(), SingleResultFoldContainer::getMembers)
        .append(constant("else {"))
        .append(constant("return onInvalid.get();"), 1)
        .append(constant("}"));
  }

  private static Generator<SingleResultFoldContainer, PojoSettings>
      standardSingleResultFoldMethodContent() {
    return Generator.<SingleResultFoldContainer, PojoSettings>emptyGen()
        .append((c, s, w) -> w.println("return %s(", singleResultFoldMethodName(c)))
        .appendList(
            (p, s, w) -> w.tab(1).println("%s,", p.dtoMappingArgument()),
            SingleResultFoldContainer::getMembers)
        .append(
            (p, s, w) ->
                w.println(
                    "() -> {throw new IllegalStateException(\"%s\");}", getOnInvalidMessage(p)),
            1)
        .append(constant(");"));
  }

  private static Generator<MultiResultFoldContainer, PojoSettings>
      standardMultipleResultFoldMethodContent() {
    return Generator.<MultiResultFoldContainer, PojoSettings>emptyGen()
        .append(constant("final List<T> result = new ArrayList<>();"))
        .appendList(multipleResultFoldSinglePojoApply(), MultiResultFoldContainer::getAnyOfMembers)
        .append(constant("return result;"))
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_LIST))
        .append(w -> w.ref(JavaRefs.JAVA_UTIL_ARRAY_LIST));
  }

  private static String getOnInvalidMessage(SingleResultFoldContainer singleResultFoldContainer) {
    return String.format(
        "Unable to fold the %s part of %s: Not valid against one of the schemas [%s] or not valid against the schema described by the discriminator.",
        singleResultFoldContainer.getType().getName().startLowerCase(),
        singleResultFoldContainer.getPojo().getClassName(),
        singleResultFoldContainer
            .getComposition()
            .getPojos()
            .map(JavaPojo::getClassName)
            .toPList()
            .mkString(", "));
  }

  private static Generator<MultiResultFoldMemberPojo, PojoSettings>
      multipleResultFoldSinglePojoApply() {
    return Generator.<MultiResultFoldMemberPojo, PojoSettings>emptyGen()
        .append((p, s, w) -> w.println("if (%s()) {", p.isValidAgainstMethodName()))
        .append(
            (p, s, w) ->
                w.println(
                    "result.add(%s.apply(%s()));",
                    p.dtoMappingArgument(), p.asConversionMethodName()),
            1)
        .append(constant("}"));
  }

  private static Generator<SingleResultFoldMemberPojo, PojoSettings>
      singleResultFoldConditionAndContent() {
    return Generator.<SingleResultFoldMemberPojo, PojoSettings>emptyGen()
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
  static class SingleResultFoldContainer {
    JavaObjectPojo pojo;
    DiscriminatableJavaComposition composition;

    private static PList<SingleResultFoldContainer> fromObjectPojo(JavaObjectPojo pojo) {
      final Optional<SingleResultFoldContainer> oneOfCompositionPojo =
          pojo.getOneOfComposition()
              .map(composition -> new SingleResultFoldContainer(pojo, composition));
      final Optional<SingleResultFoldContainer> anyOfCompositionWithDiscriminatorPojo =
          pojo.getAnyOfComposition()
              .filter(DiscriminatableJavaComposition::hasDiscriminator)
              .map(composition -> new SingleResultFoldContainer(pojo, composition));
      return PList.of(oneOfCompositionPojo, anyOfCompositionWithDiscriminatorPojo)
          .flatMapOptional(Function.identity());
    }

    private NonEmptyList<JavaObjectPojo> getMemberPojos() {
      return composition.getPojos();
    }

    private NonEmptyList<SingleResultFoldMemberPojo> getMembers() {
      return composition.getPojos().map(member -> new SingleResultFoldMemberPojo(this, member));
    }

    public DiscriminatableJavaComposition.Type getType() {
      return composition.getType();
    }
  }

  @Value
  static class SingleResultFoldMemberPojo {
    SingleResultFoldContainer singleResultFoldContainer;
    JavaObjectPojo memberPojo;

    private String discriminatorCondition() {
      return singleResultFoldContainer
          .getComposition()
          .getDiscriminator()
          .map(
              discriminator ->
                  String.format(
                      "\"%s\".equals(%s) && ",
                      discriminator.getStringValueForSchemaName(
                          memberPojo.getSchemaName().getOriginalName()),
                      discriminator.discriminatorPropertyToStringValue()))
          .orElse("");
    }

    private String ifOrElseIf() {
      return singleResultFoldContainer.getComposition().getPojos().head().equals(memberPojo)
          ? "if"
          : "else if";
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
  static class MultiResultFoldContainer {
    JavaObjectPojo pojo;
    DiscriminatableJavaComposition composition;

    private static Optional<MultiResultFoldContainer> fromObjectPojo(JavaObjectPojo pojo) {
      return pojo.getAnyOfComposition()
          .filter(anyOfComposition -> not(anyOfComposition.hasDiscriminator()))
          .map(composition -> new MultiResultFoldContainer(pojo, composition));
    }

    private NonEmptyList<JavaObjectPojo> getMemberPojos() {
      return composition.getPojos();
    }

    private NonEmptyList<MultiResultFoldMemberPojo> getAnyOfMembers() {
      return composition.getPojos().map(member -> new MultiResultFoldMemberPojo(this, member));
    }
  }

  @Value
  static class MultiResultFoldMemberPojo {
    MultiResultFoldContainer multiResultFoldContainer;
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
