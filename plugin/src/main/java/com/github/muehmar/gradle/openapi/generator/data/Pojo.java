package com.github.muehmar.gradle.openapi.generator.data;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.Resolver;

import java.util.Objects;
import java.util.Optional;

public class Pojo {
  private final String key;
  private final String description;
  private final String suffix;
  private final PList<PojoMember> members;
  private final boolean isArray;

  public Pojo(
      String key, String description, String suffix, PList<PojoMember> members, boolean isArray) {
    this.key = key;
    this.description = Optional.ofNullable(description).orElse("");
    this.suffix = suffix;
    this.members = members;
    this.isArray = isArray;
  }

  public String getKey() {
    return key;
  }

  public String className(Resolver resolver) {
    return resolver.className(key + suffix);
  }

  public String getDescription() {
    return description;
  }

  public PList<PojoMember> getMembers() {
    return members;
  }

  public boolean isArray() {
    return isArray;
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
        && Objects.equals(key, pojo.key)
        && Objects.equals(description, pojo.description)
        && Objects.equals(suffix, pojo.suffix)
        && Objects.equals(members, pojo.members);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, description, suffix, members, isArray);
  }

  @Override
  public String toString() {
    return "Pojo{"
        + "key='"
        + key
        + '\''
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
        + '}';
  }
}
