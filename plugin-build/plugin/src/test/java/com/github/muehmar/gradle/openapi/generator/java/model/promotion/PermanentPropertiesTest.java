package com.github.muehmar.gradle.openapi.generator.java.model.promotion;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.byteArrayMember;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredDouble;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredInteger;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.anyType;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class PermanentPropertiesTest {

  @Test
  void extractDeep_when_calledForPojo_then_permanentMembersAndAdditionalPropertiesExtracted() {
    final JavaObjectPojo allOfSubPojo =
        JavaPojos.objectPojo(requiredString(), requiredBirthdate())
            .withRequiredAdditionalProperties(PList.of(JavaRequiredAdditionalProperties.prop1()));

    final JavaObjectPojo oneOfSubPojo =
        JavaPojos.objectPojo(requiredInteger())
            .withRequiredAdditionalProperties(PList.of(JavaRequiredAdditionalProperties.prop3()));
    final JavaObjectPojo anyOfSubPojo =
        JavaPojos.objectPojo(byteArrayMember())
            .withRequiredAdditionalProperties(PList.of(JavaRequiredAdditionalProperties.prop4()));

    final JavaObjectPojo pojo =
        JavaPojos.objectPojo(requiredDouble())
            .withRequiredAdditionalProperties(PList.of(JavaRequiredAdditionalProperties.prop2()))
            .withAllOfComposition(
                Optional.of(JavaAllOfComposition.fromPojos(NonEmptyList.single(allOfSubPojo))))
            .withOneOfComposition(
                Optional.of(JavaOneOfComposition.fromPojos(NonEmptyList.single(oneOfSubPojo))))
            .withAnyOfComposition(
                Optional.of(JavaAnyOfComposition.fromPojos(NonEmptyList.single(anyOfSubPojo))));

    // method call
    final PermanentProperties permanentProperties = PermanentProperties.extractDeep(pojo);

    assertEquals(
        PList.of(requiredString(), requiredBirthdate(), requiredDouble()).toHashSet(),
        permanentProperties.getMembers().toHashSet());
    assertEquals(
        PList.of(JavaRequiredAdditionalProperties.prop1(), JavaRequiredAdditionalProperties.prop2())
            .toHashSet(),
        permanentProperties.getRequiredAdditionalProperties().toHashSet());
  }

  @Test
  void
      determinePromotableMembers_when_propertyAndRequiredPropertyWithSameNameButNotAnyType_then_noPromotableMembers() {
    final JavaRequiredAdditionalProperty requiredProperty =
        new JavaRequiredAdditionalProperty(optionalBirthdate().getName(), JavaTypes.stringType());
    final PermanentProperties permanentProperties =
        new PermanentProperties(PList.of(optionalBirthdate()), PList.of(requiredProperty));

    final PList<JavaPojoMember> promotableMembers =
        permanentProperties.determinePromotableMembers();

    assertEquals(PList.empty(), promotableMembers);
  }

  @Test
  void
      determinePromotableMembers_when_anyTypeRequiredAdditionalPropertyMatchesMember_then_correctRequiredMemberAsPromotableProperty() {
    final PList<JavaPojoMember> permanentMembers = PList.of(optionalString(), optionalBirthdate());
    final JavaRequiredAdditionalProperty requiredProperty1 =
        new JavaRequiredAdditionalProperty(optionalString().getName(), anyType());

    final PermanentProperties permanentProperties =
        new PermanentProperties(permanentMembers, PList.of(requiredProperty1));

    final PList<JavaPojoMember> promotableMembers =
        permanentProperties.determinePromotableMembers();

    assertEquals(PList.single(optionalString().withNecessity(REQUIRED)), promotableMembers);
  }
}
