package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.Optional;
import java.util.function.Function;

public interface ObjectType extends AdditionalPropertiesValueType, InlinableType {

  PojoName getName();

  @Override
  ObjectType applyMapping(PojoNameMapping pojoNameMapping);

  @Override
  default <T> T foldInlinableType(
      Function<NumericType, T> onNumericType,
      Function<IntegerType, T> onIntegerType,
      Function<StringType, T> onStringType,
      Function<BooleanType, T> onBooleanType,
      Function<ObjectType, T> onObjectType,
      Function<EnumType, T> onEnumType,
      Function<AnyType, T> onAnyType) {
    return onObjectType.apply(this);
  }

  @Override
  default <T> T foldAdditionalPropertiesValueType(
      Function<NumericType, T> onNumericType,
      Function<IntegerType, T> onIntegerType,
      Function<StringType, T> onStringType,
      Function<BooleanType, T> onBooleanType,
      Function<ObjectType, T> onObjectType,
      Function<EnumType, T> onEnumType,
      Function<AnyType, T> onAnyType) {
    return onObjectType.apply(this);
  }

  @Override
  ObjectType makeNullable();

  ObjectType withNullability(Nullability nullability);

  Optional<EnumObjectType> asEnumObjectType();

  default ObjectType replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, InlinableType newType) {
    return this;
  }

  <T> T fold(
      Function<StandardObjectType, T> onStandardObjectType,
      Function<EnumObjectType, T> onEnumObjectType);
}
