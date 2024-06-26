package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import lombok.Value;

@Value
public class AdditionalProperties {
  boolean allowed;
  Type type;

  public static AdditionalProperties anyTypeAllowed() {
    return new AdditionalProperties(true, AnyType.create(NULLABLE));
  }

  public static AdditionalProperties allowed(Type type) {
    return new AdditionalProperties(true, type);
  }

  public static AdditionalProperties notAllowed() {
    return new AdditionalProperties(false, AnyType.create(NULLABLE));
  }

  public AdditionalProperties replaceObjectType(PojoName objectTypeName, Type newObjectType) {
    final Type newType =
        type.asObjectType()
            .filter(objectType -> objectType.getName().equals(objectTypeName))
            .map(ignore -> newObjectType)
            .orElse(type);
    return new AdditionalProperties(allowed, newType);
  }

  public AdditionalProperties adjustNullablePojo(PojoName nullablePojo) {
    final Type newType = type.adjustNullablePojo(nullablePojo);
    return new AdditionalProperties(allowed, newType);
  }

  public AdditionalProperties applyMapping(PojoNameMapping pojoNameMapping) {
    final Type newType = type.applyMapping(pojoNameMapping);
    return new AdditionalProperties(allowed, newType);
  }
}
