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

  public Pojo(
      Name name, String description, String suffix, PList<PojoMember> members, boolean isArray) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.suffix = suffix;
    this.members = members;
    this.isArray = isArray;
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
        && Objects.equals(name, pojo.name)
        && Objects.equals(description, pojo.description)
        && Objects.equals(suffix, pojo.suffix)
        && Objects.equals(members, pojo.members);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, suffix, members, isArray);
  }

  @Override
  public String toString() {
    return "Pojo{"
        + "name='"
        + name
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
