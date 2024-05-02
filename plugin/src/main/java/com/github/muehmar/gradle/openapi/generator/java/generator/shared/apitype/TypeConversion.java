package com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NULL_SAFE;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import io.github.muehmar.codegenerator.Generator;
import java.util.Optional;

class TypeConversion {
  private TypeConversion() {}

  public static Generator<ConversionMethod, Void> typeConversion(
      String variableName, ConversionGenerationMode mode) {
    return Generator.<ConversionMethod, Void>emptyGen()
        .appendConditionally(
            ((apiType, settings, writer) ->
                writer.print(
                    "%s != null ? %s : null", variableName, conversion(apiType, variableName))),
            method -> mode.equals(NULL_SAFE))
        .appendConditionally(
            ((apiType, settings, writer) -> writer.print("%s", conversion(apiType, variableName))),
            method -> mode.equals(NO_NULL_CHECK))
        .appendOptional(RefsGenerator.classNameRef(), TypeConversion::getApiTypeOptionalFunction);
  }

  private static String conversion(ConversionMethod conversionMethod, String variableName) {
    return conversionMethod.fold(
        factoryMethodConversion ->
            String.format(
                "%s.%s(%s)",
                factoryMethodConversion.getClassName().getClassName(),
                factoryMethodConversion.getMethodName(),
                variableName),
        instanceMethodConversion ->
            String.format("%s.%s()", variableName, instanceMethodConversion.getMethodName()));
  }

  private static Optional<QualifiedClassName> getApiTypeOptionalFunction(
      ConversionMethod conversionMethod) {
    return conversionMethod.fold(
        factoryMethodConversion -> Optional.of(factoryMethodConversion.getClassName()),
        instanceMethodConversion -> Optional.empty());
  }
}
