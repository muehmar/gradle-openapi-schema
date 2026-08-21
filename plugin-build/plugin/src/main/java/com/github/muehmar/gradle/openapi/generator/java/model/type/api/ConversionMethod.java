package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.util.OneOf3;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ConversionMethod {
  private final OneOf3<FactoryMethodConversion, InstanceMethodConversion, ConstructorConversion>
      method;

  public ConversionMethod(
      OneOf3<FactoryMethodConversion, InstanceMethodConversion, ConstructorConversion> method) {
    this.method = method;
  }

  public static ConversionMethod ofString(QualifiedClassName className, String conversionMethod) {
    return FactoryMethodConversion.fromString(className, conversionMethod)
        .map(ConversionMethod::ofFactoryMethod)
        .orElseGet(
            () ->
                ConversionMethod.ofInstanceMethod(
                    InstanceMethodConversion.ofString(conversionMethod)));
  }

  public static ConversionMethod ofFactoryMethod(FactoryMethodConversion factoryMethodConversion) {
    return new ConversionMethod(OneOf3.ofFirst(factoryMethodConversion));
  }

  public static ConversionMethod ofInstanceMethod(
      InstanceMethodConversion instanceMethodConversion) {
    return new ConversionMethod(OneOf3.ofSecond(instanceMethodConversion));
  }

  public static ConversionMethod ofConstructor(ConstructorConversion constructorConversion) {
    return new ConversionMethod(OneOf3.ofThird(constructorConversion));
  }

  public <T> T fold(
      Function<FactoryMethodConversion, T> onFactoryMethod,
      Function<InstanceMethodConversion, T> onInstanceMethod,
      Function<ConstructorConversion, T> onConstructor) {
    return method.fold(onFactoryMethod, onInstanceMethod, onConstructor);
  }
}
