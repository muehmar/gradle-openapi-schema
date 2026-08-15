package com.github.muehmar.gradle.openapi.generator.model.type;

import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import java.util.Optional;
import java.util.function.Function;

public interface ObjectType extends Type {

  PojoName getName();

  ObjectType withNullability(Nullability nullability);

  Optional<EnumObjectType> asEnumObjectType();

  default ObjectType replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType) {
    return this;
  }

  <T> T fold(
      Function<StandardObjectType, T> onStandardObjectType,
      Function<EnumObjectType, T> onEnumObjectType);
}
