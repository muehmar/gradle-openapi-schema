package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder;

import static io.github.muehmar.codegenerator.Generator.constant;
import static io.github.muehmar.codegenerator.java.JavaModifier.*;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import java.util.function.Function;

public class SingleBuilderClassGenerator {
  private SingleBuilderClassGenerator() {}

  public static <T> Generator<T, PojoSettings> singleBuilderClassGenerator(
      Function<T, String> createClassName, Generator<T, PojoSettings> content) {
    return singleBuilderClassGenerator(createClassName, PList.single(content));
  }

  public static <T> Generator<T, PojoSettings> singleBuilderClassGenerator(
      Function<T, String> createClassName, PList<Generator<T, PojoSettings>> content) {
    final Generator<T, PojoSettings> singleGenContent =
        content
            .reduce((gen1, gen2) -> gen1.appendSingleBlankLine().append(gen2))
            .orElse(Generator.emptyGen());
    return ClassGenBuilder.<T, PojoSettings>create()
        .clazz()
        .nested()
        .packageGen(Generator.emptyGen())
        .noJavaDoc()
        .noAnnotations()
        .modifiers(PUBLIC, STATIC, FINAL)
        .className(createClassName)
        .noSuperClass()
        .noInterfaces()
        .content(builderContent(createClassName).appendSingleBlankLine().append(singleGenContent))
        .build();
  }

  private static <T> Generator<T, PojoSettings> builderContent(
      Function<T, String> createClassName) {
    return Generator.<T, PojoSettings>emptyGen()
        .append(constant("private final Builder builder;"))
        .appendNewLine()
        .append((m, s, w) -> w.println("private %s(Builder builder) {", createClassName.apply(m)))
        .append(constant("this.builder = builder;"), 1)
        .append(constant("}"))
        .appendSingleBlankLine();
  }
}
