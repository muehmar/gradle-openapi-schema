package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;

public interface Type {
  Constraints getConstraints();

  Nullability getNullability();

  Type applyMapping(PojoNameMapping pojoNameMapping);

  Type makeNullable();

  Type replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType);

  default Type adjustNullablePojo(PojoName nullablePojo) {
    return asObjectType()
        .filter(objectType -> objectType.getName().equals(nullablePojo))
        .<Type>map(objectType -> objectType.withNullability(Nullability.NULLABLE))
        .orElse(this);
  }

  <T> T fold(
      Function<NumericType, T> onNumericType,
      Function<IntegerType, T> onIntegerType,
      Function<StringType, T> onStringType,
      Function<ArrayType, T> onArrayType,
      Function<BooleanType, T> onBooleanType,
      Function<ObjectType, T> onObjectType,
      Function<EnumType, T> onEnumType,
      Function<MapType, T> onMapType,
      Function<AnyType, T> onAnyType);

  default Type onObjectType(UnaryOperator<ObjectType> mapObjectType) {
    return fold(
        Type.class::cast,
        Type.class::cast,
        Type.class::cast,
        Type.class::cast,
        Type.class::cast,
        mapObjectType::apply,
        Type.class::cast,
        Type.class::cast,
        Type.class::cast);
  }

  default Optional<ObjectType> asObjectType() {
    return fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        Optional::of,
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }

  default boolean isObjectType() {
    return asObjectType().isPresent();
  }

  default Optional<ArrayType> asArrayType() {
    return fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        Optional::of,
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }

  default boolean isArrayType() {
    return asArrayType().isPresent();
  }

  default Optional<MapType> asMapType() {
    return fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        Optional::of,
        ignore -> Optional.empty());
  }

  default boolean isMapType() {
    return asMapType().isPresent();
  }

  default Optional<EnumType> asEnumType() {
    return fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        Optional::of,
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }

  default boolean isEnumType() {
    return asEnumType().isPresent();
  }

  default boolean isAnyType() {
    return fold(
        numericType -> false,
        integerType -> false,
        stringType -> false,
        arrayType -> false,
        booleanType -> false,
        objectType -> false,
        enumType -> false,
        mapType -> false,
        anyType -> true);
  }

  default Optional<StringType> asStringType() {
    return fold(
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        Optional::of,
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty(),
        ignore -> Optional.empty());
  }

  default boolean isStringType() {
    return asStringType().isPresent();
  }
}
