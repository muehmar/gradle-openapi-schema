package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class StringType implements NewType {
  private final Format format;
  private final String formatString;
  private final Constraints constraints;

  private StringType(Format format, String formatString, Constraints constraints) {
    this.format = format;
    this.formatString = formatString;
    this.constraints = constraints;
  }

  public Format getFormat() {
    return format;
  }

  public String getFormatString() {
    return formatString;
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public <T> T fold(
      Function<NumericType, T> onNumericType,
      Function<StringType, T> onStringType,
      Function<ArrayType, T> onArrayType,
      Function<BooleanType, T> onBooleanType,
      Function<ObjectType, T> onObjectType) {
    return onStringType.apply(this);
  }

  public enum Format {
    DATE,
    DATE_TIME,
    PASSWORD,
    BYTE,
    BINARY,
    EMAIL,
    UUID,
    URI,
    HOSTNAME,
    IPV4,
    IPV6,
    OTHER
  }
}
