package com.github.muehmar.gradle.openapi.generator.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import java.util.function.Function;
import lombok.Value;

@Value
@PojoBuilder
public class EnumType implements Type {
  Name name;
  PList<String> members;
  Optional<String> format;
  Nullability nullability;
  Nullability legacyNullability;

  //  public static EnumType ofNameAndMembersAndFormat(
  //      Name name, PList<String> members, String format) {
  //    return new EnumType(name, members, Optional.of(format), Nullability.NOT_NULLABLE);
  //  }

  //  public static EnumType ofNameAndMembersAndFormat(
  //      Name name, PList<String> members, Optional<String> format) {
  //    return new EnumType(name, members, format, Nullability.NOT_NULLABLE);
  //  }

  public static EnumType ofNameAndMembers(Name name, PList<String> members) {
    return new EnumType(
        name, members, Optional.empty(), Nullability.NOT_NULLABLE, Nullability.NOT_NULLABLE);
  }

  @Override
  public Constraints getConstraints() {
    return Constraints.empty();
  }

  @Override
  public EnumType applyMapping(PojoNameMapping pojoNameMapping) {
    return this;
  }

  @Override
  public Type makeNullable() {
    return new EnumType(name, members, format, Nullability.NULLABLE, Nullability.NOT_NULLABLE);
  }

  @Override
  public Type replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType) {
    return this;
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
    return onEnumType.apply(this);
  }
}
