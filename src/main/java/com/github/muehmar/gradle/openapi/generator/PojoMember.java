package com.github.muehmar.gradle.openapi.generator;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

public abstract class PojoMember {
  private final boolean nullable;
  private final String description;
  private final Type type;
  private final String key;

  public PojoMember(boolean nullable, String description, Type type, String key) {
    this.nullable = nullable;
    this.description = Optional.ofNullable(description).orElse("");
    this.type = type;
    this.key = key;
  }

  public String getDescription() {
    return description;
  }

  public String getTypeName(Resolver resolver) {
    return type.isEnum() ? resolver.enumName(key) : type.getName();
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
  public void onEnum(Consumer<List<String>> code) {
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

  public Set<String> getImports() {
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
        && Objects.equals(description, that.description)
        && Objects.equals(type, that.type)
        && Objects.equals(key, that.key);
  }

  @Override
  public int hashCode() {
    return Objects.hash(nullable, description, type, key);
  }

  @Override
  public String toString() {
    return "PojoMember{"
        + "nullable="
        + nullable
        + ", description='"
        + description
        + '\''
        + ", javaType="
        + type
        + ", key='"
        + key
        + '\''
        + '}';
  }
}
