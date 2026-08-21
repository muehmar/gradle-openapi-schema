package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames.patientName;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.promotion.PromotableMembers;
import java.util.Collections;
import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class JavaOneOfCompositionTest {

  @Test
  void getMembers_when_oneOfComposition_then_allHaveOneOfMemberType() {
    final JavaOneOfComposition composition =
        JavaOneOfComposition.fromPojos(NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()));

    final Set<JavaPojoMember.MemberType> memberTypes =
        composition.getMembers().asList().map(JavaPojoMember::getType).toHashSet();

    assertEquals(Collections.singleton(JavaPojoMember.MemberType.ONE_OF_MEMBER), memberTypes);
  }

  @Test
  void getMembers_when_oneOfComposition_then_stringValMemberOnlyOnceReturned() {
    final JavaOneOfComposition composition =
        JavaOneOfComposition.fromPojos(NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()));

    final JavaPojoMembers members = composition.getMembers();

    assertEquals(
        PList.of("birthdate", "doubleVal", "email", "intVal", "stringVal"),
        members
            .asList()
            .map(JavaPojoMember::getName)
            .map(JavaName::asString)
            .sort(Comparator.comparing(Function.identity())));
  }

  @Test
  void promote_when_called_then_correctDelegated() {
    final JavaComposition javaComposition1 = mock(JavaComposition.class);
    final JavaComposition javaComposition2 = mock(JavaComposition.class);
    final PromotableMembers promotableMembers = mock(PromotableMembers.class);

    final ArgumentCaptor<Function<JavaObjectPojo, PromotableMembers>> captor =
        ArgumentCaptor.forClass(Function.class);

    when(javaComposition1.promote(any(), captor.capture()))
        .thenReturn(
            new JavaComposition.CompositionPromotionResult(
                javaComposition2, PList.of(sampleObjectPojo1())));
    when(promotableMembers.addSubPojo(any())).thenReturn(promotableMembers);

    final JavaOneOfComposition javaOneOfComposition =
        new JavaOneOfComposition(javaComposition1, Optional.empty());

    // method call
    final JavaOneOfComposition.OneOfCompositionPromotionResult promotionResult =
        javaOneOfComposition.promote(patientName(), promotableMembers);

    captor.getValue().apply(sampleObjectPojo2());

    verify(promotableMembers).addSubPojo(sampleObjectPojo2());
    assertEquals(
        new JavaOneOfComposition(javaComposition2, Optional.empty()),
        promotionResult.getComposition());
    assertEquals(PList.single(sampleObjectPojo1()), promotionResult.getNewPojos());
  }
}
