package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeConversion;
import lombok.Value;

@Value
public class ApiType {
  QualifiedClassName className;
  ParameterizedApiClassName parameterizedClassName;
  ToApiTypeConversion toApiTypeConversion;
  FromApiTypeConversion fromApiTypeConversion;

  public static ApiType fromConversion(
      QualifiedClassName className, TypeConversion conversion, PList<JavaType> generics) {
    final ParameterizedApiClassName parameterizedClassName =
        ParameterizedApiClassName.ofClassNameAndGenerics(className, generics);
    final ToApiTypeConversion toApiTypeConversion =
        new ToApiTypeConversion(ConversionMethod.ofString(className, conversion.getToCustomType()));
    final FromApiTypeConversion fromApiTypeConversion =
        new FromApiTypeConversion(
            ConversionMethod.ofString(className, conversion.getFromCustomType()));
    return new ApiType(
        className, parameterizedClassName, toApiTypeConversion, fromApiTypeConversion);
  }
}
