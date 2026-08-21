package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;

public class ApiTypes {
  private ApiTypes() {}

  public static ApiType userId() {
    final QualifiedClassName qualifiedClassName =
        QualifiedClassName.ofQualifiedClassName("com.github.muehmar.UserId");
    return ApiType.ofUserDefinedType(
        new UserDefinedApiType(
            QualifiedClassNames.STRING,
            ParameterizedApiClassName.ofClassNameAndGenerics(qualifiedClassName),
            new ToApiTypeConversion(
                ConversionMethod.ofString(
                    qualifiedClassName, "com.github.muehmar.UserId#fromString")),
            new FromApiTypeConversion(
                ConversionMethod.ofString(
                    qualifiedClassName, "com.github.muehmar.UserId#toString"))));
  }

  public static ApiType counter() {
    final QualifiedClassName qualifiedClassName =
        QualifiedClassName.ofQualifiedClassName("com.github.muehmar.Counter");
    return ApiType.ofUserDefinedType(
        new UserDefinedApiType(
            QualifiedClassNames.LONG,
            ParameterizedApiClassName.ofClassNameAndGenerics(qualifiedClassName),
            new ToApiTypeConversion(ConversionMethod.ofString(qualifiedClassName, "toCounter")),
            new FromApiTypeConversion(ConversionMethod.ofString(qualifiedClassName, "toLong"))));
  }

  public static ApiType pluginApiType() {
    return ApiType.ofPluginType(PluginApiType.useSetForListType(JavaTypes.stringType()));
  }

  public static ApiType pluginApiTypeAndUserMapping() {
    final PluginApiType pluginApiType = PluginApiType.useSetForListType(JavaTypes.stringType());
    final QualifiedClassName userClassName =
        QualifiedClassName.ofQualifiedClassName("com.github.muehmar.CustomSet");
    final UserDefinedApiType userDefinedApiType =
        new UserDefinedApiType(
            QualifiedClassNames.SET,
            ParameterizedApiClassName.ofClassNameAndGenerics(
                userClassName, PList.single(JavaTypes.stringType())),
            new ToApiTypeConversion(
                ConversionMethod.ofString(userClassName, "com.github.muehmar.CustomSet#of")),
            new FromApiTypeConversion(ConversionMethod.ofString(userClassName, "toSet")));
    return ApiType.of(userDefinedApiType, java.util.Optional.of(pluginApiType));
  }
}
