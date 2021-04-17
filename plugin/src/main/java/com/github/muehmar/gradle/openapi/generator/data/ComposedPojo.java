package com.github.muehmar.gradle.openapi.generator.data;

import ch.bluecare.commons.data.PList;

import java.util.Objects;

/**
 * Represents a composition of other definitions, i.e. pojos. Distinguishes between different types
 * of compositions, see {@link CompositionType}.
 */
public class ComposedPojo {
  private final CompositionType type;
  private final PList<String> pojoNames;
  private final PList<PojoMember> members;

  public enum CompositionType {
    ALL_OF,
    ANY_OF,
    ONE_OF;
  }

  public ComposedPojo(CompositionType type, PList<String> pojoNames, PList<PojoMember> members) {
    this.type = type;
    this.pojoNames = pojoNames;
    this.members = members;
  }

  public CompositionType getType() {
    return type;
  }

  public PList<String> getPojoNames() {
    return pojoNames;
  }

  public PList<PojoMember> getMembers() {
    return members;
  }

  @Override
  public boolean equals(Object o) {

    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ComposedPojo that = (ComposedPojo) o;
    return type == that.type
        && Objects.equals(pojoNames, that.pojoNames)
        && Objects.equals(members, that.members);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, pojoNames, members);
  }

  @Override
  public String toString() {
    return "ComposedPojo{"
        + "type="
        + type
        + ", pojoNames="
        + pojoNames
        + ", members="
        + members
        + '}';
  }
}
