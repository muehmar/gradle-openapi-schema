package com.github.muehmar.gradle.openapi.generator.model;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;
import lombok.Value;

@Value
@PojoExtension
public class NewPojoMember implements NewPojoMemberExtension {
  Name name;
  String description;
  NewType type;
  Necessity necessity;
  Nullability nullability;

  public NewPojoMember(
      Name name, String description, NewType type, Necessity necessity, Nullability nullability) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.type = type;
    this.necessity = necessity;
    this.nullability = nullability;
  }

  public NewPojoMember addObjectTypeDescription(PojoName objectTypeName, String description) {
    return type.asObjectType()
        .filter(objType -> objType.getName().equals(objectTypeName))
        .map(ignore -> withDescription(description))
        .orElse(this);
  }

  public NewPojoMember inlineObjectReference(
      PojoName referenceName, String referenceDescription, NewType referenceType) {
    return type.asObjectType()
        .filter(objType -> objType.getName().equals(referenceName))
        .map(ignore -> withDescription(referenceDescription).withType(referenceType))
        .orElse(this);
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
}
