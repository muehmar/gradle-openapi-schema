package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Could either represent an array type or an object type with properties. */
@EqualsAndHashCode
@ToString
public class StandardObjectType implements ObjectType {
  private final PojoName name;
  private final Nullability nullability;

  private StandardObjectType(PojoName name, Nullability nullability) {
    this.name = name;
    this.nullability = nullability;
  }

  public static StandardObjectType ofName(PojoName name) {
    return new StandardObjectType(name, Nullability.NOT_NULLABLE);
  }

  public PojoName getName() {
    return name;
  }

  @Override
  public StandardObjectType applyMapping(PojoNameMapping pojoNameMapping) {
    return new StandardObjectType(pojoNameMapping.map(name), nullability);
  }

  @Override
  public Type makeNullable() {
    return withNullability(Nullability.NULLABLE);
  }

  @Override
  public StandardObjectType withNullability(Nullability nullability) {
    return new StandardObjectType(name, nullability);
  }

  @Override
  public Optional<EnumObjectType> asEnumObjectType() {
    return Optional.empty();
  }

  @Override
  public Constraints getConstraints() {
    return Constraints.empty();
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
      Function<AnyType, T> onAnyType) {
    return onObjectType.apply(this);
  }
}
