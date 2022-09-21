package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import static com.github.muehmar.gradle.openapi.generator.model.ComposedPojo.CompositionType.ALL_OF;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import java.util.function.BiFunction;

public class ComposedPojoResolver {
  private ComposedPojoResolver() {}

  /**
   * Converts {@link ComposedPojo}'s to actual {@link Pojo}'s. The resulting list contains all
   * supplied pojo's as well as the converted ones.
   */
  public static PList<Pojo> resolve(PList<ComposedPojo> composedPojos, PList<Pojo> pojos) {
    final ResolveContext context =
        ResolveContext.initial(
            composedPojos.filter(composedPojo -> composedPojo.getType().equals(ALL_OF)), pojos);
    return resolveContext(context);
  }

  private static PList<Pojo> resolveContext(ResolveContext context) {
    return context.resolveNext(
        (ctx, composedPojo) -> {
          final ResolveContext nextResolveContext = resolveAllofComposedPojo(ctx, composedPojo);
          return resolveContext(nextResolveContext);
        });
  }

  private static ResolveContext resolveAllofComposedPojo(
      ResolveContext currentContext, ComposedPojo composedPojo) {
    final PList<PojoName> pojoNames = composedPojo.getPojoNames();
    final PList<Pojo> pojos = currentContext.getPojos();

    final PList<Pojo> allOfPojos =
        pojoNames.flatMapOptional(
            allOfPojoName -> pojos.find(pojo -> pojo.getName().equalsIgnoreCase(allOfPojoName)));

    if (allOfPojos.size() != pojoNames.size()) {
      return currentContext.failedAttempt(composedPojo);
    }

    final PList<PojoMember> allPojoMembers =
        allOfPojos.flatMap(
            pojo ->
                pojo.fold(
                    ObjectPojo::getMembers, arrayPojo -> PList.empty(), enumPojo -> PList.empty()));

    final ObjectPojo objectPojo =
        ObjectPojo.of(composedPojo.getName(), composedPojo.getDescription(), allPojoMembers);
    return currentContext.successfullyResolved(objectPojo);
  }

  private static class ResolveContext {
    private final PList<ComposedPojo> composedPojos;
    private final PList<Pojo> pojos;
    private final int failedResolveAttempts;

    public ResolveContext(
        PList<ComposedPojo> composedPojos, PList<Pojo> pojos, int failedResolveAttempts) {
      this.composedPojos = composedPojos;
      this.pojos = pojos;
      this.failedResolveAttempts = failedResolveAttempts;
    }

    public PList<Pojo> getPojos() {
      return pojos;
    }

    public static ResolveContext initial(PList<ComposedPojo> composedPojos, PList<Pojo> pojos) {
      return new ResolveContext(composedPojos, pojos, 0);
    }

    public PList<Pojo> resolveNext(
        BiFunction<ResolveContext, ComposedPojo, PList<Pojo>> resolveNext) {
      if (failedResolveAttempts > composedPojos.size()) {
        final String message =
            String.format(
                "Unable to resolve %d composed schemas: '%s'",
                composedPojos.size(), composedPojos.map(ComposedPojo::getName).mkString(", "));
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

    public ResolveContext failedAttempt(ComposedPojo composedPojo) {
      return new ResolveContext(composedPojos.add(composedPojo), pojos, failedResolveAttempts + 1);
    }

    public ResolveContext successfullyResolved(Pojo pojo) {
      return new ResolveContext(composedPojos, pojos.add(pojo), 0);
    }
  }
}
