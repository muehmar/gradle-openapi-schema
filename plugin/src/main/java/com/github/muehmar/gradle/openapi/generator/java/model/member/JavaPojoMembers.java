package com.github.muehmar.gradle.openapi.generator.java.model.member;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * The members of a {@link JavaObjectPojo}. This container ensures, that the same property does not
 * exist twice. Adding a property which is already present will not add a new property/member, it
 * will keep the current property but maybe with lower restrictions concerning {@link Nullability}
 * and {@link Necessity}. Usually, when aggregating all members of a pojo with the members of the
 * subpojos from the compositions, the members of the pojo itself will always be equally or less
 * restrictive than the members of the subpojos after the promotion was applied.
 */
@EqualsAndHashCode
@ToString
public class JavaPojoMembers {
  private final MergeStrategy mergeStrategy;
  private final PList<JavaPojoMember> members;

  private JavaPojoMembers(MergeStrategy mergeStrategy, PList<JavaPojoMember> members) {
    this.mergeStrategy = mergeStrategy;
    this.members = members;
  }

  public static JavaPojoMembers emptyLeastRestrictive() {
    return new JavaPojoMembers(MergeStrategy.LEAST_RESTRICTIVE, PList.empty());
  }

  public static JavaPojoMembers emptyMostRestrictive() {
    return new JavaPojoMembers(MergeStrategy.MOST_RESTRICTIVE, PList.empty());
  }

  public static JavaPojoMembers leastRestrictive(PList<JavaPojoMember> members) {
    return JavaPojoMembers.emptyLeastRestrictive().add(members);
  }

  public JavaPojoMembers add(JavaPojoMember memberToAdd) {
    final boolean hasMemberWithSameKey =
        members.exists(member -> member.getMemberKey().equals(memberToAdd.getMemberKey()));
    if (hasMemberWithSameKey) {
      final BiFunction<JavaPojoMember, JavaPojoMember, Optional<JavaPojoMember>> merge =
          mergeStrategy.equals(MergeStrategy.LEAST_RESTRICTIVE)
              ? JavaPojoMember::mergeToLeastRestrictive
              : JavaPojoMember::mergeToMostRestrictive;
      final PList<JavaPojoMember> newMembers =
          members.map(member -> merge.apply(member, memberToAdd).orElse(member));
      return new JavaPojoMembers(mergeStrategy, newMembers);
    } else {
      return new JavaPojoMembers(mergeStrategy, members.add(memberToAdd));
    }
  }

  public JavaPojoMembers add(PList<JavaPojoMember> membersToAdd) {
    return membersToAdd.foldLeft(this, JavaPojoMembers::add);
  }

  public JavaPojoMembers add(JavaPojoMembers membersToAdd) {
    return add(membersToAdd.asList());
  }

  public JavaPojoMembers map(UnaryOperator<JavaPojoMember> f) {
    return new JavaPojoMembers(mergeStrategy, members.map(f));
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

  public enum MergeStrategy {
    LEAST_RESTRICTIVE,
    MOST_RESTRICTIVE
  }
}
