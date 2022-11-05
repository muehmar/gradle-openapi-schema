package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.util.Booleans;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.java.MethodGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import java.util.function.Function;

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
        .appendConditionally(JavaParameter::printMinOrMax, this::printMin)
        .appendConditionally(JavaParameter::printMinOrMax, this::printMax)
        .appendConditionally(JavaParameter::printDecimalMinOrMax, this::printDecMin)
        .appendConditionally(JavaParameter::printDecimalMinOrMax, this::printDecMax)
        .appendConditionally(JavaParameter::printDefaultValue, this::printDefault)
        .appendConditionally(JavaParameter::printDefaultAsString, this::printDefaultAsString)
        .appendNewLine()
        .appendConditionally(JavaParameter::printMinOrMax, printExceedMinMaxLimits())
        .appendConditionally(JavaParameter::printDecimalMinOrMax, printExceedDecMinMaxLimits());
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
                writer
                    .println(
                        "public static final %s MIN = %s%s;",
                        parameter.getTypeClassName(),
                        decMin.getValue(),
                        parameter.javaConstantSuffix())
                    .println(
                        "public static final boolean EXCLUSIVE_MIN = %s;",
                        Booleans.not(decMin.isInclusiveMin())))
        .orElse(writer);
  }

  private Writer printDecMax(JavaParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onDecimalMaxFn(
            decMax ->
                writer
                    .println(
                        "public static final %s MAX = %s%s;",
                        parameter.getTypeClassName(),
                        decMax.getValue(),
                        parameter.javaConstantSuffix())
                    .println(
                        "public static final boolean EXCLUSIVE_MAX = %s;",
                        Booleans.not(decMax.isInclusiveMax())))
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

  private Generator<JavaParameter, PojoSettings> printExceedMinMaxLimits() {
    return MethodGenBuilder.<JavaParameter, PojoSettings>create()
        .modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType("boolean")
        .methodName("exceedLimits")
        .arguments(parameter -> PList.single(String.format("%s val", parameter.getTypeClassName())))
        .content(
            parameter -> {
              final Constraints constraints = parameter.getJavaType().getConstraints();
              final Optional<String> minCondition = constraints.onMinFn(min -> "val < MIN");
              final Optional<String> maxCondition = constraints.onMaxFn(max -> "MAX < val");
              final String condition =
                  PList.of(minCondition, maxCondition)
                      .flatMapOptional(Function.identity())
                      .reduce((a, b) -> a + " || " + b)
                      .orElse("false");
              return String.format("return %s;", condition);
            })
        .build();
  }

  private Generator<JavaParameter, PojoSettings> printExceedDecMinMaxLimits() {
    return MethodGenBuilder.<JavaParameter, PojoSettings>create()
        .modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType("boolean")
        .methodName("exceedLimits")
        .arguments(parameter -> PList.single(String.format("%s val", parameter.getTypeClassName())))
        .content(
            parameter -> {
              final Constraints constraints = parameter.getJavaType().getConstraints();
              final Optional<String> minCondition =
                  constraints.onDecimalMinFn(
                      min -> min.isInclusiveMin() ? "val < MIN" : "val <= MIN");
              final Optional<String> maxCondition =
                  constraints.onDecimalMaxFn(
                      max -> max.isInclusiveMax() ? "MAX < val" : "MAX <= val");
              final String condition =
                  PList.of(minCondition, maxCondition)
                      .flatMapOptional(Function.identity())
                      .reduce((a, b) -> a + " || " + b)
                      .orElse("false");
              return String.format("return %s;", condition);
            })
        .build();
  }
}
