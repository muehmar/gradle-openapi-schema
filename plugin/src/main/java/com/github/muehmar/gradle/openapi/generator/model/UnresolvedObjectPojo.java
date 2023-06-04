package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@PojoBuilder
public class UnresolvedObjectPojo {
  private final PojoName name;
  private final String description;
  private final PList<PojoMember> members;
  private final Optional<UnresolvedAllOfComposition> allOfComposition;
  private final Optional<UnresolvedOneOfComposition> oneOfComposition;
  private final Optional<UnresolvedAnyOfComposition> anyOfComposition;
  private final Constraints constraints;
  private final AdditionalProperties additionalProperties;

  public UnresolvedObjectPojo(
      PojoName name,
      String description,
      PList<PojoMember> members,
      Optional<UnresolvedAllOfComposition> allOfComposition,
      Optional<UnresolvedOneOfComposition> oneOfComposition,
      Optional<UnresolvedAnyOfComposition> anyOfComposition,
      Constraints constraints,
      AdditionalProperties additionalProperties) {
    this.name = name;
    this.description = description;
    this.members = members;
    this.allOfComposition = allOfComposition;
    this.oneOfComposition = oneOfComposition;
    this.anyOfComposition = anyOfComposition;
    this.constraints = constraints;
    this.additionalProperties = additionalProperties;
  }

  public PojoName getName() {
    return name;
  }

  public Optional<ObjectPojo> resolve(
      Function<UnresolvedAllOfComposition, Optional<AllOfComposition>> resolveAllOf,
      Function<UnresolvedOneOfComposition, Optional<OneOfComposition>> resolveOneOf,
      Function<UnresolvedAnyOfComposition, Optional<AnyOfComposition>> resolveAnyOf) {

    final Optional<AllOfComposition> resolvedAllOf = allOfComposition.flatMap(resolveAllOf);
    final Optional<OneOfComposition> resolvedOneOf = oneOfComposition.flatMap(resolveOneOf);
    final Optional<AnyOfComposition> resolvedAnyOf = anyOfComposition.flatMap(resolveAnyOf);
    if (bothPresentOrAbsent(allOfComposition, resolvedAllOf)
        && bothPresentOrAbsent(oneOfComposition, resolvedOneOf)
        && bothPresentOrAbsent(anyOfComposition, resolvedAnyOf)) {
      return ObjectPojoBuilder.create()
          .name(name)
          .description(description)
          .members(members)
          .constraints(constraints)
          .additionalProperties(additionalProperties)
          .andAllOptionals()
          .allOfComposition(resolvedAllOf)
          .oneOfComposition(resolvedOneOf)
          .anyOfComposition(resolvedAnyOf)
          .build()
          .asObjectPojo();
    } else {
      return Optional.empty();
    }
  }

  private static boolean bothPresentOrAbsent(Optional<?> a, Optional<?> b) {
    return not(a.isPresent() ^ b.isPresent());
  }
}
