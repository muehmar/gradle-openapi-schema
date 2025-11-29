package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
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

  public static PluginApiType useEnumAsApiType(QualifiedClassName enumClassName) {
    final ParameterizedApiClassName parameterizedApiClassName =
        ParameterizedApiClassName.ofClassNameAndGenerics(enumClassName);
    return new PluginApiType(
        enumClassName,
        parameterizedApiClassName,
        new ToApiTypeConversion(
            ConversionMethod.ofFactoryMethod(
                new FactoryMethodConversion(enumClassName, Name.ofString("valueOf")))),
        new FromApiTypeConversion(
            ConversionMethod.ofInstanceMethod(InstanceMethodConversion.ofString("name"))));
  }

  public PluginApiType replaceClassName(
      QualifiedClassName currentClassName, QualifiedClassName newClassName) {
    return new PluginApiType(
        className.replaceIfEquals(currentClassName, newClassName),
        parameterizedClassName.replaceClassName(currentClassName, newClassName),
        toApiTypeConversion.replaceClassName(currentClassName, newClassName),
        fromApiTypeConversion.replaceClassName(currentClassName, newClassName));
  }
}
