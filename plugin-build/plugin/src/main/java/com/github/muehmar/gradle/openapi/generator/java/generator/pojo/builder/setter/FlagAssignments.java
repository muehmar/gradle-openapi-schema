package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.builder.setter;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberAndNameScope;
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
    public static String requiredNullableFlagAssignment(MemberAndNameScope mas) {
      return String.format("this.%s = true;", mas.getIsPresentFlagName());
    }

    public static String optionalNotNullableFlagAssignment(MemberAndNameScope mas) {
      return String.format(
          "this.%s = %s != null;", mas.getIsNotNullFlagName(), mas.getMember().getName());
    }

    public static String optionalNullableFlagAssignment(MemberAndNameScope mas) {
      return String.format(
          "this.%s = %s == null;", mas.getIsNullFlagName(), mas.getMember().getName());
    }
  }

  /**
   * These assignments are meant to be used with the wrapped property, i.e. wrapped with either
   * {@link Optional} or the Tristate class.
   */
  public static class Wrapped {
    public static String requiredNullableFlagAssignment(MemberAndNameScope mas) {
      return Raw.requiredNullableFlagAssignment(mas);
    }

    public static String optionalNotNullableFlagAssignment(MemberAndNameScope mas) {
      return String.format("this.%s = true;", mas.getIsNotNullFlagName());
    }

    public static String optionalNullableFlagAssignment(MemberAndNameScope mas) {
      return String.format(
          "this.%s = %s.%s;",
          mas.getIsNullFlagName(),
          mas.getMember().getName(),
          mas.getMember().tristateToIsNullFlag());
    }
  }

  public static Optional<String> forStandardMemberSetter(MemberAndNameScope mas) {
    final JavaPojoMember member = mas.getMember();
    if (member.isRequiredAndNullable()) {
      return Optional.of(Raw.requiredNullableFlagAssignment(mas));
    } else if (member.isOptionalAndNotNullable()) {
      return Optional.of(Raw.optionalNotNullableFlagAssignment(mas));
    } else if (member.isOptionalAndNullable()) {
      return Optional.of(Raw.optionalNullableFlagAssignment(mas));
    }
    return Optional.empty();
  }

  public static Generator<MemberAndNameScope, PojoSettings> forStandardMemberSetter() {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .appendOptional((v, s, w) -> w.println(v), FlagAssignments::forStandardMemberSetter);
  }

  public static Optional<String> forWrappedMemberSetter(MemberAndNameScope mas) {
    final JavaPojoMember member = mas.getMember();
    if (member.isRequiredAndNullable()) {
      return Optional.of(Wrapped.requiredNullableFlagAssignment(mas));
    } else if (member.isOptionalAndNotNullable()) {
      return Optional.of(Wrapped.optionalNotNullableFlagAssignment(mas));
    } else if (member.isOptionalAndNullable()) {
      return Optional.of(Wrapped.optionalNullableFlagAssignment(mas));
    }
    return Optional.empty();
  }

  public static Generator<MemberAndNameScope, PojoSettings> forWrappedMemberSetter() {
    return Generator.<MemberAndNameScope, PojoSettings>emptyGen()
        .appendOptional((v, s, w) -> w.println(v), FlagAssignments::forWrappedMemberSetter);
  }
}
