package com.github.muehmar.gradle.openapi.generator.model.composition;

import com.github.muehmar.gradle.openapi.generator.model.type.EnumObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class DiscriminatorType {
  private final StringType stringType;
  private final EnumType enumType;
  private final EnumObjectType enumObjectType;

  private DiscriminatorType(
      StringType stringType, EnumType enumType, EnumObjectType enumObjectType) {
    this.stringType = stringType;
    this.enumType = enumType;
    this.enumObjectType = enumObjectType;
  }

  public static DiscriminatorType fromStringType(StringType stringType) {
    return new DiscriminatorType(stringType, null, null);
  }

  public static DiscriminatorType fromEnumType(EnumType enumType) {
    return new DiscriminatorType(null, enumType, null);
  }

  public static DiscriminatorType fromEnumObjectType(EnumObjectType enumObjectType) {
    return new DiscriminatorType(null, null, enumObjectType);
  }

  public <T> T fold(
      Function<StringType, T> onStringType,
      Function<EnumType, T> onEnumType,
      Function<EnumObjectType, T> onEnumObjectType) {
    if (stringType != null) {
      return onStringType.apply(stringType);
    } else if (enumType != null) {
      return onEnumType.apply(enumType);
    } else {
      return onEnumObjectType.apply(enumObjectType);
    }
  }
}
