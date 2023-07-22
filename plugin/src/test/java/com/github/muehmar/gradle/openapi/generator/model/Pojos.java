package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;

public class Pojos {
  private Pojos() {}

  public static ObjectPojo objectPojo(PList<PojoMember> members) {
    return objectPojo(members, anyTypeAllowed());
  }

  public static ObjectPojo objectPojo(
      PList<PojoMember> members, AdditionalProperties additionalProperties) {
    return ObjectPojoBuilder.create()
        .name(PojoName.ofNameAndSuffix("ObjectPojo", "Dto"))
        .description("Object pojo")
        .members(members)
        .requiredAdditionalProperties(PList.empty())
        .constraints(Constraints.empty())
        .additionalProperties(additionalProperties)
        .build();
  }

  public static ObjectPojo anyOfPojo(NonEmptyList<Pojo> anyOfPojos) {
    return ObjectPojoBuilder.create()
        .name(PojoName.ofNameAndSuffix("ObjectPojo", "Dto"))
        .description("Object pojo")
        .members(PList.empty())
        .requiredAdditionalProperties(PList.empty())
        .constraints(Constraints.empty())
        .additionalProperties(anyTypeAllowed())
        .andOptionals()
        .anyOfComposition(AnyOfComposition.fromPojos(anyOfPojos))
        .build();
  }

  public static ObjectPojo anyOfPojo(Pojo anyOfPojo, Pojo... morePojos) {
    return anyOfPojo(NonEmptyList.of(anyOfPojo, morePojos));
  }
}
