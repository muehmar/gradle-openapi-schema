package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;

public class ApiTypes {
  private ApiTypes() {}

  public static ApiType userId() {
    final QualifiedClassName qualifiedClassName =
        QualifiedClassName.ofQualifiedClassName("com.github.muehmar.UserId");
    return new ApiType(
        QualifiedClassNames.STRING,
        ParameterizedApiClassName.ofClassNameAndGenerics(qualifiedClassName),
        new ToApiTypeConversion(
            ConversionMethod.ofString(qualifiedClassName, "com.github.muehmar.UserId#fromString")),
        new FromApiTypeConversion(
            ConversionMethod.ofString(qualifiedClassName, "com.github.muehmar.UserId#toString")));
  }

  public static ApiType counter() {
    final QualifiedClassName qualifiedClassName =
        QualifiedClassName.ofQualifiedClassName("com.github.muehmar.Counter");
    return new ApiType(
        QualifiedClassNames.LONG,
        ParameterizedApiClassName.ofClassNameAndGenerics(qualifiedClassName),
        new ToApiTypeConversion(ConversionMethod.ofString(qualifiedClassName, "toCounter")),
        new FromApiTypeConversion(ConversionMethod.ofString(qualifiedClassName, "toLong")));
  }
}
