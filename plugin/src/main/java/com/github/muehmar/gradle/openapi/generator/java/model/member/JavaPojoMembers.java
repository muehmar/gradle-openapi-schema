package com.github.muehmar.gradle.openapi.generator.java.model.member;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import java.util.function.UnaryOperator;

/**
 * The members of a {@link JavaObjectPojo}. This container ensures, that the same property does not
 * exist twice. Adding a property which is already present will not add a new property/member, it
 * will keep the current property but maybe with lower the restrictions concerning {@link
 * Nullability} and {@link Necessity}. Usually, when aggregating all members of a pojo with the
 * members of the subpojos from the compositions, the members of the pojo itself will always be
 * equally or less restrictive than the members of the subpojos after the promotion was applied.
 */
public class JavaPojoMembers {
  private final PList<JavaPojoMember> members;

  private JavaPojoMembers(PList<JavaPojoMember> members) {
    this.members = members;
  }

  public static JavaPojoMembers empty() {
    return new JavaPojoMembers(PList.empty());
  }

  public static JavaPojoMembers fromList(PList<JavaPojoMember> members) {
    return JavaPojoMembers.empty().add(members);
  }

  public JavaPojoMembers add(JavaPojoMember memberToAdd) {
    final boolean hasMemberWithSameKey =
        members.exists(member -> member.getMemberKey().equals(memberToAdd.getMemberKey()));
    if (hasMemberWithSameKey) {
      final PList<JavaPojoMember> newMembers =
          members.map(
              member -> {
                if (member.getMemberKey().equals(memberToAdd.getMemberKey())) {
                  final Nullability nullability =
                      Nullability.leastRestrictive(
                          member.getNullability(), memberToAdd.getNullability());
                  final Necessity necessity =
                      Necessity.leastRestrictive(member.getNecessity(), memberToAdd.getNecessity());
                  return member.withNullability(nullability).withNecessity(necessity);
                } else {
                  return member;
                }
              });
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
