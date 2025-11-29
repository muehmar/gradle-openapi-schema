package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import java.util.function.Function;
import lombok.Value;

@Value
public class FromApiTypeConversion {
  ConversionMethod conversionMethod;

  public FromApiTypeConversion(ConversionMethod conversionMethod) {
    this.conversionMethod = conversionMethod;
  }

  public <T> T fold(
      Function<FactoryMethodConversion, T> onFactoryMethod,
      Function<InstanceMethodConversion, T> onInstanceMethod,
      Function<ConstructorConversion, T> onConstructor) {
    return conversionMethod.fold(onFactoryMethod, onInstanceMethod, onConstructor);
  }

  public FromApiTypeConversion replaceClassName(
      QualifiedClassName currentClassName, QualifiedClassName newClassName) {
    return new FromApiTypeConversion(
        conversionMethod.replaceClassName(currentClassName, newClassName));
  }
}
