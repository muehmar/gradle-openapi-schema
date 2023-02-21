package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.util.Functions.allExceptFirst;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.JavaModifier;
import io.github.muehmar.codegenerator.java.JavaModifiers;
import io.github.muehmar.codegenerator.writer.Writer;
import io.github.muehmar.pojobuilder.annotations.FieldBuilder;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@PojoBuilder
public class ConstructorGenerator<A, B> implements Generator<A, B> {
  private final BiFunction<A, B, JavaModifiers> createModifiers;
  private final BiFunction<A, B, String> createClassName;
  private final BiFunction<A, B, PList<String>> createArguments;
  private final Generator<A, B> contentGenerator;

  ConstructorGenerator(
      BiFunction<A, B, JavaModifiers> createModifiers,
      BiFunction<A, B, String> createClassName,
      BiFunction<A, B, PList<String>> createArguments,
      Generator<A, B> contentGenerator) {
    this.createModifiers = createModifiers;
    this.createClassName = createClassName;
    this.createArguments = createArguments;
    this.contentGenerator = contentGenerator;
  }

  @Override
  public Writer generate(A data, B settings, Writer writer) {
    return Generator.<A, B>emptyGen()
        .append(
            w -> {
              final JavaModifiers modifiers = createModifiers.apply(data, settings);
              final String className = createClassName.apply(data, settings);
              return w.println("%s%s(", modifiers.asStringTrailingWhitespace(), className);
            })
        .append(argumentGenerator(), 2)
        .append(w -> w.tab(1).println(") {"))
        .append(contentGenerator, 1)
        .append(w -> w.println("}"))
        .generate(data, settings, writer);
  }

  private Generator<A, B> argumentGenerator() {
    return (data, settings, writer) ->
        createArguments
            .apply(data, settings)
            .reverse()
            .zipWithIndex()
            .map(allExceptFirst(arg -> arg + ","))
            .reverse()
            .foldLeft(writer, Writer::println);
  }

  @FieldBuilder(fieldName = "createModifiers")
  static class ModifiersGen {
    private ModifiersGen() {}

    static <A, B> BiFunction<A, B, JavaModifiers> modifiers() {
      return (d, s) -> JavaModifiers.of();
    }

    static <A, B> BiFunction<A, B, JavaModifiers> modifiers(JavaModifier modifier) {
      return (d, s) -> JavaModifiers.of(modifier);
    }

    static <A, B> BiFunction<A, B, JavaModifiers> modifiers(JavaModifier m1, JavaModifier m2) {
      return (d, s) -> JavaModifiers.of(m1, m2);
    }
  }

  @FieldBuilder(fieldName = "createClassName")
  static class ClassNameBuilder {
    private ClassNameBuilder() {}

    static <A, B> BiFunction<A, B, String> className(BiFunction<A, B, String> createClassName) {
      return createClassName;
    }

    static <A, B> BiFunction<A, B, String> pojoName(Function<A, PojoName> createClassName) {
      return (d, s) -> createClassName.apply(d).asString();
    }

    static <A, B> BiFunction<A, B, String> className(Function<A, String> createClassName) {
      return (d, s) -> createClassName.apply(d);
    }

    static <A, B> BiFunction<A, B, String> className(String className) {
      return (d, s) -> className;
    }
  }

  @FieldBuilder(fieldName = "createArguments")
  static class ArgumentsBuilder {
    private ArgumentsBuilder() {}

    static <A, B> BiFunction<A, B, PList<String>> arguments(
        BiFunction<A, B, PList<String>> createArguments) {
      return createArguments;
    }

    static <A, B> BiFunction<A, B, PList<String>> arguments(
        Function<A, PList<String>> createArguments) {
      return (d, s) -> createArguments.apply(d);
    }

    static <A, B> BiFunction<A, B, PList<String>> singleArgument(
        Function<A, String> createArgument) {
      return (d, s) -> PList.single(createArgument.apply(d));
    }

    static <A, B> BiFunction<A, B, PList<String>> singleArgument(String argument) {
      return (d, s) -> PList.single(argument);
    }

    static <A, B> BiFunction<A, B, PList<String>> noArguments() {
      return (d, s) -> PList.empty();
    }
  }

  @FieldBuilder(fieldName = "contentGenerator")
  static class ContentBuilder {
    private ContentBuilder() {}

    static <A, B> Generator<A, B> content(String content) {
      return (data, settings, writer) -> writer.println(content);
    }

    static <A, B> Generator<A, B> content(Generator<A, B> content) {
      return content;
    }

    static <A, B> Generator<A, B> content(UnaryOperator<Writer> content) {
      return (data, settings, writer) -> content.apply(writer);
    }

    static <A, B> Generator<A, B> noContent() {
      return (data, settings, writer) -> writer;
    }
  }
}
