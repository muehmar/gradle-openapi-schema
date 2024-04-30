package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ConversionMethod {
  private final FactoryMethodConversion factoryMethodConversion;
  private final InstanceMethodConversion instanceMethodConversion;

  private ConversionMethod(
      FactoryMethodConversion factoryMethodConversion,
      InstanceMethodConversion instanceMethodConversion) {
    this.factoryMethodConversion = factoryMethodConversion;
    this.instanceMethodConversion = instanceMethodConversion;
  }

  public static ConversionMethod ofString(String conversionMethod) {
    return FactoryMethodConversion.fromString(conversionMethod)
        .map(ConversionMethod::ofFactoryMethod)
        .orElseGet(
            () ->
                ConversionMethod.ofInstanceMethod(
                    InstanceMethodConversion.ofString(conversionMethod)));
  }

  static ConversionMethod ofFactoryMethod(FactoryMethodConversion factoryMethodConversion) {
    return new ConversionMethod(factoryMethodConversion, null);
  }

  static ConversionMethod ofInstanceMethod(InstanceMethodConversion instanceMethodConversion) {
    return new ConversionMethod(null, instanceMethodConversion);
  }

  public <T> T fold(
      Function<FactoryMethodConversion, T> onFactoryMethod,
      Function<InstanceMethodConversion, T> onInstanceMethod) {
    if (factoryMethodConversion != null) {
      return onFactoryMethod.apply(factoryMethodConversion);
    } else {
      return onInstanceMethod.apply(instanceMethodConversion);
    }
  }
}
