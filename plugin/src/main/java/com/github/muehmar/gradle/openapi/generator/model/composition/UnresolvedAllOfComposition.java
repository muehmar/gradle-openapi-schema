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
public class UnresolvedAllOfComposition {
  private final PList<ComponentName> componentNames;

  private UnresolvedAllOfComposition(PList<ComponentName> componentNames) {
    this.componentNames = componentNames;
  }

  public static UnresolvedAllOfComposition fromComponentNames(PList<ComponentName> pojoNames) {
    return new UnresolvedAllOfComposition(pojoNames);
  }

  public Optional<AllOfComposition> resolve(
      Function<PList<ComponentName>, Optional<PList<Pojo>>> resolve) {
    return resolve
        .apply(componentNames)
        .flatMap(NonEmptyList::fromIter)
        .map(AllOfComposition::fromPojos);
  }
}
