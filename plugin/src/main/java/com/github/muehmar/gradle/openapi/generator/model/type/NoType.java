package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class NoType implements Type {

  public static NoType create() {
    return new NoType();
  }

  @Override
  public Constraints getConstraints() {
    return Constraints.empty();
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
    return onNoType.apply(this);
  }
}
