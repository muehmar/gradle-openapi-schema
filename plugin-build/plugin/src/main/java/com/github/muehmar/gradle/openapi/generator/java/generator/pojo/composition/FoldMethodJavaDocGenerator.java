package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.FoldMethodGenerator.singleResultFoldMethodName;
import static com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition.Type.ONE_OF;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaDocGenerator;
import io.github.muehmar.codegenerator.writer.Writer;

class FoldMethodJavaDocGenerator {
  private FoldMethodJavaDocGenerator() {}

  static Generator<FoldMethodGenerator.MultiResultFoldContainer, PojoSettings>
      multiResultFoldJavaDoc() {
    final Generator<FoldMethodGenerator.MultiResultFoldContainer, PojoSettings> gen =
        multiFoldIntro()
            .append(javaDocExample(), FoldMethodGenerator.MultiResultFoldContainer::getComposition)
            .append(multiFoldNoValidSchemaDoc());
    return JavaDocGenerator.javaDoc(
        (container, settings) -> gen.generate(container, settings, Writer.javaWriter()).asString());
  }

  private static Generator<FoldMethodGenerator.MultiResultFoldContainer, PojoSettings>
      multiFoldIntro() {
    return Generator.constant(
        "Folds the anyOf part of this instance using the given mapping functions for the DTO's. All mapping functions "
            + "gets executed with its corresponding DTO as input if this instance is valid against the corresponding "
            + "schema and the results are returned in a list. The order of the elements in the returned list is "
            + "deterministic: The order corresponds to the order of the mapping function arguments, i.e. the result of "
            + "the first mapping function will always be at the first position in the list (if the function gets "
            + "executed).\n");
  }

  private static Generator<FoldMethodGenerator.MultiResultFoldContainer, PojoSettings>
      multiFoldNoValidSchemaDoc() {
    return Generator.constant(
        "This method assumes this instance is either manually or automatically validated, i.e. "
            + "the JSON is valid against at least one of the anyOf schemas. If it is valid against no schema, "
            + "it will simply return an empty list.");
  }

  static Generator<FoldMethodGenerator.SingleResultFoldContainer, PojoSettings>
      singleResultFoldJavaDoc(boolean fullMethod) {
    final Generator<FoldMethodGenerator.SingleResultFoldContainer, PojoSettings> gen =
        singleFoldIntro1()
            .append(singleFoldIntro2())
            .append(singleFoldIntro3())
            .append(javaDocExample(), FoldMethodGenerator.SingleResultFoldContainer::getComposition)
            .append(singleFoldFullMethod().filter(ignore -> fullMethod))
            .append(singleFoldStandardMethod().filter(ignore -> not(fullMethod)));
    return JavaDocGenerator.javaDoc(
        (container, settings) -> gen.generate(container, settings, Writer.javaWriter()).asString());
  }

  private static Generator<FoldMethodGenerator.SingleResultFoldContainer, PojoSettings>
      singleFoldIntro1() {
    return (container, s, w) ->
        w.print(
            "Folds the %s part of this instance using the given mapping functions for the DTO's.",
            container.getType().getName().startLowerCase());
  }

  private static Generator<FoldMethodGenerator.SingleResultFoldContainer, PojoSettings>
      singleFoldIntro2() {
    return (container, s, w) ->
        container.getType().equals(ONE_OF)
            ? w.print(" If this instance is valid against exactly one of the specified schemas")
            : w.print(
                " If this instance is valid against the schema described by the discriminator");
  }

  private static Generator<FoldMethodGenerator.SingleResultFoldContainer, PojoSettings>
      singleFoldIntro3() {
    return Generator.constant(
        ", its corresponding mapping function gets executed with the DTO as input and its result is returned.\n");
  }

  private static Generator<DiscriminatableJavaComposition, PojoSettings> javaDocExample() {
    return (composition, s, w) ->
        w.println(
            "I.e. if the JSON was valid against the schema '%s'%s, the mapping method {@code %s} gets executed with the {@link %s} as argument.\n",
            composition.getPojos().head().getSchemaName().getOriginalName(),
            composition.hasDiscriminator() ? " and the discriminator points to this schema" : "",
            MethodNames.Composition.dtoMappingArgumentName(composition.getPojos().head()),
            composition.getPojos().head().getClassName());
  }

  private static Generator<FoldMethodGenerator.SingleResultFoldContainer, PojoSettings>
      singleFoldFullMethod() {
    final String oneOfCondition = "not valid against exactly one of the defined oneOf schemas";
    final String oneOfDiscriminatorCondition =
        oneOfCondition + " and not valid against the schema described by the discriminator";
    final String anyOfCondition = "not valid against the schema described by the discriminator";
    final String format =
        "Unlike %s, this method accepts as last parameter a {@link Supplier} which gets called "
            + "in case this instance is %s and its value is returned.";
    return (container, s, w) -> {
      final String unsafeFoldRef =
          String.format(
              "{@link %s#%s(%s)}",
              container.getPojo().getClassName(),
              singleResultFoldMethodName(container),
              container
                  .getComposition()
                  .getPojos()
                  .map(ignore -> "Function")
                  .toPList()
                  .mkString(", "));
      final String oneOf =
          container.getComposition().hasDiscriminator()
              ? oneOfDiscriminatorCondition
              : oneOfCondition;
      final String condition = container.getType().equals(ONE_OF) ? oneOf : anyOfCondition;
      return w.println(format, unsafeFoldRef, condition);
    };
  }

  private static Generator<FoldMethodGenerator.SingleResultFoldContainer, PojoSettings>
      singleFoldStandardMethod() {
    final String oneOfCondition = "valid against exactly one of the defined oneOf schemas";
    final String oneOfDiscriminatorCondition =
        oneOfCondition + " and valid against the schema described by the discriminator";
    final String anyOfCondition = "valid against the schema described by the discriminator";
    final String format =
        "This method assumes this instance is either manually or automatically validated, i.e. "
            + "it is %s. If not, it will throw an {@link "
            + "IllegalStateException}";
    return (container, s, w) -> {
      final String oneOf =
          container.getComposition().hasDiscriminator()
              ? oneOfDiscriminatorCondition
              : oneOfCondition;
      final String condition = container.getType().equals(ONE_OF) ? oneOf : anyOfCondition;
      return w.println(format, condition);
    };
  }
}
