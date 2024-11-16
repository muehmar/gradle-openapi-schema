package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MapType implements Type {
  private final Type key;
  private final Type value;
  private final Nullability nullability;
  private final Constraints constraints;

  private MapType(Type key, Type value, Nullability nullability, Constraints constraints) {
    this.key = key;
    this.value = value;
    this.nullability = nullability;
    this.constraints = constraints;
  }

  public static MapType ofKeyAndValueType(Type key, Type value) {
    return new MapType(key, value, Nullability.NOT_NULLABLE, Constraints.empty());
  }

  public Type getKey() {
    return key;
  }

  public Type getValue() {
    return value;
  }

  public MapType withConstraints(Constraints constraints) {
    return new MapType(key, value, nullability, constraints);
  }

  public MapType withNullability(Nullability nullability) {
    return new MapType(key, value, nullability, constraints);
  }

  @Override
  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public MapType applyMapping(PojoNameMapping pojoNameMapping) {
    return new MapType(
        key.applyMapping(pojoNameMapping),
        value.applyMapping(pojoNameMapping),
        nullability,
        constraints);
  }

  @Override
  public Type makeNullable() {
    return withNullability(Nullability.NULLABLE);
  }

  @Override
  public Type replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType) {
    return value
        .asObjectType()
        .filter(objectType -> objectType.getName().equals(objectTypeName))
        .map(ignore -> new MapType(key, newObjectType, nullability, constraints))
        .orElse(this);
  }

  @Override
  public Nullability getNullability() {
    return nullability;
  }

  @Override
  public <T> T fold(
      Function<NumericType, T> onNumericType,
      Function<IntegerType, T> onIntegerType,
      Function<StringType, T> onStringType,
      Function<ArrayType, T> onArrayType,
      Function<BooleanType, T> onBooleanType,
      Function<ObjectType, T> onObjectType,
      Function<EnumType, T> onEnumType,
      Function<MapType, T> onMapType,
      Function<AnyType, T> onAnyType,
      Function<MultiType, T> onMultiType) {
    return onMapType.apply(this);
  }
}
