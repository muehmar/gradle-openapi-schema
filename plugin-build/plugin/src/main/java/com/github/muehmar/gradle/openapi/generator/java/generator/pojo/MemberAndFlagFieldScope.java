package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.FlagFieldNameScope;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import lombok.Value;

/**
 * A member together with the field names taken within the class it is generated into. The dto and
 * its builder share one scope, as both declare the same flag fields next to the same properties.
 */
@Value
public class MemberAndFlagFieldScope {
  JavaPojoMember member;
  FlagFieldNameScope nameScope;

  public static PList<MemberAndFlagFieldScope> fromPojo(JavaObjectPojo pojo) {
    final FlagFieldNameScope nameScope = pojo.getFlagFieldNameScope();
    return pojo.getAllMembers().map(member -> new MemberAndFlagFieldScope(member, nameScope));
  }

  /** A subset of the pojo's members, paired with the scope built from all of them. */
  public static PList<MemberAndFlagFieldScope> forMembers(
      PList<JavaPojoMember> members, JavaObjectPojo pojo) {
    final FlagFieldNameScope nameScope = pojo.getFlagFieldNameScope();
    return members.map(member -> new MemberAndFlagFieldScope(member, nameScope));
  }

  public JavaName getIsPresentFlagName() {
    return member.getIsPresentFlagName(nameScope);
  }

  public JavaName getIsNullFlagName() {
    return member.getIsNullFlagName(nameScope);
  }

  public JavaName getIsNotNullFlagName() {
    return member.getIsNotNullFlagName(nameScope);
  }
}
