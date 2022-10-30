package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaNumericType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

public class NumbericParameterGenerator implements Generator<Parameter, PojoSettings> {
  private final Generator<NumericParameter, PojoSettings> delegate;

  public NumbericParameterGenerator() {
    this.delegate =
        ClassGenBuilder.<NumericParameter, PojoSettings>create()
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
  public Writer generate(Parameter parameter, PojoSettings settings, Writer writer) {
    return NumericParameter.fromParameter(parameter)
        .map(intParam -> delegate.generate(intParam, settings, writer))
        .orElse(writer);
  }

  private Generator<NumericParameter, PojoSettings> content() {
    return Generator.<NumericParameter, PojoSettings>emptyGen()
        .append(this::printPrivateConstructor)
        .appendNewLine()
        .append(this::printDecMin)
        .append(this::printDecMax)
        .append(this::printDefault)
        .append(this::printDefaultAsString);
  }

  private <T> Writer printPrivateConstructor(
      NumericParameter parameter, T settings, Writer writer) {
    return writer.println("private %s() {}", parameter.getParamClassName());
  }

  private Writer printDecMin(NumericParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .numericType
        .getConstraints()
        .onDecimalMinFn(
            decMin ->
                writer.println(
                    "public static final %s MIN = %s%s;",
                    parameter.getTypeClassName(), decMin.getValue(), parameter.javaSuffix()))
        .orElse(writer);
  }

  private Writer printDecMax(NumericParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .numericType
        .getConstraints()
        .onDecimalMaxFn(
            decMax ->
                writer.println(
                    "public static final %s MAX = %s%s;",
                    parameter.getTypeClassName(), decMax.getValue(), parameter.javaSuffix()))
        .orElse(writer);
  }

  private Writer printDefault(NumericParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .getDefaultValue()
        .map(
            defaultValue ->
                writer.println(
                    "public static final %s DEFAULT = %s%s;",
                    parameter.getTypeClassName(), defaultValue, parameter.javaSuffix()))
        .orElse(writer);
  }

  private Writer printDefaultAsString(
      NumericParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .getDefaultValue()
        .map(
            defaultValue ->
                writer.println("public static final String DEFAULT_STR = \"%s\";", defaultValue))
        .orElse(writer);
  }

  @Value
  private static class NumericParameter {
    Name name;
    NumericType numericType;
    Optional<Object> defaultValue;

    public static Optional<NumericParameter> fromParameter(Parameter parameter) {
      return parameter
          .getType()
          .asNumericType()
          .map(
              numericType ->
                  new NumericParameter(
                      parameter.getName(), numericType, parameter.getDefaultValue()));
    }

    public Name getTypeClassName() {
      return JavaNumericType.wrap(numericType, TypeMappings.empty()).getClassName();
    }

    public Name getParamClassName() {
      return name.startUpperCase();
    }

    public String javaSuffix() {
      if (numericType.getFormat() == NumericType.Format.FLOAT) {
        return "f";
      }
      return "";
    }
  }
}
