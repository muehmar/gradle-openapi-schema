package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.oneof;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers.requiredEmail;
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

class OneOfBuilderNameTest {
  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void initial_when_oneOfPojo_then_instanceOfOneOfBuilderNameReturned(SafeBuilderVariant variant) {
    final JavaObjectPojo oneOfPojo = JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2());

    final BuilderName builderName = OneOfBuilderName.initial(variant, oneOfPojo);

    assertTrue(builderName instanceof OneOfBuilderName);
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void initial_when_noOneOfComposition_then_instanceOfRequiredPropertyBuilderNameReturned(
      SafeBuilderVariant variant) {
    final JavaObjectPojo objectPojo = JavaPojos.objectPojo(requiredEmail(), optionalString());

    final BuilderName builderName = OneOfBuilderName.initial(variant, objectPojo);

    assertTrue(builderName instanceof RequiredPropertyBuilderName);
  }
}
