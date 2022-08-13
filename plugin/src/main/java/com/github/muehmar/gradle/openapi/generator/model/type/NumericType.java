package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class NumericType implements NewType {

  private final Format format;
  private final Constraints constraints;

  private NumericType(Format format, Constraints constraints) {
    this.format = format;
    this.constraints = constraints;
  }

  public Format getFormat() {
    return format;
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
    return onNumericType.apply(this);
  }

  public enum Format {
    FLOAT,
    DOUBLE,
    INTEGER,
    LONG;
  }
}
