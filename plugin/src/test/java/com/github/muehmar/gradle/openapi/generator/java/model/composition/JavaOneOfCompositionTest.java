package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Function;
import org.junit.jupiter.api.Test;

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
}
