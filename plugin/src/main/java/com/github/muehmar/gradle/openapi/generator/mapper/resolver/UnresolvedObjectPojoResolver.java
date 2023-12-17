package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import java.util.Optional;
import java.util.function.BiFunction;

public class UnresolvedObjectPojoResolver {
  private UnresolvedObjectPojoResolver() {}

  /**
   * Converts {@link UnresolvedComposedPojo}'s to actual {@link Pojo}'s. The resulting list contains
   * all supplied pojo's as well as the converted ones.
   */
  public static PList<Pojo> resolve(PList<UnresolvedObjectPojo> objectPojos, PList<Pojo> pojos) {
    final ResolveContext context = ResolveContext.initial(objectPojos, pojos);
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
      ResolveContext ctx, UnresolvedObjectPojo unresolvedObjectPojo) {
    final PList<Pojo> pojos = ctx.getPojos();
    return unresolvedObjectPojo
        .resolve(
            allOf -> resolveAllOf(allOf, pojos),
            oneOf -> resolveOneOf(oneOf, pojos),
            anyOf -> resolveAnyOf(anyOf, pojos))
        .map(ctx::successfullyResolved)
        .orElseGet(() -> ctx.failedAttempt(unresolvedObjectPojo));
  }

  private static Optional<AllOfComposition> resolveAllOf(
      UnresolvedAllOfComposition unresolvedAllOfComposition, PList<Pojo> pojos) {
    return unresolvedAllOfComposition.resolve(
        componentNames -> resolveComponentNames(componentNames, pojos));
  }

  private static Optional<OneOfComposition> resolveOneOf(
      UnresolvedOneOfComposition unresolvedOneOfComposition, PList<Pojo> pojos) {
    return unresolvedOneOfComposition.resolve(pojoNames -> resolveComponentNames(pojoNames, pojos));
  }

  private static Optional<AnyOfComposition> resolveAnyOf(
      UnresolvedAnyOfComposition unresolvedAnyOfComposition, PList<Pojo> pojos) {
    return unresolvedAnyOfComposition.resolve(pojoNames -> resolveComponentNames(pojoNames, pojos));
  }

  private static Optional<PList<Pojo>> resolveComponentNames(
      PList<ComponentName> componentNames, PList<Pojo> pojos) {
    final PList<Pojo> resolvedComposedPojos =
        componentNames.flatMapOptional(
            name ->
                pojos.find(
                    pojo -> pojo.getName().getPojoName().equalsIgnoreCase(name.getPojoName())));

    if (resolvedComposedPojos.size() != componentNames.size()) {
      return Optional.empty();
    }

    return Optional.of(resolvedComposedPojos);
  }

  private static class ResolveContext {
    private final PList<UnresolvedObjectPojo> objectPojos;
    private final PList<Pojo> pojos;
    private final int failedResolveAttempts;

    public ResolveContext(
        PList<UnresolvedObjectPojo> objectPojos, PList<Pojo> pojos, int failedResolveAttempts) {
      this.objectPojos = objectPojos;
      this.pojos = pojos;
      this.failedResolveAttempts = failedResolveAttempts;
    }

    public PList<Pojo> getPojos() {
      return pojos;
    }

    public static ResolveContext initial(
        PList<UnresolvedObjectPojo> objectPojos, PList<Pojo> pojos) {
      return new ResolveContext(objectPojos, pojos, 0);
    }

    public PList<Pojo> resolveNext(
        BiFunction<ResolveContext, UnresolvedObjectPojo, PList<Pojo>> resolveNext) {
      if (failedResolveAttempts > objectPojos.size()) {
        final String message = createUnresolvableMessage();
        throw new IllegalStateException(message);
      }
      return objectPojos
          .headOption()
          .map(
              composedPojo ->
                  resolveNext.apply(
                      new ResolveContext(objectPojos.tail(), pojos, failedResolveAttempts),
                      composedPojo))
          .orElse(pojos);
    }

    public ResolveContext failedAttempt(UnresolvedObjectPojo objectPojo) {
      return new ResolveContext(objectPojos.add(objectPojo), pojos, failedResolveAttempts + 1);
    }

    public ResolveContext successfullyResolved(Pojo pojo) {
      return new ResolveContext(objectPojos, pojos.add(pojo), 0);
    }

    private String createUnresolvableMessage() {
      final String singleMessages =
          objectPojos.map(this::createUnresolvableMessageForPojo).mkString("\n");
      return String.format(
          "%s\nIf the schema is present in the specification, it may not be detected properly as object type, adding 'type: object' explicitly to the definition may help.",
          singleMessages);
    }

    private String createUnresolvableMessageForPojo(UnresolvedObjectPojo pojo) {
      final PList<ComponentName> missingComponentNames =
          pojo.getAllOfComposition()
              .map(UnresolvedAllOfComposition::getComponentNames)
              .orElse(PList.empty())
              .concat(
                  pojo.getOneOfComposition()
                      .map(UnresolvedOneOfComposition::getComponentNames)
                      .orElse(PList.empty()))
              .concat(
                  pojo.getAnyOfComposition()
                      .map(UnresolvedAnyOfComposition::getComponentNames)
                      .orElse(PList.empty()))
              .filter(
                  componentName ->
                      not(
                          pojos.exists(
                              p -> p.getName().getPojoName().equals(componentName.getPojoName()))));

      return String.format(
          "For schema %s the following schemas could not be resolved: [%s]",
          pojo.getName().getSchemaName(),
          missingComponentNames.map(ComponentName::getSchemaName).mkString(", "));
    }
  }
}
