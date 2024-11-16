package com.github.muehmar.gradle.openapi.generator.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.function.Function;

public class MultiType implements Type {
  private final Nullability nullability;
  private final PList<Type> types;

  private MultiType(Nullability nullability, PList<Type> types) {
    this.nullability = nullability;
    this.types = types;
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
  public Type applyMapping(PojoNameMapping pojoNameMapping) {
    final PList<Type> mappedTypes = types.map(type -> type.applyMapping(pojoNameMapping));
    return new MultiType(nullability, mappedTypes);
  }

  @Override
  public Type makeNullable() {
    return new MultiType(Nullability.NULLABLE, types);
  }

  @Override
  public Type replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType) {
    final PList<Type> mappedTypes =
        types.map(
            type ->
                type.replaceObjectType(objectTypeName, newObjectTypeDescription, newObjectType));
    return new MultiType(nullability, mappedTypes);
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
    return onMultiType.apply(this);
  }
}
