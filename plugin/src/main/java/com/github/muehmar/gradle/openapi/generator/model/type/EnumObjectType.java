package com.github.muehmar.gradle.openapi.generator.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class EnumObjectType implements ObjectType {
  private final PojoName name;
  private final PList<String> members;

  public EnumObjectType(PojoName name, PList<String> members) {
    this.name = name;
    this.members = members;
  }

  public static EnumObjectType ofEnumPojo(EnumPojo enumPojo) {
    return new EnumObjectType(enumPojo.getName().getPojoName(), enumPojo.getMembers());
  }

  public PojoName getName() {
    return name;
  }

  @Override
  public EnumObjectType withNullability(Nullability nullability) {
    return this;
  }

  @Override
  public Optional<EnumObjectType> asEnumObjectType() {
    return Optional.of(this);
  }

  @Override
  public Constraints getConstraints() {
    return Constraints.empty();
  }

  @Override
  public Nullability getNullability() {
    return Nullability.NOT_NULLABLE;
  }

  @Override
  public EnumObjectType applyMapping(PojoNameMapping pojoNameMapping) {
    return new EnumObjectType(pojoNameMapping.map(name), members);
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
