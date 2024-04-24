package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

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

  public PojoMember(
      Name name, String description, Type type, PropertyScope propertyScope, Necessity necessity) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.type = type;
    this.propertyScope = propertyScope;
    this.necessity = necessity;
  }

  public PojoMember replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType) {
    return type.asObjectType()
        .filter(objType -> objType.getName().equals(objectTypeName))
        .map(ignore -> withDescription(newObjectTypeDescription).withType(newObjectType))
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
    return type.getNullability().equals(Nullability.NULLABLE);
  }

  public boolean isNotNullable() {
    return not(isNullable());
  }

  public boolean isRequiredAndNullable() {
    return isRequired() && isNullable();
  }

  public boolean isRequiredAndNotNullable() {
    return isRequired() && not(isNullable());
  }

  public boolean isOptionalAndNullable() {
    return isOptional() && isNullable();
  }

  public boolean isOptionalAndNotNullable() {
    return isOptional() && not(isNullable());
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
