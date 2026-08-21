package com.github.muehmar.gradle.openapi.generator.java.model.name;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import java.util.Objects;

/** Represents a name of a local variable in a method. */
public class LocalVariableName {
  private final String name;
  private final PList<String> otherLocalNames;

  private LocalVariableName(String name, PList<String> otherLocalNames) {
    this.name = name;
    this.otherLocalNames = otherLocalNames;
  }

  public static LocalVariableName of(String name) {
    return new LocalVariableName(name, PList.empty());
  }

  public LocalVariableName withPojoMemberAsMethodArgument(JavaPojoMember member) {
    return new LocalVariableName(name, otherLocalNames.add(member.getName().asString()));
  }

  public String asString() {
    return escape(name, otherLocalNames);
  }

  private static String escape(String name, PList<String> otherLocalNames) {
    if (otherLocalNames.exists(name::equals)) {
      return escape(name + "_", otherLocalNames);
    }
    return name;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    final LocalVariableName that = (LocalVariableName) o;
    return Objects.equals(asString(), that.asString());
  }

  @Override
  public int hashCode() {
    return Objects.hash(asString());
  }

  @Override
  public String toString() {
    return asString();
  }
}
