package com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NO_NULL_CHECK;
import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.apitype.ConversionGenerationMode.NULL_SAFE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.RefsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.api.ConversionMethod;
import io.github.muehmar.codegenerator.Generator;
import java.util.Optional;

class TypeConversion {
  private TypeConversion() {}

  public static Generator<PList<ConversionMethod>, Void> typeConversion(
      String variableName, ConversionGenerationMode mode) {
    return Generator.<PList<ConversionMethod>, Void>emptyGen()
        .appendConditionally(
            ((methods, settings, writer) ->
                writer.print(
                    "%s != null ? %s : null", variableName, conversion(methods, variableName))),
            method -> mode.equals(NULL_SAFE))
        .appendConditionally(
            ((methods, settings, writer) -> writer.print("%s", conversion(methods, variableName))),
            method -> mode.equals(NO_NULL_CHECK))
        .appendList(RefsGenerator.classNameRef(), TypeConversion::getConversionClassNameForImport);
  }

  private static String conversion(PList<ConversionMethod> conversionMethods, String variableName) {
    return conversionMethods.foldLeft(
        variableName, (variable, method) -> conversion(method, variable));
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
            String.format("%s.%s()", variableName, instanceMethodConversion.getMethodName()),
        constructorConversion ->
            String.format(
                "new %s%s(%s)",
                constructorConversion
                    .getConstructorClassName()
                    .orElse(constructorConversion.getReferenceClassName())
                    .getClassName(),
                constructorConversion.isGenericClass() ? "<>" : "",
                variableName));
  }

  private static PList<QualifiedClassName> getConversionClassNameForImport(
      PList<ConversionMethod> conversionMethods) {
    return conversionMethods
        .flatMapOptional(
            conversionMethod ->
                conversionMethod.fold(
                    factoryMethodConversion -> Optional.of(factoryMethodConversion.getClassName()),
                    instanceMethodConversion -> Optional.empty(),
                    constructorConversion ->
                        Optional.of(
                            constructorConversion
                                .getConstructorClassName()
                                .orElse(constructorConversion.getReferenceClassName()))))
        .filter(QualifiedClassName::usedForImport);
  }
}
