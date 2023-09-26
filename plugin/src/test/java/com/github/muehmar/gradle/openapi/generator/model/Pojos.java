package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
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
        .name(componentName("ObjectPojo", "Dto"))
        .description("Object pojo")
        .members(members)
        .requiredAdditionalProperties(PList.empty())
        .constraints(Constraints.empty())
        .additionalProperties(additionalProperties)
        .build();
  }

  public static ObjectPojo anyOfPojo(NonEmptyList<Pojo> anyOfPojos) {
    return ObjectPojoBuilder.create()
        .name(componentName("AnyOfPojo", "Dto"))
        .description("Any of pojo")
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

  public static ObjectPojo oneOfPojo(NonEmptyList<Pojo> oneOfPojos) {
    return ObjectPojoBuilder.create()
        .name(componentName("OneOfPojo", "Dto"))
        .description("One of pojo")
        .members(PList.empty())
        .requiredAdditionalProperties(PList.empty())
        .constraints(Constraints.empty())
        .additionalProperties(anyTypeAllowed())
        .andOptionals()
        .oneOfComposition(OneOfComposition.fromPojos(oneOfPojos))
        .build();
  }

  public static ObjectPojo oneOfPojo(Pojo oneOfPojo, Pojo... morePojos) {
    return oneOfPojo(NonEmptyList.of(oneOfPojo, morePojos));
  }

  public static ObjectPojo allOfPojo(NonEmptyList<Pojo> allOfPojos) {
    return ObjectPojoBuilder.create()
        .name(componentName("AllOfPojo", "Dto"))
        .description("All of pojo")
        .members(PList.empty())
        .requiredAdditionalProperties(PList.empty())
        .constraints(Constraints.empty())
        .additionalProperties(anyTypeAllowed())
        .andOptionals()
        .allOfComposition(AllOfComposition.fromPojos(allOfPojos))
        .build();
  }

  public static ObjectPojo allOfPojo(Pojo anyOfPojo, Pojo... morePojos) {
    return allOfPojo(NonEmptyList.of(anyOfPojo, morePojos));
  }
}
