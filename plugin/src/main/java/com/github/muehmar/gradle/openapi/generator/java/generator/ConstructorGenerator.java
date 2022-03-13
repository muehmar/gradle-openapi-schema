package com.github.muehmar.gradle.openapi.generator.java.generator;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.FieldBuilder;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import io.github.muehmar.pojoextension.generator.Generator;
import io.github.muehmar.pojoextension.generator.impl.JavaModifier;
import io.github.muehmar.pojoextension.generator.impl.JavaModifiers;
import io.github.muehmar.pojoextension.generator.writer.Writer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@SafeBuilder
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
    return Generator.<A, B>ofWriterFunction(
            w -> {
              final String arguments = createArguments.apply(data, settings).mkString(", ");
              final String className = createClassName.apply(data, settings);
              final JavaModifiers modifiers = createModifiers.apply(data, settings);
              return w.print(
                  "%s%s(%s) {", modifiers.asStringTrailingWhitespace(), className, arguments);
            })
        .append(contentGenerator, 1)
        .append(w -> w.println("}"))
        .generate(data, settings, writer);
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
