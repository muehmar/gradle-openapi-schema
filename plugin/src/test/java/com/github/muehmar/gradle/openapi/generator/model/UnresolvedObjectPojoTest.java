package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class UnresolvedObjectPojoTest {
  @ParameterizedTest
  @MethodSource("compositionsAndResolverCombinations")
  void resolve_when_compositionsAndResolvers_then_expectedIsResolved(
      Optional<UnresolvedAllOfComposition> allOfComposition,
      Optional<UnresolvedOneOfComposition> oneOfComposition,
      Optional<UnresolvedAnyOfComposition> anyOfComposition,
      Function<UnresolvedAllOfComposition, Optional<AllOfComposition>> allOfNotResolver,
      Function<UnresolvedOneOfComposition, Optional<OneOfComposition>> oneOfNotResolver,
      Function<UnresolvedAnyOfComposition, Optional<AnyOfComposition>> anyOfNotResolver,
      boolean expectedIsResolved) {
    final UnresolvedObjectPojo unresolvedObjectPojo =
        UnresolvedObjectPojoBuilder.create()
            .name(componentName("Object", "Dto"))
            .description("description")
            .nullability(NOT_NULLABLE)
            .members(PList.empty())
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(AdditionalProperties.notAllowed())
            .andAllOptionals()
            .allOfComposition(allOfComposition)
            .oneOfComposition(oneOfComposition)
            .anyOfComposition(anyOfComposition)
            .discriminator(Optional.empty())
            .build();

    final Optional<ObjectPojo> resolved =
        unresolvedObjectPojo.resolve(allOfNotResolver, oneOfNotResolver, anyOfNotResolver);

    assertEquals(expectedIsResolved, resolved.isPresent());
  }

  private static Stream<Arguments> compositionsAndResolverCombinations() {
    final NonEmptyList<Pojo> pojos = NonEmptyList.single(Pojos.objectPojo(PList.empty()));
    final Function<UnresolvedAllOfComposition, Optional<AllOfComposition>> allOfResolver =
        ignore -> Optional.of(AllOfComposition.fromPojos(pojos));
    final Function<UnresolvedOneOfComposition, Optional<OneOfComposition>> oneOfResolver =
        ignore -> Optional.of(OneOfComposition.fromPojos(pojos));
    final Function<UnresolvedAnyOfComposition, Optional<AnyOfComposition>> anyOfResolver =
        ignore -> Optional.of(AnyOfComposition.fromPojos(pojos));

    final UnresolvedAllOfComposition unresolvedAllOf =
        UnresolvedAllOfComposition.fromComponentNames(PList.of(componentName("Object", "Dto")));
    final UnresolvedOneOfComposition unresolvedOneOf =
        UnresolvedOneOfComposition.fromComponentNames(PList.of(componentName("Object", "Dto")));
    final UnresolvedAnyOfComposition unresolvedAnyOf =
        UnresolvedAnyOfComposition.fromPojoNames(PList.of(componentName("Object", "Dto")));

    return Stream.of(
        arguments(
            Optional.empty(),
            Optional.empty(),
            Optional.empty(),
            notResolver(),
            notResolver(),
            notResolver(),
            true),
        arguments(
            Optional.of(unresolvedAllOf),
            Optional.empty(),
            Optional.empty(),
            allOfResolver,
            notResolver(),
            notResolver(),
            true),
        arguments(
            Optional.empty(),
            Optional.of(unresolvedOneOf),
            Optional.empty(),
            notResolver(),
            oneOfResolver,
            notResolver(),
            true),
        arguments(
            Optional.empty(),
            Optional.empty(),
            Optional.of(unresolvedAnyOf),
            notResolver(),
            notResolver(),
            anyOfResolver,
            true),
        arguments(
            Optional.of(unresolvedAllOf),
            Optional.empty(),
            Optional.of(unresolvedAnyOf),
            allOfResolver,
            notResolver(),
            anyOfResolver,
            true),
        arguments(
            Optional.of(unresolvedAllOf),
            Optional.empty(),
            Optional.of(unresolvedAnyOf),
            notResolver(),
            notResolver(),
            anyOfResolver,
            false),
        arguments(
            Optional.of(unresolvedAllOf),
            Optional.empty(),
            Optional.empty(),
            notResolver(),
            oneOfResolver,
            anyOfResolver,
            false),
        arguments(
            Optional.empty(),
            Optional.of(unresolvedOneOf),
            Optional.empty(),
            allOfResolver,
            notResolver(),
            anyOfResolver,
            false),
        arguments(
            Optional.empty(),
            Optional.empty(),
            Optional.of(unresolvedAnyOf),
            allOfResolver,
            oneOfResolver,
            notResolver(),
            false));
  }

  private static <A, B> Function<A, Optional<B>> notResolver() {
    return ignore -> Optional.empty();
  }
}
