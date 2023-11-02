package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant.FULL;
import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant.STANDARD;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers.optionalBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers.requiredEmail;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof.AllOfBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof.AnyOfBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof.OneOfBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.OptionalPropertyBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredPropertyBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class BuilderNameTest {

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void initial_when_simpleMapPojo_then_instanceOfOptionalPropertyBuilderNameReturned(
      SafeBuilderVariant variant) {
    final JavaObjectPojo simpleMapPojo =
        JavaPojos.simpleMapPojo(JavaAdditionalProperties.anyTypeAllowed());
    final BuilderName initial = BuilderName.initial(variant, simpleMapPojo);

    assertTrue(initial instanceof OptionalPropertyBuilderName);
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void initial_when_allOfPojo_then_instanceOfAllOfBuilderNameReturned(SafeBuilderVariant variant) {
    final JavaObjectPojo allOfPojo = JavaPojos.allOfPojo(sampleObjectPojo1(), sampleObjectPojo2());

    final BuilderName builderName = BuilderName.initial(variant, allOfPojo);

    assertTrue(builderName instanceof AllOfBuilderName);
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void initial_when_oneOfPojo_then_instanceOfOneOfBuilderNameReturned(SafeBuilderVariant variant) {
    final JavaObjectPojo oneOfPojo = JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2());

    final BuilderName builderName = BuilderName.initial(variant, oneOfPojo);

    assertTrue(builderName instanceof OneOfBuilderName);
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void initial_when_anyOfPojo_then_instanceOfAnyOfBuilderNameReturned(SafeBuilderVariant variant) {
    final JavaObjectPojo anyOfPojo = JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2());

    final BuilderName builderName = BuilderName.initial(variant, anyOfPojo);

    assertTrue(builderName instanceof AnyOfBuilderName);
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void
      initial_when_objectPojoWithRequiredProperties_then_instanceOfRequiredPropertyBuilderNameReturned(
          SafeBuilderVariant variant) {
    final JavaObjectPojo objectPojo = JavaPojos.objectPojo(requiredEmail(), optionalString());

    final BuilderName builderName = BuilderName.initial(variant, objectPojo);

    assertTrue(builderName instanceof RequiredPropertyBuilderName);
  }

  @Test
  void
      initial_when_fullBuilderAndObjectPojoWithoutRequiredProperties_then_instanceOfOptionalPropertyBuilderNameReturned() {
    final JavaObjectPojo objectPojo = JavaPojos.objectPojo(optionalBirthdate(), optionalString());

    final BuilderName builderName = BuilderName.initial(FULL, objectPojo);

    assertTrue(builderName instanceof OptionalPropertyBuilderName);
  }

  @Test
  void
      initial_when_standardBuilderAndObjectPojoWithoutRequiredProperties_then_instanceOfRequiredPropertyBuilderNameReturned() {
    final JavaObjectPojo objectPojo = JavaPojos.objectPojo(optionalBirthdate(), optionalString());

    final BuilderName builderName = BuilderName.initial(STANDARD, objectPojo);

    assertTrue(builderName instanceof RequiredPropertyBuilderName);
  }
}
