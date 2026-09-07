package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.getter.definition.AccessorProfile;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.MemberNameScope;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import java.util.function.BiFunction;
import lombok.Value;

/**
 * A member together with the names taken within the class it is generated into. Only the
 * declaration-only anchors of a member need the scope, every other name is derived from the member
 * alone. See {@link MemberNameScope}.
 *
 * <p>The dto and its builder are separate classes, hence their names are kept in separate scopes: a
 * builder method prefix of {@code get} would otherwise let the setters of a sibling rename the json
 * getter of this member.
 */
@Value
public class MemberAndNameScope {
  JavaPojoMember member;
  MemberNameScope dtoNameScope;
  MemberNameScope builderNameScope;

  /** The members of a class, each paired with the names taken within that class. */
  public static PList<MemberAndNameScope> forMembers(
      PList<JavaPojoMember> members, PojoSettings settings) {
    final MemberNameScope dtoNameScope =
        scopeOfClass(members, (profile, m) -> profile.contractGetterNames(m, settings));
    final MemberNameScope builderNameScope =
        scopeOfClass(members, (profile, m) -> profile.contractSetterNames(m, settings));
    return members.map(member -> new MemberAndNameScope(member, dtoNameScope, builderNameScope));
  }

  /** The single member of a class, e.g. the value member of an array pojo. */
  public static MemberAndNameScope singleMember(JavaPojoMember member, PojoSettings settings) {
    return forMembers(PList.single(member), settings).head();
  }

  /**
   * The contract names of the whole class, the ones of the member itself included: an anchor must
   * avoid the api of its own property just as much as the api of a sibling. A property named {@code
   * point.} - sanitized to {@code point_} - would otherwise let its validation getter collide with
   * its own api getter.
   */
  private static MemberNameScope scopeOfClass(
      PList<JavaPojoMember> members,
      BiFunction<AccessorProfile, JavaPojoMember, PList<JavaName>> contractNames) {
    final PList<JavaName> takenNames =
        members.flatMap(m -> contractNames.apply(AccessorProfile.of(m), m));
    return MemberNameScope.ofTakenNames(takenNames);
  }
}
