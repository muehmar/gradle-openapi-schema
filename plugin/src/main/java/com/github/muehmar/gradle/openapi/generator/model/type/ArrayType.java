package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ArrayType implements NewType {
  private final Constraints constraints;
  private final NewType itemType;

  private ArrayType(Constraints constraints, NewType itemType) {
    this.constraints = constraints;
    this.itemType = itemType;
  }

  public NewType getItemType() {
    return itemType;
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
    return onArrayType.apply(this);
  }
}
