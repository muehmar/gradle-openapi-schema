package com.github.muehmar.gradle.openapi.generator.model.pojo;

import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@PojoBuilder
public class ArrayPojo implements Pojo {
  ComponentName name;
  String description;
  Nullability nullability;
  Type itemType;
  Constraints constraints;

  public static ArrayPojo of(
      ComponentName name,
      String description,
      Nullability nullability,
      Type itemType,
      Constraints constraints) {
    return new ArrayPojo(name, description, nullability, itemType, constraints);
  }

  @Override
  public Pojo addObjectTypeDescription(PojoName objectTypeName, String description) {
    return this;
  }

  @Override
  public Pojo inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType) {
    return this;
  }

  @Override
  public ArrayPojo applyMapping(PojoNameMapping pojoNameMapping) {
    final ComponentName mappedName = name.applyPojoMapping(pojoNameMapping);
    final Type mappedItemType = itemType.applyMapping(pojoNameMapping);
    return new ArrayPojo(mappedName, description, nullability, mappedItemType, constraints);
  }

  @Override
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo) {
    return onArrayType.apply(this);
  }
}
