package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.Value;
import lombok.With;

@Value
@With
@PojoBuilder
public class PojoMember {
  Name name;
  String description;
  Type type;
  PropertyScope propertyScope;
  Necessity necessity;
  Nullability nullability;

  public PojoMember(
      Name name,
      String description,
      Type type,
      PropertyScope propertyScope,
      Necessity necessity,
      Nullability nullability) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.type = type;
    this.propertyScope = propertyScope;
    this.necessity = necessity;
    this.nullability = nullability;
  }

  public PojoMember addObjectTypeDescription(PojoName objectTypeName, String description) {
    return type.asObjectType()
        .filter(objType -> objType.getName().equals(objectTypeName))
        .map(ignore -> withDescription(description))
        .orElse(this);
  }

  public PojoMember inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType) {
    return type.asObjectType()
        .filter(objType -> objType.getName().equals(referenceName))
        .map(ignore -> withDescription(referenceDescription).withType(referenceType))
        .orElse(this);
  }

  public PojoMember applyMapping(PojoNameMapping pojoNameMapping) {
    final Type newType = type.applyMapping(pojoNameMapping);
    return withType(newType);
  }

  public boolean isOptional() {
    return necessity.equals(Necessity.OPTIONAL);
  }

  public boolean isRequired() {
    return !isOptional();
  }

  public boolean isNullable() {
    return nullability.equals(Nullability.NULLABLE);
  }

  public boolean isNotNullable() {
    return !isNullable();
  }

  public boolean isRequiredAndNullable() {
    return isRequired() && isNullable();
  }

  public boolean isRequiredAndNotNullable() {
    return isRequired() && !isNullable();
  }

  public boolean isOptionalAndNullable() {
    return isOptional() && isNullable();
  }

  public boolean isOptionalAndNotNullable() {
    return isOptional() && !isNullable();
  }

  public PropertyScope getPropertyScope() {
    return propertyScope;
  }

  public boolean isReadOnlyScope() {
    return getPropertyScope().equals(PropertyScope.READ_ONLY);
  }

  public boolean isWriteOnlyScope() {
    return getPropertyScope().equals(PropertyScope.WRITE_ONLY);
  }

  public boolean isDefaultScope() {
    return getPropertyScope().equals(PropertyScope.DEFAULT);
  }
}
