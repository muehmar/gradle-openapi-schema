package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ObjectType implements Type {
  private final PojoName name;

  private ObjectType(PojoName name) {
    this.name = name;
  }

  public static ObjectType ofName(PojoName name) {
    return new ObjectType(name);
  }

  public PojoName getName() {
    return name;
  }

  @Override
  public ObjectType applyMapping(PojoNameMapping pojoNameMapping) {
    return new ObjectType(pojoNameMapping.map(name));
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
    return onObjectType.apply(this);
  }
}
