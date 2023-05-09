package com.github.muehmar.gradle.openapi.generator.model;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import java.util.Optional;

public class Pojos {
  private Pojos() {}

  public static ObjectPojo objectPojo(PList<PojoMember> members) {
    return ObjectPojoBuilder.create()
        .name(PojoName.ofNameAndSuffix("ObjectPojo", "Dto"))
        .description("Object pojo")
        .members(members)
        .constraints(Constraints.empty())
        .build();
  }

  public static ComposedPojo composedAnyOf(PList<ObjectPojo> pojos) {
    final UnresolvedComposedPojo unresolvedComposedPojo =
        new UnresolvedComposedPojo(
            PojoName.ofNameAndSuffix("ComposedAnyOf", "Dto"),
            "Description",
            UnresolvedComposedPojo.CompositionType.ANY_OF,
            pojos.map(ObjectPojo::getName),
            Constraints.empty(),
            Optional.empty());
    return ComposedPojo.resolvedAnyOf(pojos.map(p -> p), unresolvedComposedPojo);
  }
}
