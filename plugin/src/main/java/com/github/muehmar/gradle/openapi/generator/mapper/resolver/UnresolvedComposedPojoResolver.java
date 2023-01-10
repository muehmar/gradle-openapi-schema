package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import static com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo.CompositionType.ALL_OF;
import static com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo.CompositionType.ANY_OF;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import java.util.Optional;
import java.util.function.BiFunction;

public class UnresolvedComposedPojoResolver {
  private UnresolvedComposedPojoResolver() {}

  /**
   * Converts {@link UnresolvedComposedPojo}'s to actual {@link Pojo}'s. The resulting list contains
   * all supplied pojo's as well as the converted ones.
   */
  public static PList<Pojo> resolve(
      PList<UnresolvedComposedPojo> composedPojos, PList<Pojo> pojos) {
    final ResolveContext context = ResolveContext.initial(composedPojos, pojos);
    return resolveContext(context);
  }

  private static PList<Pojo> resolveContext(ResolveContext context) {
    return context.resolveNext(
        (ctx, composedPojo) -> {
          final ResolveContext nextResolveContext = resolveSingle(ctx, composedPojo);
          return resolveContext(nextResolveContext);
        });
  }

  private static ResolveContext resolveSingle(
      ResolveContext ctx, UnresolvedComposedPojo unresolvedComposedPojo) {
    final UnresolvedComposedPojo.CompositionType type = unresolvedComposedPojo.getType();
    if (type.equals(ALL_OF)) {
      return resolveAllOf(ctx, unresolvedComposedPojo);
    } else if (type.equals(ANY_OF)) {
      return resolveAnyOf(ctx, unresolvedComposedPojo);
    } else {
      return resolveOneOf(ctx, unresolvedComposedPojo);
    }
  }

  private static ResolveContext resolveAllOf(
      ResolveContext context, UnresolvedComposedPojo unresolvedComposedPojo) {
    final PList<Pojo> pojos = context.getPojos();

    return resolvePojos(unresolvedComposedPojo, pojos)
        .map(resolvedPojos -> createAllOfObjectPojo(context, unresolvedComposedPojo, resolvedPojos))
        .orElseGet(() -> context.failedAttempt(unresolvedComposedPojo));
  }

  private static ResolveContext createAllOfObjectPojo(
      ResolveContext context,
      UnresolvedComposedPojo unresolvedComposedPojo,
      PList<Pojo> resolvedPojos) {
    final PList<PojoMember> allPojoMembers =
        resolvedPojos.flatMap(
            pojo ->
                pojo.fold(
                    ObjectPojo::getMembers,
                    arrayPojo -> PList.empty(),
                    enumPojo -> PList.empty(),
                    composedPojo -> PList.empty(),
                    freeFormPojo -> PList.empty()));

    final ObjectPojo objectPojo =
        ObjectPojo.of(
            unresolvedComposedPojo.getName(),
            unresolvedComposedPojo.getDescription(),
            allPojoMembers,
            unresolvedComposedPojo.getConstraints());
    return context.successfullyResolved(objectPojo);
  }

  private static ResolveContext resolveAnyOf(
      ResolveContext context, UnresolvedComposedPojo unresolvedComposedPojo) {
    final PList<Pojo> pojos = context.getPojos();
    return resolvePojos(unresolvedComposedPojo, pojos)
        .map(resolvePojos -> createAnyOfComposedPojo(context, unresolvedComposedPojo, resolvePojos))
        .orElseGet(() -> context.failedAttempt(unresolvedComposedPojo));
  }

  private static ResolveContext createAnyOfComposedPojo(
      ResolveContext context,
      UnresolvedComposedPojo unresolvedComposedPojo,
      PList<Pojo> resolvedPojos) {
    final ComposedPojo composedPojo =
        ComposedPojo.resolvedAnyOf(resolvedPojos, unresolvedComposedPojo);
    return context.successfullyResolved(composedPojo);
  }

  private static ResolveContext resolveOneOf(
      ResolveContext context, UnresolvedComposedPojo unresolvedComposedPojo) {
    final PList<Pojo> pojos = context.getPojos();
    return resolvePojos(unresolvedComposedPojo, pojos)
        .map(resolvePojos -> createOneOfComposedPojo(context, unresolvedComposedPojo, resolvePojos))
        .orElseGet(() -> context.failedAttempt(unresolvedComposedPojo));
  }

  private static ResolveContext createOneOfComposedPojo(
      ResolveContext context,
      UnresolvedComposedPojo unresolvedComposedPojo,
      PList<Pojo> resolvedPojos) {
    final ComposedPojo composedPojo =
        ComposedPojo.resolvedOneOf(resolvedPojos, unresolvedComposedPojo);
    return context.successfullyResolved(composedPojo);
  }

  private static Optional<PList<Pojo>> resolvePojos(
      UnresolvedComposedPojo unresolvedComposedPojo, PList<Pojo> pojos) {
    final PList<PojoName> pojoNames = unresolvedComposedPojo.getPojoNames();

    final PList<Pojo> resolvedComposedPojos =
        pojoNames.flatMapOptional(
            name -> pojos.find(pojo -> pojo.getName().equalsIgnoreCase(name)));

    if (resolvedComposedPojos.size() != pojoNames.size()) {
      return Optional.empty();
    }

    return Optional.of(resolvedComposedPojos);
  }

  private static class ResolveContext {
    private final PList<UnresolvedComposedPojo> composedPojos;
    private final PList<Pojo> pojos;
    private final int failedResolveAttempts;

    public ResolveContext(
        PList<UnresolvedComposedPojo> composedPojos, PList<Pojo> pojos, int failedResolveAttempts) {
      this.composedPojos = composedPojos;
      this.pojos = pojos;
      this.failedResolveAttempts = failedResolveAttempts;
    }

    public PList<Pojo> getPojos() {
      return pojos;
    }

    public static ResolveContext initial(
        PList<UnresolvedComposedPojo> composedPojos, PList<Pojo> pojos) {
      return new ResolveContext(composedPojos, pojos, 0);
    }

    public PList<Pojo> resolveNext(
        BiFunction<ResolveContext, UnresolvedComposedPojo, PList<Pojo>> resolveNext) {
      if (failedResolveAttempts > composedPojos.size()) {
        final String message =
            String.format(
                "Unable to resolve %d composed schemas: '%s'",
                composedPojos.size(),
                composedPojos.map(UnresolvedComposedPojo::getName).mkString(", "));
        throw new IllegalStateException(message);
      }
      return composedPojos
          .headOption()
          .map(
              composedPojo ->
                  resolveNext.apply(
                      new ResolveContext(composedPojos.tail(), pojos, failedResolveAttempts),
                      composedPojo))
          .orElse(pojos);
    }

    public ResolveContext failedAttempt(UnresolvedComposedPojo composedPojo) {
      return new ResolveContext(composedPojos.add(composedPojo), pojos, failedResolveAttempts + 1);
    }

    public ResolveContext successfullyResolved(Pojo pojo) {
      return new ResolveContext(composedPojos, pojos.add(pojo), 0);
    }
  }
}
