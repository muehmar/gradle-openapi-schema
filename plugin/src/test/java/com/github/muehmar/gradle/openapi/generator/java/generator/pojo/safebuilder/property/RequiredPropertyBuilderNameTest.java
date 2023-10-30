package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant.FULL;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers.optionalBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers.requiredInteger;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import org.junit.jupiter.api.Test;

class RequiredPropertyBuilderNameTest {
  @Test
  void
      initial_when_standardBuilderAndPojoWithRequiredProperties_then_instanceOfRequiredPropertyBuilderNameReturned() {
    final JavaObjectPojo pojo = JavaPojos.objectPojo(requiredInteger(), optionalString());

    final BuilderName builderName = RequiredPropertyBuilderName.initial(STANDARD, pojo);

    assertTrue(builderName instanceof RequiredPropertyBuilderName);
  }

  @Test
  void
      initial_when_standardBuilderAndPojoWithoutRequiredProperties_then_instanceOfRequiredPropertyBuilderNameReturned() {
    final JavaObjectPojo pojo = JavaPojos.objectPojo(optionalBirthdate(), optionalString());

    final BuilderName builderName = RequiredPropertyBuilderName.initial(STANDARD, pojo);

    assertTrue(builderName instanceof RequiredPropertyBuilderName);
  }

  @Test
  void
      initial_when_fullBuilderAndPojoWithRequiredProperties_then_instanceOfRequiredPropertyBuilderNameReturned() {
    final JavaObjectPojo pojo = JavaPojos.objectPojo(requiredInteger(), optionalString());

    final BuilderName builderName = RequiredPropertyBuilderName.initial(FULL, pojo);

    assertTrue(builderName instanceof RequiredPropertyBuilderName);
  }

  @Test
  void
      initial_when_fullBuilderAndPojoWithoutRequiredProperties_then_instanceOfOptionalPropertyBuilderNameReturned() {
    final JavaObjectPojo pojo = JavaPojos.objectPojo(optionalBirthdate(), optionalString());

    final BuilderName builderName = RequiredPropertyBuilderName.initial(FULL, pojo);

    assertTrue(builderName instanceof OptionalPropertyBuilderName);
  }
}
