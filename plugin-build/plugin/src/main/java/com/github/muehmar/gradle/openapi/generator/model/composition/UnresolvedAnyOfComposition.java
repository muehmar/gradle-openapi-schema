package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class UnresolvedAnyOfComposition {
  private final PList<ComponentName> componentNames;

  private UnresolvedAnyOfComposition(PList<ComponentName> componentNames) {
    this.componentNames = componentNames;
  }

  public static UnresolvedAnyOfComposition fromPojoNames(PList<ComponentName> componentNames) {
    return new UnresolvedAnyOfComposition(componentNames);
  }

  public PList<ComponentName> getComponentNames() {
    return componentNames;
  }

  public Optional<AnyOfComposition> resolve(
      Function<PList<ComponentName>, Optional<PList<Pojo>>> resolve) {
    return resolve
        .apply(componentNames)
        .flatMap(NonEmptyList::fromIter)
        .map(AnyOfComposition::fromPojos);
  }
}
