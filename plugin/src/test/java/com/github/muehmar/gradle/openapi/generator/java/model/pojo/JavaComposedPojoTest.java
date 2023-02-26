package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import org.junit.jupiter.api.Test;

class JavaComposedPojoTest {

  @Test
  void getMembers_when_pojosHaveSameMembers_then_onlyDistinctMembersReturned() {
    final JavaPojo pojo1 = JavaPojos.allNecessityAndNullabilityVariants();
    final PList<JavaPojoMember> members2 =
        pojo1.getMembersOrEmpty().take(2).cons(JavaPojoMembers.requiredBirthdate());
    final JavaObjectPojo pojo2 = JavaPojos.objectPojo(members2);
    final PList<JavaPojo> pojos = PList.of(pojo1, pojo2);
    final JavaComposedPojo javaComposedPojo =
        JavaPojos.composedPojo(pojos, ComposedPojo.CompositionType.ONE_OF);

    final PList<JavaPojoMember> composedMembers = javaComposedPojo.getMembers();

    final PList<JavaPojoMember> expectedMembers =
        pojo1.getMembersOrEmpty().add(JavaPojoMembers.requiredBirthdate());

    assertEquals(expectedMembers.toHashSet(), composedMembers.toHashSet());
  }
}
