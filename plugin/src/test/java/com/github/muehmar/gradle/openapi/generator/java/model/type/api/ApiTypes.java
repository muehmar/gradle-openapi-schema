package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;

public class ApiTypes {
  private ApiTypes() {}

  public static ApiType userId() {
    return new ApiType(
        QualifiedClassNames.STRING,
        ParameterizedApiClassName.ofClassNameAndGenerics(
            QualifiedClassName.ofQualifiedClassName("com.github.muehmar.UserId")),
        new ToApiTypeConversion(ConversionMethod.ofString("com.github.muehmar.UserId#fromString")),
        new FromApiTypeConversion(ConversionMethod.ofString("com.github.muehmar.UserId#toString")));
  }

  public static ApiType counter() {
    return new ApiType(
        QualifiedClassNames.LONG,
        ParameterizedApiClassName.ofClassNameAndGenerics(
            QualifiedClassName.ofQualifiedClassName("com.github.muehmar.Counter")),
        new ToApiTypeConversion(ConversionMethod.ofString("toCounter")),
        new FromApiTypeConversion(ConversionMethod.ofString("toLong")));
  }
}
