package com.github.muehmar.gradle.openapi.generator.java.model.member;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MemberNameScope;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The property names taken within a single generated class, used to rename the companion flag field
 * of a property colliding with one of them. Two flags cannot collide with each other, hence only
 * the property names are taken.
 */
@EqualsAndHashCode
@ToString
public class FlagFieldNameScope {
  private static final FlagFieldNameScope EMPTY = new FlagFieldNameScope(MemberNameScope.empty());

  private final MemberNameScope nameScope;

  private FlagFieldNameScope(MemberNameScope nameScope) {
    this.nameScope = nameScope;
  }

  /** A scope without any sibling. */
  public static FlagFieldNameScope empty() {
    return EMPTY;
  }

  public static FlagFieldNameScope ofMembers(PList<JavaPojoMember> members) {
    return new FlagFieldNameScope(
        MemberNameScope.ofTakenNames(members.map(JavaPojoMember::getName)));
  }

  JavaName resolveFlagFieldName(JavaName plainName) {
    return nameScope.resolveFieldName(plainName);
  }
}
