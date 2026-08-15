package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.function.Function;

/**
 * A {@link Type} which is allowed as value type of the additional properties of an object.
 *
 * <p>In contrast to {@link Type} this excludes the container types {@link ArrayType} and {@link
 * MapType}: a container as additional-property value type is mapped to a dedicated pojo (see {@code
 * AdditionalPropertiesSchema#mapAdditionalPropertiesSchema}) and referenced as {@link ObjectType},
 * hence the value type is never a container. Narrowing the type makes this invariant explicit
 * instead of relying on the mapping and allows to omit the container cases in the whole
 * additional-properties handling.
 */
public interface AdditionalPropertiesValueType extends Type {

  <T> T foldAdditionalPropertiesValueType(
      Function<NumericType, T> onNumericType,
      Function<IntegerType, T> onIntegerType,
      Function<StringType, T> onStringType,
      Function<BooleanType, T> onBooleanType,
      Function<ObjectType, T> onObjectType,
      Function<EnumType, T> onEnumType,
      Function<AnyType, T> onAnyType);

  /**
   * Same as {@link Type#applyMapping(PojoNameMapping)} but keeps the narrowed type: none of the
   * possible cases maps to a container type.
   */
  default AdditionalPropertiesValueType applyMappingToValueType(PojoNameMapping pojoNameMapping) {
    return foldAdditionalPropertiesValueType(
        numericType -> numericType.applyMapping(pojoNameMapping),
        integerType -> integerType.applyMapping(pojoNameMapping),
        stringType -> stringType.applyMapping(pojoNameMapping),
        booleanType -> booleanType.applyMapping(pojoNameMapping),
        objectType -> objectType.applyMapping(pojoNameMapping),
        enumType -> enumType.applyMapping(pojoNameMapping),
        anyType -> anyType.applyMapping(pojoNameMapping));
  }

  /**
   * Same as {@link Type#adjustNullablePojo(PojoName)} but keeps the narrowed type: only an {@link
   * ObjectType} is adjusted, which stays an {@link ObjectType}.
   */
  default AdditionalPropertiesValueType adjustNullablePojoValueType(PojoName nullablePojo) {
    return asObjectType()
        .filter(objectType -> objectType.getName().equals(nullablePojo))
        .<AdditionalPropertiesValueType>map(
            objectType -> objectType.withNullability(Nullability.NULLABLE))
        .orElse(this);
  }
}
