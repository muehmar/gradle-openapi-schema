package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder.fullObjectPojoBuilder;
import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@PojoBuilder
public class UnresolvedObjectPojo {
  private final ComponentName name;
  private final String description;
  private final PList<PojoMember> members;
  private final PList<Name> requiredAdditionalProperties;
  private final Optional<UnresolvedAllOfComposition> allOfComposition;
  private final Optional<UnresolvedOneOfComposition> oneOfComposition;
  private final Optional<UnresolvedAnyOfComposition> anyOfComposition;
  private final Constraints constraints;
  private final AdditionalProperties additionalProperties;
  private final Optional<UntypedDiscriminator> discriminator;

  public ComponentName getName() {
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
      return fullObjectPojoBuilder()
          .name(name)
          .description(description)
          .members(members)
          .requiredAdditionalProperties(requiredAdditionalProperties)
          .constraints(constraints)
          .additionalProperties(additionalProperties)
          .allOfComposition(resolvedAllOf)
          .oneOfComposition(resolvedOneOf)
          .anyOfComposition(resolvedAnyOf)
          .discriminator(discriminator)
          .build()
          .asObjectPojo();
    } else {
      return Optional.empty();
    }
  }

  public Optional<UnresolvedAllOfComposition> getAllOfComposition() {
    return allOfComposition;
  }

  public Optional<UnresolvedOneOfComposition> getOneOfComposition() {
    return oneOfComposition;
  }

  public Optional<UnresolvedAnyOfComposition> getAnyOfComposition() {
    return anyOfComposition;
  }

  private static boolean bothPresentOrAbsent(Optional<?> a, Optional<?> b) {
    return not(a.isPresent() ^ b.isPresent());
  }
}
