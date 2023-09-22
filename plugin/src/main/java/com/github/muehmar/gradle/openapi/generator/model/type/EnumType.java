package com.github.muehmar.gradle.openapi.generator.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class EnumType implements Type {
  private final Name name;
  private final PList<String> members;
  private final Optional<String> format;

  private EnumType(Name name, PList<String> members, Optional<String> format) {
    this.name = name;
    this.members = members;
    this.format = format;
  }

  public static EnumType ofNameAndMembersAndFormat(
      Name name, PList<String> members, String format) {
    return new EnumType(name, members, Optional.of(format));
  }

  public static EnumType ofNameAndMembersAndFormat(
      Name name, PList<String> members, Optional<String> format) {
    return new EnumType(name, members, format);
  }

  public static EnumType ofNameAndMembers(Name name, PList<String> members) {
    return new EnumType(name, members, Optional.empty());
  }

  public Name getName() {
    return name;
  }

  public PList<String> getMembers() {
    return members;
  }

  public Optional<String> getFormat() {
    return format;
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
