package com.github.muehmar.gradle.openapi.generator.java.generator.parameter;

import static io.github.muehmar.codegenerator.java.JavaModifier.FINAL;
import static io.github.muehmar.codegenerator.java.JavaModifier.PUBLIC;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaIntegerType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.java.ClassGenBuilder;
import io.github.muehmar.codegenerator.writer.Writer;
import java.util.Optional;
import lombok.Value;

public class IntegerParameterGenerator implements Generator<Parameter, PojoSettings> {
  private final Generator<IntegerParameter, PojoSettings> delegate;

  public IntegerParameterGenerator() {
    this.delegate =
        ClassGenBuilder.<IntegerParameter, PojoSettings>create()
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
    return IntegerParameter.fromParameter(parameter)
        .map(intParam -> delegate.generate(intParam, settings, writer))
        .orElse(writer);
  }

  private Generator<IntegerParameter, PojoSettings> content() {
    return Generator.<IntegerParameter, PojoSettings>emptyGen()
        .append(this::printPrivateConstructor)
        .appendNewLine()
        .append(this::printMin)
        .append(this::printMax)
        .append(this::printDefault)
        .append(this::printDefaultAsString);
  }

  private <T> Writer printPrivateConstructor(
      IntegerParameter parameter, T settings, Writer writer) {
    return writer.println("private %s() {}", parameter.getParamClassName());
  }

  private Writer printMin(IntegerParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .integerType
        .getConstraints()
        .onMinFn(
            min ->
                writer.println(
                    "public static final %s MIN = %s%s;",
                    parameter.getTypeClassName(), min.getValue(), parameter.javaSuffix()))
        .orElse(writer);
  }

  private Writer printMax(IntegerParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .integerType
        .getConstraints()
        .onMaxFn(
            max ->
                writer.println(
                    "public static final %s MAX = %s%s;",
                    parameter.getTypeClassName(), max.getValue(), parameter.javaSuffix()))
        .orElse(writer);
  }

  private Writer printDefault(IntegerParameter parameter, PojoSettings settings, Writer writer) {
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
      IntegerParameter parameter, PojoSettings settings, Writer writer) {
    return parameter
        .getDefaultValue()
        .map(
            defaultValue ->
                writer.println("public static final String DEFAULT_STR = \"%s\";", defaultValue))
        .orElse(writer);
  }

  @Value
  private static class IntegerParameter {
    Name name;
    IntegerType integerType;
    Optional<Object> defaultValue;

    public static Optional<IntegerParameter> fromParameter(Parameter parameter) {
      return parameter
          .getType()
          .asIntegerType()
          .map(
              integerType ->
                  new IntegerParameter(
                      parameter.getName(), integerType, parameter.getDefaultValue()));
    }

    public Name getTypeClassName() {
      return JavaIntegerType.wrap(integerType, TypeMappings.empty()).getClassName();
    }

    public Name getParamClassName() {
      return name.startUpperCase();
    }

    public String javaSuffix() {
      if (integerType.getFormat() == IntegerType.Format.LONG) {
        return "L";
      }
      return "";
    }
  }
}
