package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;

public class ParameterGenerator implements Generator<JavaParameter, PojoSettings> {
  private final Generator<JavaParameter, PojoSettings> delegate;

  public ParameterGenerator() {
    this.delegate =
        ClassGenBuilder.<JavaParameter, PojoSettings>create()
            .clazz()
            .topLevel()
            .packageGen(
                (a, settings, writer) ->
                    writer.println("package %s.parameter;", settings.getPackageName()))
            .modifiers(PUBLIC, FINAL)
            .className(parameter -> parameter.getName().startUpperCase().asString())
            .noSuperClass()
            .noInterfaces()
            .content(content())
            .andAllOptionals()
            .build();
  }

  @Override
  public Writer generate(JavaParameter parameter, PojoSettings settings, Writer writer) {
    return delegate.generate(parameter, settings, writer);
  }

  private Generator<JavaParameter, PojoSettings> content() {
    return Generator.<JavaParameter, PojoSettings>emptyGen()
        .append(this::printConstructor)
        .appendNewLine()
        .appendConditionally(JavaParameter::printMin, this::printMin)
        .appendConditionally(JavaParameter::printMax, this::printMax)
        .appendConditionally(JavaParameter::printDecimalMin, this::printDecMin)
        .appendConditionally(JavaParameter::printDecimalMax, this::printDecMax)
        .appendConditionally(JavaParameter::printDefaultValue, this::printDefault)
        .appendConditionally(JavaParameter::printDefaultAsString, this::printDefaultAsString);
  }

  private <T> Writer printConstructor(JavaParameter parameter, T settings, Writer writer) {
    return writer.println("private %s() {}", parameter.getParamClassName());
  }

  private Writer printMin(JavaParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onMinFn(
            min ->
                writer.println(
                    "public static final %s MIN = %s%s;",
                    parameter.getTypeClassName(), min.getValue(), parameter.javaConstantSuffix()))
        .orElse(writer);
  }

  private Writer printMax(JavaParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onMaxFn(
            max ->
                writer.println(
                    "public static final %s MAX = %s%s;",
                    parameter.getTypeClassName(), max.getValue(), parameter.javaConstantSuffix()))
        .orElse(writer);
  }

  private Writer printDecMin(JavaParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onDecimalMinFn(
            decMin ->
                writer.println(
                    "public static final %s MIN = %s%s;",
                    parameter.getTypeClassName(),
                    decMin.getValue(),
                    parameter.javaConstantSuffix()))
        .orElse(writer);
  }

  private Writer printDecMax(JavaParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onDecimalMaxFn(
            decMax ->
                writer.println(
                    "public static final %s MAX = %s%s;",
                    parameter.getTypeClassName(),
                    decMax.getValue(),
                    parameter.javaConstantSuffix()))
        .orElse(writer);
  }

  private Writer printDefault(JavaParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .getDefaultValue()
        .map(
            defaultValue ->
                writer.println(
                    "public static final %s DEFAULT = %s%s;",
                    parameter.getTypeClassName(), defaultValue, parameter.javaConstantSuffix()))
        .orElse(writer);
  }

  private Writer printDefaultAsString(
      JavaParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .getDefaultValue()
        .map(
            defaultValue ->
                writer.println("public static final String DEFAULT_STR = \"%s\";", defaultValue))
        .orElse(writer);
  }
}
