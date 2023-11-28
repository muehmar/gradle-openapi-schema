package com.github.muehmar.gradle.openapi.generator.java.model.member;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The members of a {@link JavaObjectPojo}. This container ensures, that the same property does not
 * exist twice. Adding a property which is already present will not add a new property/member, it
 * will keep the current property but maybe with lower restrictions concerning {@link Nullability}
 * and {@link Necessity}.
 */
@EqualsAndHashCode
@ToString
public class JavaPojoMembers {
  private final PList<JavaPojoMember> members;

  private JavaPojoMembers(PList<JavaPojoMember> members) {
    this.members = members;
  }

  public static JavaPojoMembers empty() {
    return new JavaPojoMembers(PList.empty());
  }

  public static JavaPojoMembers fromMembers(PList<JavaPojoMember> members) {
    return JavaPojoMembers.empty().add(members);
  }

  public JavaPojoMembers add(JavaPojoMember memberToAdd) {
    final boolean hasMemberWithSameKey =
        members.exists(member -> member.getMemberKey().equals(memberToAdd.getMemberKey()));
    if (hasMemberWithSameKey) {
      final PList<JavaPojoMember> newMembers =
          members.map(member -> member.mergeToLeastRestrictive(memberToAdd).orElse(member));
      return new JavaPojoMembers(newMembers);
    } else {
      return new JavaPojoMembers(members.add(memberToAdd));
    }
  }

  public JavaPojoMembers add(PList<JavaPojoMember> membersToAdd) {
    return membersToAdd.foldLeft(this, JavaPojoMembers::add);
  }

  public JavaPojoMembers add(JavaPojoMembers membersToAdd) {
    return add(membersToAdd.asList());
  }

  public JavaPojoMembers map(UnaryOperator<JavaPojoMember> f) {
    return new JavaPojoMembers(members.map(f));
  }

  public PList<JavaPojoMember> asList() {
    return members;
  }

  public boolean isEmpty() {
    return members.isEmpty();
  }

  public boolean hasRequiredMembers() {
    return members.exists(JavaPojoMember::isRequired);
  }

  public int getRequiredMemberCount() {
    return members.filter(JavaPojoMember::isRequired).size();
  }
}
