package com.github.muehmar.gradle.openapi.generator.model.composition;

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

  private DiscriminatorType(StringType stringType, EnumType enumType) {
    this.stringType = stringType;
    this.enumType = enumType;
  }

  public static DiscriminatorType fromStringType(StringType stringType) {
    return new DiscriminatorType(stringType, null);
  }

  public static DiscriminatorType fromEnumType(EnumType enumType) {
    return new DiscriminatorType(null, enumType);
  }

  public <T> T fold(Function<StringType, T> onStringType, Function<EnumType, T> onEnumType) {
    if (stringType != null) {
      return onStringType.apply(stringType);
    } else {
      return onEnumType.apply(enumType);
    }
  }
}
