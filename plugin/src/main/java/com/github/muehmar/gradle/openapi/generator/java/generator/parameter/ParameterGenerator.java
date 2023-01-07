package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import static com.github.muehmar.gradle.openapi.generator.java.JavaRefs.JAVA_UTIL_REGEX_PATTERN;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;
import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;
import static io.github.muehmar.codegenerator.java.JavaModifier.STATIC;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaEscaper;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
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
            .noJavaDoc()
            .noAnnotations()
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
        .appendConditionally(JavaParameter::printSize, this::printMinLength)
        .appendConditionally(JavaParameter::printSize, this::printMaxLength)
        .appendConditionally(JavaParameter::printPattern, this::printPattern)
        .appendConditionally(JavaParameter::printPattern, this::printPatternString)
        .appendConditionally(JavaParameter::printDefaultValue, this::printDefault)
        .appendConditionally(JavaParameter::printDefaultAsString, this::printDefaultAsString)
        .appendNewLine()
        .appendConditionally(JavaParameter::printMinOrMax, printMatchesMinMaxLimits())
        .appendConditionally(JavaParameter::printDecimalMinOrMax, printMatchesDecMinMaxLimits())
        .appendConditionally(JavaParameter::printSize, printMatchesSizeLimits())
        .appendConditionally(JavaParameter::printPattern, printMatchesPattern());
  }

  private <T> Writer printConstructor(JavaParameter parameter, T settings, Writer writer) {
    return writer.println("private %s() {}", parameter.getParamClassName());
  }

  private <T> Writer printMinLength(JavaParameter parameter, T settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onSizeFn(Size::getMin)
        .flatMap(Function.identity())
        .map(value -> printPublicConstant("int", "MIN_LENGTH", value, writer))
        .orElse(writer);
  }

  private <T> Writer printMaxLength(JavaParameter parameter, T settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onSizeFn(Size::getMax)
        .flatMap(Function.identity())
        .map(value -> printPublicConstant("int", "MAX_LENGTH", value, writer))
        .orElse(writer);
  }

  private <T> Writer printMin(JavaParameter parameter, T settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onMinFn(Min::getValue)
        .map(value -> printPublicConstant(parameter, "MIN", value, writer))
        .orElse(writer);
  }

  private <T> Writer printMax(JavaParameter parameter, T settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onMaxFn(Max::getValue)
        .map(value -> printPublicConstant(parameter, "MAX", value, writer))
        .orElse(writer);
  }

  private <T> Writer printDecMin(JavaParameter parameter, T settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onDecimalMinFn(
            decMin -> {
              final Writer tempWriter =
                  printPublicConstant(parameter, "MIN", decMin.getValue(), writer);
              return printPublicConstant(
                  "boolean", "EXCLUSIVE_MIN", not(decMin.isInclusiveMin()), tempWriter);
            })
        .orElse(writer);
  }

  private <T> Writer printDecMax(JavaParameter parameter, T settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onDecimalMaxFn(
            decMax -> {
              final Writer tempWriter =
                  printPublicConstant(parameter, "MAX", decMax.getValue(), writer);
              return printPublicConstant(
                  "boolean", "EXCLUSIVE_MAX", not(decMax.isInclusiveMax()), tempWriter);
            })
        .orElse(writer);
  }

  private <T> Writer printPattern(JavaParameter parameter, T settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onPatternFn(pattern -> pattern.getPatternEscaped(JavaEscaper::escape))
        .map(
            pattern ->
                writer
                    .println(
                        "public static final Pattern PATTERN = Pattern.compile(\"%s\");", pattern)
                    .ref(JAVA_UTIL_REGEX_PATTERN))
        .orElse(writer);
  }

  private <T> Writer printPatternString(JavaParameter parameter, T settings, Writer writer) {
    return parameter
        .getJavaType()
        .getConstraints()
        .onPatternFn(pattern -> pattern.getPatternEscaped(JavaEscaper::escape))
        .map(pattern -> printPublicConstant("String", "PATTERN_STR", "\"" + pattern + "\"", writer))
        .orElse(writer);
  }

  private <T> Writer printDefault(JavaParameter parameter, T settings, Writer writer) {
    return parameter
        .getDefaultValue()
        .map(defaultValue -> printPublicConstant(parameter, "DEFAULT", defaultValue, writer))
        .orElse(writer);
  }

  private <T> Writer printDefaultAsString(JavaParameter parameter, T settings, Writer writer) {
    return parameter
        .getDefaultValue()
        .map(
            defaultValue ->
                printPublicConstant("String", "DEFAULT_STR", "\"" + defaultValue + "\"", writer))
        .orElse(writer);
  }

  private Writer printPublicConstant(
      JavaParameter parameter, String constantName, Object value, Writer writer) {
    return printPublicConstant(
        parameter.getTypeClassName().asString(),
        constantName,
        parameter.formatConstant(value),
        writer);
  }

  private Writer printPublicConstant(
      String className, String constantName, Object value, Writer writer) {
    return writer.println("public static final %s %s = %s;", className, constantName, value);
  }

  private <T> Generator<JavaParameter, T> printMatchesPattern() {
    return MethodGenBuilder.<JavaParameter, T>create()
        .modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType("boolean")
        .methodName("matchesPattern")
        .arguments(parameter -> PList.single(String.format("%s val", parameter.getTypeClassName())))
        .content(
            parameter -> {
              final String condition =
                  parameter
                      .getJavaType()
                      .getConstraints()
                      .onPatternFn(pattern -> pattern.getPatternEscaped(JavaEscaper::escape))
                      .map(pattern -> "PATTERN.matcher(val).matches()")
                      .orElse("true");

              return String.format("return %s;", condition);
            })
        .build()
        .appendNewLine();
  }

  private <T> Generator<JavaParameter, T> printMatchesMinMaxLimits() {
    return printMatchesLimits(
        constraints -> constraints.onMinFn(min -> "MIN <= val"),
        constraints -> constraints.onMaxFn(max -> "val <= MAX"));
  }

  private <T> Generator<JavaParameter, T> printMatchesDecMinMaxLimits() {
    final Function<Constraints, Optional<String>> genMinCondition =
        constraints ->
            constraints.onDecimalMinFn(min -> min.isInclusiveMin() ? "MIN <= val" : "MIN < val");
    final Function<Constraints, Optional<String>> genMaxCondition =
        constraints ->
            constraints.onDecimalMaxFn(max -> max.isInclusiveMax() ? "val <= MAX" : "val < MAX");
    return printMatchesLimits(genMinCondition, genMaxCondition);
  }

  private <T> Generator<JavaParameter, T> printMatchesSizeLimits() {
    final Function<Constraints, Optional<String>> genMinCondition =
        constraints ->
            constraints
                .onSizeFn(Size::getMin)
                .flatMap(Function.identity())
                .map(min -> "MIN_LENGTH <= val.length()");
    final Function<Constraints, Optional<String>> genMaxCondition =
        constraints ->
            constraints
                .onSizeFn(Size::getMax)
                .flatMap(Function.identity())
                .map(max -> "val.length() <= MAX_LENGTH");
    return printMatchesLimits(genMinCondition, genMaxCondition);
  }

  private <T> Generator<JavaParameter, T> printMatchesLimits(
      Function<Constraints, Optional<String>> genMinCondition,
      Function<Constraints, Optional<String>> genMaxCondition) {
    return MethodGenBuilder.<JavaParameter, T>create()
        .modifiers(PUBLIC, STATIC)
        .noGenericTypes()
        .returnType("boolean")
        .methodName("matchesLimits")
        .arguments(parameter -> PList.single(String.format("%s val", parameter.getTypeClassName())))
        .content(
            parameter -> {
              final Constraints constraints = parameter.getJavaType().getConstraints();
              final Optional<String> minCondition = genMinCondition.apply(constraints);
              final Optional<String> maxCondition = genMaxCondition.apply(constraints);
              final String condition =
                  PList.of(minCondition, maxCondition)
                      .flatMapOptional(Function.identity())
                      .reduce((a, b) -> a + " && " + b)
                      .orElse("true");
              return String.format("return %s;", condition);
            })
        .build()
        .appendNewLine();
  }
}
