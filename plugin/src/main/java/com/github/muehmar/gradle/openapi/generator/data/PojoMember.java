package com.github.muehmar.gradle.openapi.generator.data;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.Resolver;
import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.Value;

@Value
@PojoExtension
public class PojoMember implements PojoMemberExtension {
  Name name;
  String description;
  Type type;
  Necessity necessity;
  Nullability nullability;

  public PojoMember(
      Name name, String description, Type type, Necessity necessity, Nullability nullability) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.type = type;
    this.necessity = necessity;
    this.nullability = nullability;
  }

  public Name getTypeName(Resolver resolver) {
    return type.isEnum() ? resolver.enumName(name) : type.getFullName();
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

  public Name getterName(Resolver resolver) {
    return resolver.getterName(name, type);
  }
  /**
   * The provided {@code code} is executed in case this type is an enum with the list of members in
   * the enum as arguments.
   */
  public void onEnum(Consumer<PList<String>> code) {
    type.onEnum(code);
  }

  public Name setterName(Resolver resolver) {
    return resolver.setterName(name);
  }

  public Name witherName(Resolver resolver) {
    return resolver.witherName(name);
  }

  public Name memberName(Resolver resolver) {
    return resolver.memberName(name);
  }

  public PList<String> getImports() {
    return type.getImports();
  }

  /**
   * Replaces a type of a member with another description and type.
   *
   * @param memberType If memberType is equally to the full name of the current type, the given
   *     description and type will be replaced in this member.
   * @param newDescription Description of the member which should be used.
   * @param newType Type of the member which should be used
   */
  public PojoMember replaceMemberType(Name memberType, String newDescription, Type newType) {
    if (type.getFullName().equals(memberType)) {
      return new PojoMember(name, newDescription, newType, necessity, nullability);
    } else {
      return this;
    }
  }
}
