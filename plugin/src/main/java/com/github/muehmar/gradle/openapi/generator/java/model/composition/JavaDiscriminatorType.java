package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaEnumType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.model.composition.DiscriminatorType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;

public class JavaDiscriminatorType {
  private final JavaStringType stringType;
  private final JavaEnumType enumType;

  private JavaDiscriminatorType(JavaStringType stringType, JavaEnumType enumType) {
    this.stringType = stringType;
    this.enumType = enumType;
  }

  public static JavaDiscriminatorType fromStringType(JavaStringType stringType) {
    return new JavaDiscriminatorType(stringType, null);
  }

  public static JavaDiscriminatorType fromEnumType(JavaEnumType enumType) {
    return new JavaDiscriminatorType(null, enumType);
  }

  public static JavaDiscriminatorType wrap(DiscriminatorType discriminatorType) {
    return discriminatorType.fold(
        stringType ->
            JavaDiscriminatorType.fromStringType(
                JavaStringType.wrap(stringType, TypeMappings.empty())),
        enumType -> JavaDiscriminatorType.fromEnumType(JavaEnumType.wrap(enumType)),
        enumObjectType ->
            JavaDiscriminatorType.fromEnumType(
                JavaEnumType.wrap(
                    EnumType.ofNameAndMembers(
                        enumObjectType.getName().getName(), enumObjectType.getMembers()))));
  }

  public <T> T fold(
      Function<JavaStringType, T> onStringType, Function<JavaEnumType, T> onEnumType) {
    if (stringType != null) {
      return onStringType.apply(stringType);
    } else {
      return onEnumType.apply(enumType);
    }
  }
}
