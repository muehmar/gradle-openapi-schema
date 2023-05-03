package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Pojos;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
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

  @Test
  void new_when_pojosHaveMembersWithSameNameButDifferentAttributes_then_throwsException() {
    final JavaObjectPojo pojo1 =
        JavaPojos.objectPojo(
            PList.single(JavaPojoMembers.birthdate(Necessity.REQUIRED, Nullability.NOT_NULLABLE)));
    final JavaObjectPojo pojo2 =
        JavaPojos.objectPojo(
            PList.single(JavaPojoMembers.birthdate(Necessity.OPTIONAL, Nullability.NOT_NULLABLE)));

    final PList<JavaPojo> pojos = PList.of(pojo1, pojo2);
    assertThrows(
        IllegalArgumentException.class,
        () -> JavaPojos.composedPojo(pojos, ComposedPojo.CompositionType.ONE_OF));
  }

  @Test
  void wrap_when_onlyDefaultPropertyScope_then_onlyDefaultPojoTypeCreated() {
    final PojoMember pojoMember = PojoMembers.requiredString(PropertyScope.DEFAULT);
    final ObjectPojo objectPojo = Pojos.objectPojo(PList.single(pojoMember));

    final UnresolvedComposedPojo unresolvedComposedPojo =
        new UnresolvedComposedPojo(
            PojoName.ofNameAndSuffix("Composed", "Dto"),
            "Description",
            UnresolvedComposedPojo.CompositionType.ANY_OF,
            PList.of(objectPojo.getName()),
            Constraints.empty(),
            Optional.empty());
    final ComposedPojo composedPojo =
        ComposedPojo.resolvedOneOf(PList.of(objectPojo), unresolvedComposedPojo);

    final NonEmptyList<JavaComposedPojo> pojos =
        JavaComposedPojo.wrap(composedPojo, TypeMappings.empty());

    assertEquals(1, pojos.size());
    assertEquals("ComposedDto", pojos.head().getClassName().asString());
    assertEquals(PojoType.DEFAULT, pojos.head().getType());
  }

  @Test
  void
      wrap_when_nonDefaultPropertyScope_then_threePojoTypesCreatedWithCorrectMembersAndJavaPojos() {
    final PojoMember pojoMember1 = PojoMembers.requiredString(PropertyScope.READ_ONLY);
    final PojoMember pojoMember2 = PojoMembers.requiredBirthdate(PropertyScope.WRITE_ONLY);
    final ObjectPojo objectPojo = Pojos.objectPojo(PList.of(pojoMember1, pojoMember2));

    final ComposedPojo composedPojo = Pojos.composedAnyOf(PList.single(objectPojo));

    final NonEmptyList<JavaComposedPojo> pojos =
        JavaComposedPojo.wrap(composedPojo, TypeMappings.empty());

    assertEquals(3, pojos.size());

    final Optional<JavaComposedPojo> defaultPojo =
        pojos.toPList().find(pojo -> pojo.getType().equals(PojoType.DEFAULT));
    final Optional<JavaComposedPojo> responsePojo =
        pojos.toPList().find(pojo -> pojo.getType().equals(PojoType.RESPONSE));
    final Optional<JavaComposedPojo> requestPojo =
        pojos.toPList().find(pojo -> pojo.getType().equals(PojoType.REQUEST));

    assertTrue(defaultPojo.isPresent());
    assertTrue(requestPojo.isPresent());
    assertTrue(responsePojo.isPresent());

    assertEquals(2, defaultPojo.get().getMembers().size());
    assertEquals(1, requestPojo.get().getMembers().size());
    assertEquals(1, responsePojo.get().getMembers().size());
    assertEquals(pojoMember1.getName(), responsePojo.get().getMembers().head().getName().asName());
    assertEquals(pojoMember2.getName(), requestPojo.get().getMembers().head().getName().asName());

    assertEquals("ComposedAnyOfDto", defaultPojo.get().getClassName().asString());
    assertEquals(
        "ObjectPojoDto",
        defaultPojo.get().getJavaPojos().map(JavaPojo::getClassName).mkString(","));

    assertEquals("ComposedAnyOfResponseDto", responsePojo.get().getClassName().asString());
    assertEquals(
        "ObjectPojoResponseDto",
        responsePojo.get().getJavaPojos().map(JavaPojo::getClassName).mkString(","));

    assertEquals("ComposedAnyOfRequestDto", requestPojo.get().getClassName().asString());
    assertEquals(
        "ObjectPojoRequestDto",
        requestPojo.get().getJavaPojos().map(JavaPojo::getClassName).mkString(","));
  }
}
