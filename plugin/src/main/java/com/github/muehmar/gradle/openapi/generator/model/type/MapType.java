package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MapType implements NewType {
  private final NewType key;
  private final NewType value;

  private MapType(NewType key, NewType value) {
    this.key = key;
    this.value = value;
  }

  public static MapType ofKeyAndValueType(NewType key, NewType value) {
    return new MapType(key, value);
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
