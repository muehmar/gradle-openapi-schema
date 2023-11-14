package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.allof;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredEmail;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static org.junit.jupiter.api.Assertions.*;

import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.name.BuilderName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property.RequiredPropertyBuilderName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

class AllOfBuilderNameTest {
  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void initial_when_allOfPojo_then_instanceOfAllOfBuilderNameReturned(SafeBuilderVariant variant) {
    final JavaObjectPojo allOfPojo = JavaPojos.allOfPojo(sampleObjectPojo1(), sampleObjectPojo2());

    final BuilderName builderName = AllOfBuilderName.initial(variant, allOfPojo);

    assertTrue(builderName instanceof AllOfBuilderName);
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void initial_when_noAllOfComposition_then_instanceOfRequiredPropertyBuilderNameReturned(
      SafeBuilderVariant variant) {
    final JavaObjectPojo objectPojo = JavaPojos.objectPojo(requiredEmail(), optionalString());

    final BuilderName builderName = AllOfBuilderName.initial(variant, objectPojo);

    assertTrue(builderName instanceof RequiredPropertyBuilderName);
  }
}
