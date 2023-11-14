package com.github.muehmar.gradle.openapi.generator.java.model.member;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredEmail;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredInteger;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import org.junit.jupiter.api.Test;

class JavaPojoMembersTest {

  @Test
  void add_when_calledMultipleTimesForDifferentMembers_then_allAdded() {
    final JavaPojoMembers pojoMembers =
        JavaPojoMembers.empty()
            .add(requiredEmail())
            .add(requiredInteger())
            .add(optionalBirthdate());

    assertEquals(
        PList.of(requiredEmail(), requiredInteger(), optionalBirthdate()), pojoMembers.asList());
  }

  @Test
  void fromList_when_calledMultipleTimesForDifferentMembers_then_allAdded() {
    final JavaPojoMembers pojoMembers =
        JavaPojoMembers.fromList(PList.of(requiredEmail(), requiredInteger(), optionalBirthdate()));

    assertEquals(
        PList.of(requiredEmail(), requiredInteger(), optionalBirthdate()), pojoMembers.asList());
  }

  @Test
  void add_when_calledMultipleTimesForSameMembers_then_distinctAdded() {
    final JavaPojoMembers pojoMembers =
        JavaPojoMembers.empty()
            .add(requiredEmail())
            .add(requiredEmail())
            .add(requiredInteger())
            .add(optionalBirthdate())
            .add(requiredInteger());

    assertEquals(
        PList.of(requiredEmail(), requiredInteger(), optionalBirthdate()), pojoMembers.asList());
  }

  @Test
  void fromList_when_calledMultipleTimesForSameMembers_then_distinctAdded() {
    final JavaPojoMembers pojoMembers =
        JavaPojoMembers.fromList(
            PList.of(
                requiredEmail(),
                requiredEmail(),
                requiredInteger(),
                optionalBirthdate(),
                requiredInteger()));

    assertEquals(
        PList.of(requiredEmail(), requiredInteger(), optionalBirthdate()), pojoMembers.asList());
  }

  @Test
  void
      add_when_calledForSameMemberButDifferentNullabilityAndNecessity_then_addedWithLeastRestrictions() {
    final JavaPojoMember member1 =
        requiredEmail().withNecessity(REQUIRED).withNullability(NOT_NULLABLE);
    final JavaPojoMember member2 =
        requiredInteger().withNecessity(REQUIRED).withNullability(NOT_NULLABLE);
    final JavaPojoMembers pojoMembers =
        JavaPojoMembers.empty()
            .add(member1)
            .add(member1.withNecessity(OPTIONAL))
            .add(member1.withNullability(NULLABLE))
            .add(member2.withNecessity(OPTIONAL))
            .add(member2);

    assertEquals(
        PList.of(
            member1.withNecessity(OPTIONAL).withNullability(NULLABLE),
            member2.withNecessity(OPTIONAL)),
        pojoMembers.asList());
  }

  @Test
  void hasRequiredMembers_when_containsOnlyOptionalMembers_then_false() {
    final JavaPojoMembers pojoMembers =
        JavaPojoMembers.empty().add(optionalString()).add(optionalBirthdate());

    assertFalse(pojoMembers.hasRequiredMembers());
  }

  @Test
  void hasRequiredMembers_when_containsAtLeastOneRequiredMember_then_true() {
    final JavaPojoMembers pojoMembers =
        JavaPojoMembers.empty().add(requiredInteger()).add(optionalBirthdate());

    assertTrue(pojoMembers.hasRequiredMembers());
  }

  @Test
  void getRequiredMemberCount_when_containsOnlyOptionalMembers_then_zero() {
    final JavaPojoMembers pojoMembers =
        JavaPojoMembers.empty().add(optionalString()).add(optionalBirthdate());

    assertEquals(0, pojoMembers.getRequiredMemberCount());
  }

  @Test
  void getRequiredMemberCount_when_containsTwoRequiredMembers_then_two() {
    final JavaPojoMembers pojoMembers =
        JavaPojoMembers.empty()
            .add(requiredInteger())
            .add(optionalBirthdate())
            .add(requiredString());

    assertEquals(2, pojoMembers.getRequiredMemberCount());
  }
}
