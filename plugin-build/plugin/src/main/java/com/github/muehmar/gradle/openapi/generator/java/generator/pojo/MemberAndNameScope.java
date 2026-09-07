package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile;
import com.github.muehmar.gradle.openapi.generator.java.model.member.FlagFieldNameScope;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MemberNameScope;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import java.util.function.BiFunction;
import lombok.Value;

/**
 * A member together with the names taken within the class it is generated into. The dto and its
 * builder are separate classes and therefore have separate scopes: a builder method prefix of
 * {@code get} would otherwise let the setters of a sibling rename this member's json getter.
 */
@Value
public class MemberAndNameScope {
  JavaPojoMember member;
  MemberNameScope dtoNameScope;
  MemberNameScope builderNameScope;
  FlagFieldNameScope flagFieldNameScope;

  /** The members of a class, each paired with the names taken within that class. */
  public static PList<MemberAndNameScope> forMembers(
      PList<JavaPojoMember> members, PojoSettings settings) {
    return forMembers(members, settings, PList.empty());
  }

  /**
   * The members of a class, each paired with the names taken within it. The {@code
   * frameworkMethodNames} are resolved before the anchors, which have to avoid them.
   */
  public static PList<MemberAndNameScope> forMembers(
      PList<JavaPojoMember> members, PojoSettings settings, PList<JavaName> frameworkMethodNames) {
    final MemberNameScope dtoNameScope =
        scopeOfClass(members, (profile, m) -> profile.contractGetterNames(m, settings))
            .add(frameworkMethodNames);
    final MemberNameScope builderNameScope =
        scopeOfClass(members, (profile, m) -> profile.contractSetterNames(m, settings));
    final FlagFieldNameScope flagFieldNameScope = FlagFieldNameScope.ofMembers(members);
    return members.map(
        member ->
            new MemberAndNameScope(member, dtoNameScope, builderNameScope, flagFieldNameScope));
  }

  public JavaName getIsPresentFlagName() {
    return member.getIsPresentFlagName(flagFieldNameScope);
  }

  public JavaName getIsNullFlagName() {
    return member.getIsNullFlagName(flagFieldNameScope);
  }

  public JavaName getIsNotNullFlagName() {
    return member.getIsNotNullFlagName(flagFieldNameScope);
  }

  /** The single member of a class, e.g. the value member of an array pojo. */
  public static MemberAndNameScope singleMember(JavaPojoMember member, PojoSettings settings) {
    return forMembers(PList.single(member), settings).head();
  }

  /**
   * The contract names of the whole class, the member's own included: an anchor must avoid the api
   * of its own property too, e.g. {@code point.} sanitized to {@code point_}.
   */
  private static MemberNameScope scopeOfClass(
      PList<JavaPojoMember> members,
      BiFunction<AccessorProfile, JavaPojoMember, PList<JavaName>> contractNames) {
    final PList<JavaName> takenNames =
        members.flatMap(m -> contractNames.apply(AccessorProfile.of(m), m));
    return MemberNameScope.ofTakenNames(takenNames);
  }
}
