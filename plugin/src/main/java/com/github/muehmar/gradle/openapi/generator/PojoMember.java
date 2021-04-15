package com.github.muehmar.gradle.openapi.generator;

import ch.bluecare.commons.data.PList;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

public class PojoMember {
  private final String key;
  private final String description;
  private final Type type;
  private final boolean nullable;

  public PojoMember(String key, String description, Type type, boolean nullable) {
    this.key = key;
    this.description = Optional.ofNullable(description).orElse("");
    this.type = type;
    this.nullable = nullable;
  }

  public String getDescription() {
    return description;
  }

  public String getTypeName(Resolver resolver) {
    return type.isEnum() ? resolver.enumName(key) : type.getFullName();
  }

  public Type getType() {
    return type;
  }

  public String getKey() {
    return key;
  }

  public boolean isNullable() {
    return nullable;
  }

  public boolean isRequired() {
    return !isNullable();
  }

  public String getterName(Resolver resolver) {
    return resolver.getterName(key, type);
  }
  /**
   * The provided {@code code} is executed in case this type is an enum with the list of members in
   * the enum as arguments.
   */
  public void onEnum(Consumer<PList<String>> code) {
    type.onEnum(code);
  }

  public String setterName(Resolver resolver) {
    return resolver.setterName(key);
  }

  public String witherName(Resolver resolver) {
    return resolver.witherName(key);
  }

  public String memberName(Resolver resolver) {
    return resolver.memberName(key);
  }

  public PList<String> getImports() {
    return type.getImports();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PojoMember that = (PojoMember) o;
    return nullable == that.nullable
        && Objects.equals(key, that.key)
        && Objects.equals(description, that.description)
        && Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, description, type, nullable);
  }

  @Override
  public String toString() {
    return "PojoMember{"
        + "key='"
        + key
        + '\''
        + ", description='"
        + description
        + '\''
        + ", type="
        + type
        + ", nullable="
        + nullable
        + '}';
  }
}
