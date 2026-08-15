package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.AdditionalPropertiesValueType;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
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
   * com.github.muehmar.gradle.openapi.generator.model.type.ObjectType} with the given name. The
   * {@code newObjectType} must be supported as value type, i.e. must not be a container type: such
   * a type is mapped to a dedicated pojo and never inlined here.
   */
  public AdditionalProperties replaceObjectType(PojoName objectTypeName, Type newObjectType) {
    final AdditionalPropertiesValueType newType =
        type.asObjectType()
            .filter(objectType -> objectType.getName().equals(objectTypeName))
            .map(ignore -> asValueType(newObjectType))
            .orElse(type);
    return new AdditionalProperties(allowed, newType);
  }

  private static AdditionalPropertiesValueType asValueType(Type type) {
    if (type instanceof AdditionalPropertiesValueType) {
      return (AdditionalPropertiesValueType) type;
    }
    throw new IllegalStateException(
        String.format(
            "The type '%s' is not supported as additional-property value type. A container value type must have been replaced by a dedicated pojo before.",
            type));
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
