package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import ch.bluecare.commons.data.PList;
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
}
