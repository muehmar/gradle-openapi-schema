package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ArrayType implements Type {
  private final Constraints constraints;
  private final Type itemType;

  private ArrayType(Constraints constraints, Type itemType) {
    this.constraints = constraints;
    this.itemType = itemType;
  }

  public static ArrayType ofItemType(Type itemType) {
    return new ArrayType(Constraints.empty(), itemType);
  }

  public ArrayType withConstraints(Constraints constraints) {
    return new ArrayType(constraints, itemType);
  }

  public Type getItemType() {
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
