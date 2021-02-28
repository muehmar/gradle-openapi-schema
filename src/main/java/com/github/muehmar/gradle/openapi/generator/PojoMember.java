package com.github.muehmar.gradle.openapi.generator;

import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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

  public String getTypeName() {
    return type.getName();
  }

  public String getKey() {
    return key;
  }

  public boolean isNullable() {
    return nullable;
  }

  public String getterName(Resolver resolver) {
    return resolver.getterName(key, type);
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
