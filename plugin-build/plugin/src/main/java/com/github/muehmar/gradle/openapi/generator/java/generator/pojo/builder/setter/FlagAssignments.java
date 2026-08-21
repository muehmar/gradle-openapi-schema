package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter;

import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.github.muehmar.codegenerator.Generator;
import java.util.Optional;

public class FlagAssignments {
  private FlagAssignments() {}

  /**
   * These assignments are meant to be used with the raw property, i.e. not wrapped with either
   * {@link Optional} or the Tristate class.
   */
  public static class Raw {
    public static String requiredNullableFlagAssignment(JavaPojoMember member) {
      return String.format("this.%s = true;", member.getIsPresentFlagName());
    }

    public static String optionalNotNullableFlagAssignment(JavaPojoMember member) {
      return String.format(
          "this.%s = %s != null;", member.getIsNotNullFlagName(), member.getName());
    }

    public static String optionalNullableFlagAssignment(JavaPojoMember member) {
      return String.format("this.%s = %s == null;", member.getIsNullFlagName(), member.getName());
    }
  }

  /**
   * These assignments are meant to be used with the wrapped property, i.e. wrapped with either
   * {@link Optional} or the Tristate class.
   */
  public static class Wrapped {
    public static String requiredNullableFlagAssignment(JavaPojoMember member) {
      return Raw.requiredNullableFlagAssignment(member);
    }

    public static String optionalNotNullableFlagAssignment(JavaPojoMember member) {
      return String.format("this.%s = true;", member.getIsNotNullFlagName());
    }

    public static String optionalNullableFlagAssignment(JavaPojoMember member) {
      return String.format(
          "this.%s = %s.%s;",
          member.getIsNullFlagName(), member.getName(), member.tristateToIsNullFlag());
    }
  }

  public static Optional<String> forStandardMemberSetter(JavaPojoMember member) {
    if (member.isRequiredAndNullable()) {
      return Optional.of(Raw.requiredNullableFlagAssignment(member));
    } else if (member.isOptionalAndNotNullable()) {
      return Optional.of(Raw.optionalNotNullableFlagAssignment(member));
    } else if (member.isOptionalAndNullable()) {
      return Optional.of(Raw.optionalNullableFlagAssignment(member));
    }
    return Optional.empty();
  }

  public static Generator<JavaPojoMember, PojoSettings> forStandardMemberSetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendOptional((v, s, w) -> w.println(v), FlagAssignments::forStandardMemberSetter);
  }

  public static Optional<String> forWrappedMemberSetter(JavaPojoMember member) {
    if (member.isRequiredAndNullable()) {
      return Optional.of(Wrapped.requiredNullableFlagAssignment(member));
    } else if (member.isOptionalAndNotNullable()) {
      return Optional.of(Wrapped.optionalNotNullableFlagAssignment(member));
    } else if (member.isOptionalAndNullable()) {
      return Optional.of(Wrapped.optionalNullableFlagAssignment(member));
    }
    return Optional.empty();
  }

  public static Generator<JavaPojoMember, PojoSettings> forWrappedMemberSetter() {
    return Generator.<JavaPojoMember, PojoSettings>emptyGen()
        .appendOptional((v, s, w) -> w.println(v), FlagAssignments::forWrappedMemberSetter);
  }
}
