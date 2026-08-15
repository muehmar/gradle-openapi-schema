package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MethodNames;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import lombok.Value;

@Value
public class PluginApiType {
  QualifiedClassName className;
  ParameterizedApiClassName parameterizedClassName;
  ToApiTypeConversion toApiTypeConversion;
  FromApiTypeConversion fromApiTypeConversion;

  public static PluginApiType useSetForListType(JavaType itemType) {
    final ConstructorConversion conversionForSet = ConstructorConversion.conversionForSet();
    final ConstructorConversion conversionForList = ConstructorConversion.conversionForList();
    final ParameterizedApiClassName parameterizedApiClassName =
        ParameterizedApiClassName.ofClassNameAndGenerics(
            conversionForSet.getReferenceClassName(), PList.single(itemType));
    return new PluginApiType(
        conversionForSet.getReferenceClassName(),
        parameterizedApiClassName,
        new ToApiTypeConversion(ConversionMethod.ofConstructor(conversionForSet)),
        new FromApiTypeConversion(ConversionMethod.ofConstructor(conversionForList)));
  }

  /**
   * Creates the plugin api type of a string-backed enum member: the api surface uses the generated
   * enum {@code enumClassName} while the member is internally represented as a plain {@code
   * String}. The conversions are the enum's generated methods — the static factory {@code
   * fromValue} towards the api type and the instance method {@code getValue} back to the internal
   * representation.
   */
  public static PluginApiType useEnumAsApiType(QualifiedClassName enumClassName) {
    final ParameterizedApiClassName parameterizedApiClassName =
        ParameterizedApiClassName.ofClassNameAndGenerics(enumClassName);
    return new PluginApiType(
        enumClassName,
        parameterizedApiClassName,
        new ToApiTypeConversion(
            ConversionMethod.ofFactoryMethod(
                new FactoryMethodConversion(enumClassName, MethodNames.Enum.fromValue()))),
        new FromApiTypeConversion(
            ConversionMethod.ofInstanceMethod(
                new InstanceMethodConversion(MethodNames.Enum.getValue()))));
  }
}
