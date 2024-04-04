package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.birthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.byteArrayMember;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.promotion.PojoPromotionResult;
import com.github.muehmar.gradle.openapi.generator.java.model.promotion.PromotableMembers;
import org.junit.jupiter.api.Test;

class JavaCompositionTest {

  private static final PromotableMembers NO_PROMOTABLE_MEMBERS =
      PromotableMembers.fromPojo(JavaPojos.objectPojo());

  @Test
  void
      getMembers_when_calledForPojosWithSameMembers_then_resultingMembersAreUniqueAndTheLeastRestrictive() {
    final JavaObjectPojo pojo1 =
        JavaPojos.objectPojo(requiredString(), birthdate(REQUIRED, NULLABLE));
    final JavaObjectPojo pojo2 =
        JavaPojos.objectPojo(
            requiredString().withNecessity(OPTIONAL), requiredBirthdate(), byteArrayMember());

    final JavaComposition javaComposition = new JavaComposition(NonEmptyList.of(pojo1, pojo2));

    // method call
    final JavaPojoMembers members = javaComposition.getMembers(JavaPojoMember::asOneOfMember);

    final PList<JavaPojoMember> expectedMembers =
        PList.of(
                requiredString().withNecessity(OPTIONAL),
                birthdate(REQUIRED, NULLABLE),
                byteArrayMember())
            .map(JavaPojoMember::asOneOfMember);

    assertEquals(expectedMembers.toHashSet(), members.asList().toHashSet());
  }

  @Test
  void promote_when_noPromotableMembers_then_noNewPojosAndCompositionUnchanged() {
    final JavaObjectPojo pojo1 =
        JavaPojos.objectPojo(requiredString(), birthdate(REQUIRED, NULLABLE));
    final JavaObjectPojo pojo2 =
        JavaPojos.objectPojo(
            requiredString().withNecessity(OPTIONAL), requiredBirthdate(), byteArrayMember());

    final JavaComposition javaComposition = new JavaComposition(NonEmptyList.of(pojo1, pojo2));

    // method call
    final JavaComposition.CompositionPromotionResult promotionResult =
        javaComposition.promote(JavaPojoNames.invoiceName(), ignore -> NO_PROMOTABLE_MEMBERS);

    assertEquals(PList.empty(), promotionResult.getNewPojos());
    assertEquals(javaComposition, promotionResult.getComposition());
  }

  @Test
  void promote_when_twoSubPojos_then_promotionCalledCorrectlyForBothSubPojosAndCorrectResult() {
    final JavaObjectPojo pojo1 = mock(JavaObjectPojo.class);
    final JavaObjectPojo pojo2 = mock(JavaObjectPojo.class);
    final JavaObjectPojo pojo3 = mock(JavaObjectPojo.class);
    final JavaObjectPojo pojo4 = mock(JavaObjectPojo.class);

    when(pojo1.promote(any(), any())).thenReturn(PojoPromotionResult.ofUnchangedPojo(pojo1));
    when(pojo2.promote(any(), any()))
        .thenReturn(new PojoPromotionResult(pojo3, PList.of(pojo3, pojo4)));

    final JavaComposition javaComposition = new JavaComposition(NonEmptyList.of(pojo1, pojo2));

    final PromotableMembers promotableMembers1 =
        PromotableMembers.fromPojo(JavaPojos.sampleObjectPojo1());
    final PromotableMembers promotableMembers2 =
        PromotableMembers.fromPojo(JavaPojos.sampleObjectPojo2());

    final JavaPojoName rootName = JavaPojoNames.invoiceName();

    // method call
    final JavaComposition.CompositionPromotionResult promotionResult =
        javaComposition.promote(
            rootName, pojo -> pojo.equals(pojo1) ? promotableMembers1 : promotableMembers2);

    assertEquals(
        new JavaComposition(NonEmptyList.of(pojo1, pojo3)), promotionResult.getComposition());
    assertEquals(PList.of(pojo3, pojo4), promotionResult.getNewPojos());

    verify(pojo1).promote(rootName, promotableMembers1);
    verify(pojo2).promote(rootName, promotableMembers2);
  }
}
