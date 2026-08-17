package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Type;
import java.util.function.Function;

/**
 * A {@link Type} which is allowed to replace an {@link ObjectType} referencing a pojo, i.e. which
 * gets inlined instead of being generated as dedicated pojo (see {@code
 * MapResultResolverImpl#inlineMemberReferences}).
 *
 * <p>In contrast to {@link Type} this excludes the container types {@link ArrayType} and {@link
 * MapType}: only a schema which is not mapped to a pojo is inlined, which are the scalar schemas
 * creating a {@code PojoMemberReference} and an enum referenced as {@link EnumObjectType}. A
 * container schema is always mapped to a dedicated pojo and hence stays a reference. Narrowing the
 * type makes this invariant explicit instead of relying on the construction sites.
 *
 * <p>This is deliberately separate from {@link AdditionalPropertiesValueType} although both
 * currently exclude the same types: the invariants have different origins, this one is established
 * by the construction sites of a {@code PojoMemberReference} and the other one by the mapping of
 * the additional-properties schema, hence they can evolve independently. {@link
 * #asAdditionalPropertiesValueType()} converts between them with a total fold, which stops
 * compiling as soon as they diverge.
 */
public interface InlinableType extends Type {

  <T> T foldInlinableType(
      Function<NumericType, T> onNumericType,
      Function<IntegerType, T> onIntegerType,
      Function<StringType, T> onStringType,
      Function<BooleanType, T> onBooleanType,
      Function<ObjectType, T> onObjectType,
      Function<EnumType, T> onEnumType,
      Function<AnyType, T> onAnyType);

  /**
   * Same as {@link Type#makeNullable()} but keeps the narrowed type: none of the possible cases
   * maps to a container type.
   */
  @Override
  InlinableType makeNullable();

  /**
   * Returns this type as {@link AdditionalPropertiesValueType}. Both narrowings exclude exactly the
   * container types, hence every case is supported.
   */
  default AdditionalPropertiesValueType asAdditionalPropertiesValueType() {
    return foldInlinableType(
        numericType -> numericType,
        integerType -> integerType,
        stringType -> stringType,
        booleanType -> booleanType,
        objectType -> objectType,
        enumType -> enumType,
        anyType -> anyType);
  }
}
