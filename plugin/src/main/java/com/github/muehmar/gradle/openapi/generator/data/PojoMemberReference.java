package com.github.muehmar.gradle.openapi.generator.data;

import java.util.Objects;

/** A schema description which is a definition of a member used as reference. */
public class PojoMemberReference {
  private final Name name;
  private final String description;
  private final Type type;

  public PojoMemberReference(Name name, String description, Type type) {
    this.name = name;
    this.description = description;
    this.type = type;
  }

  public Name getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public Type getType() {
    return type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PojoMemberReference that = (PojoMemberReference) o;
    return Objects.equals(name, that.name)
        && Objects.equals(description, that.description)
        && Objects.equals(type, that.type);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, type);
  }

  @Override
  public String toString() {
    return "PojoMemberReference{"
        + "name="
        + name
        + ", description='"
        + description
        + '\''
        + ", type="
        + type
        + '}';
  }
}
