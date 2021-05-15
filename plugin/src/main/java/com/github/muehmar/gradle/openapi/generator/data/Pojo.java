package com.github.muehmar.gradle.openapi.generator.data;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.Resolver;
import java.util.Objects;
import java.util.Optional;

public class Pojo {
  private final Name name;
  private final String description;
  private final String suffix;
  private final PList<PojoMember> members;
  private final boolean isArray;
  private final Optional<Type> enumType;

  private Pojo(
      Name name,
      String description,
      String suffix,
      PList<PojoMember> members,
      boolean isArray,
      Optional<Type> enumType) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.suffix = suffix;
    this.members = members;
    this.isArray = isArray;
    this.enumType = enumType;
  }

  public static Pojo ofObject(
      Name name, String description, String suffix, PList<PojoMember> members) {
    return new Pojo(name, description, suffix, members, false, Optional.empty());
  }

  public static Pojo ofArray(Name name, String description, String suffix, PojoMember array) {
    return new Pojo(name, description, suffix, PList.single(array), true, Optional.empty());
  }

  public static Pojo ofEnum(Name name, String description, String suffix, Type enumType) {
    return new Pojo(name, description, suffix, PList.empty(), false, Optional.of(enumType));
  }

  public Name getName() {
    return name;
  }

  public SuffixedName className(Resolver resolver) {
    return resolver.className(name).suffix(suffix);
  }

  public String getDescription() {
    return description;
  }

  public PList<PojoMember> getMembers() {
    return members;
  }

  /** Returns true in case this pojo is a real pojo, i.e. no array and no enum. */
  public boolean isObject() {
    return !isArray() && !isEnum();
  }

  /** Returns true in case this pojo is an array-pojo. */
  public boolean isArray() {
    return isArray;
  }

  /** Returns true in case this pojo is an enum. */
  public boolean isEnum() {
    return enumType.isPresent();
  }

  public Optional<Type> getEnumType() {
    return enumType;
  }

  /**
   * Replaces a type of a member with another description and type.
   *
   * @param memberType If memberType is equally to the full name of the current type, the given
   *     description and type will be replaced in this member.
   * @param newDescription Description of the member which should be used.
   * @param newType Type of the member which should be used
   */
  public Pojo replaceMemberType(Name memberType, String newDescription, Type newType) {
    return new Pojo(
        name,
        description,
        suffix,
        members.map(member -> member.replaceMemberType(memberType, newDescription, newType)),
        isArray,
        enumType);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Pojo pojo = (Pojo) o;
    return isArray == pojo.isArray
        && Objects.equals(name, pojo.name)
        && Objects.equals(description, pojo.description)
        && Objects.equals(suffix, pojo.suffix)
        && Objects.equals(members, pojo.members)
        && Objects.equals(enumType, pojo.enumType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, suffix, members, isArray, enumType);
  }

  @Override
  public String toString() {
    return "Pojo{"
        + "name="
        + name
        + ", description='"
        + description
        + '\''
        + ", suffix='"
        + suffix
        + '\''
        + ", members="
        + members
        + ", isArray="
        + isArray
        + ", enumType="
        + enumType
        + '}';
  }
}
