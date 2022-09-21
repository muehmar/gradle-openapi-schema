package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MapType implements Type {
  private final Type key;
  private final Type value;

  private MapType(Type key, Type value) {
    this.key = key;
    this.value = value;
  }

  public static MapType ofKeyAndValueType(Type key, Type value) {
    return new MapType(key, value);
  }

  public Type getKey() {
    return key;
  }

  public Type getValue() {
    return value;
  }

  @Override
  public Constraints getConstraints() {
    return null;
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
    return onMapType.apply(this);
  }
}
