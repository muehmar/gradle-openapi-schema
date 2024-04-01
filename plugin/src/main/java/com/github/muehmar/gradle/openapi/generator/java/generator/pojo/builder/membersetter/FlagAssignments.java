package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.membersetter;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;

public class FlagAssignments {
  private FlagAssignments() {}

  public static String requiredNullableFlagAssignment(JavaPojoMember member) {
    return String.format("this.%s = true;", member.getIsPresentFlagName());
  }

  public static String optionalNotNullableFlagAssignment(JavaPojoMember member) {
    return String.format("this.%s = %s != null;", member.getIsNotNullFlagName(), member.getName());
  }

  public static String optionalNullableFlagAssignment(JavaPojoMember member) {
    return String.format("this.%s = %s == null;", member.getIsNullFlagName(), member.getName());
  }

  public static String wrappedOptionalNullableFlagAssignment(JavaPojoMember member) {
    return String.format(
        "this.%s = %s.%s;",
        member.getIsNullFlagName(), member.getName(), member.tristateToIsNullFlag());
  }
}
