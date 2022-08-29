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

  public static ArrayType ofItemType(NewType itemType) {
    return new ArrayType(Constraints.empty(), itemType);
  }

  public ArrayType withConstraints(Constraints constraints) {
    return new ArrayType(constraints, itemType);
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
      Function<ObjectType, T> onObjectType,
      Function<EnumType, T> onEnumType,
      Function<MapType, T> onMapType,
      Function<NoType, T> onNoType) {
    return onArrayType.apply(this);
  }
}
