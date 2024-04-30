package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import java.util.function.Function;
import lombok.Value;

@Value
public class ToApiTypeConversion {
  ConversionMethod conversionMethod;

  public ToApiTypeConversion(ConversionMethod conversionMethod) {
    this.conversionMethod = conversionMethod;
  }

  public <T> T fold(
      Function<FactoryMethodConversion, T> onFactoryMethod,
      Function<InstanceMethodConversion, T> onInstanceMethod) {
    return conversionMethod.fold(onFactoryMethod, onInstanceMethod);
  }
}
