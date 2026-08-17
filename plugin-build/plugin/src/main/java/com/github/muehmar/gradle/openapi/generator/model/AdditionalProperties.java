package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.AdditionalPropertiesValueType;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.model.type.InlinableType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import lombok.Value;

@Value
public class AdditionalProperties {
  boolean allowed;
  AdditionalPropertiesValueType type;

  public static AdditionalProperties anyTypeAllowed() {
    return new AdditionalProperties(true, AnyType.create(NULLABLE));
  }

  public static AdditionalProperties allowed(AdditionalPropertiesValueType type) {
    return new AdditionalProperties(true, type);
  }

  public static AdditionalProperties notAllowed() {
    return new AdditionalProperties(false, AnyType.create(NULLABLE));
  }

  /**
   * Replaces the value type in case it is an {@link
   * com.github.muehmar.gradle.openapi.generator.model.type.ObjectType} with the given name. An
   * {@link InlinableType} excludes the container types in the same way as the value type does,
   * hence it is always supported as replacement.
   */
  public AdditionalProperties replaceObjectType(PojoName objectTypeName, InlinableType newType) {
    final AdditionalPropertiesValueType newValueType =
        type.asObjectType()
            .filter(objectType -> objectType.getName().equals(objectTypeName))
            .map(ignore -> newType.asAdditionalPropertiesValueType())
            .orElse(type);
    return new AdditionalProperties(allowed, newValueType);
  }

  public AdditionalProperties adjustNullablePojo(PojoName nullablePojo) {
    final AdditionalPropertiesValueType newType = type.adjustNullablePojoValueType(nullablePojo);
    return new AdditionalProperties(allowed, newType);
  }

  public AdditionalProperties applyMapping(PojoNameMapping pojoNameMapping) {
    final AdditionalPropertiesValueType newType = type.applyMappingToValueType(pojoNameMapping);
    return new AdditionalProperties(allowed, newType);
  }
}
