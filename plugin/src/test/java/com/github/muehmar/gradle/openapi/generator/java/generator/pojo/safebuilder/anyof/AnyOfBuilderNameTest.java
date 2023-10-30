package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.anyof;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers.requiredEmail;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredPropertyBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AnyOfBuilderNameTest {

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void initial_when_anyOfPojo_then_instanceOfAnyOfBuilderNameReturned(SafeBuilderVariant variant) {
    final JavaObjectPojo anyOfPojo = JavaPojos.anyOfPojo(sampleObjectPojo1(), sampleObjectPojo2());

    final BuilderName builderName = AnyOfBuilderName.initial(variant, anyOfPojo);

    assertTrue(builderName instanceof AnyOfBuilderName);
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void initial_when_noAnyOfComposition_then_instanceOfRequiredPropertyBuilderNameReturned(
      SafeBuilderVariant variant) {
    final JavaObjectPojo objectPojo = JavaPojos.objectPojo(requiredEmail(), optionalString());

    final BuilderName builderName = AnyOfBuilderName.initial(variant, objectPojo);

    assertTrue(builderName instanceof RequiredPropertyBuilderName);
  }
}
