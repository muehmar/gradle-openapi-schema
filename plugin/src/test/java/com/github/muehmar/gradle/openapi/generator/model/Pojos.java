package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;

import ch.bluecare.commons.data.PList;
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
        .constraints(Constraints.empty())
        .additionalProperties(additionalProperties)
        .build();
  }
}
