package com.github.muehmar.gradle.openapi.generator.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class EnumType implements Type {
  private final Name name;
  private final PList<String> members;

  private EnumType(Name name, PList<String> members) {
    this.name = name;
    this.members = members;
  }

  public static EnumType ofNameAndMembers(Name name, PList<String> members) {
    return new EnumType(name, members);
  }

  public Name getName() {
    return name;
  }

  public PList<String> getMembers() {
    return members;
  }

  @Override
  public Constraints getConstraints() {
    return Constraints.empty();
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
