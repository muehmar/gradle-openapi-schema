package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class UnresolvedAnyOfComposition {
  private final PList<PojoName> pojoNames;

  private UnresolvedAnyOfComposition(PList<PojoName> pojoNames) {
    this.pojoNames = pojoNames;
  }

  public static UnresolvedAnyOfComposition fromPojoNames(PList<PojoName> pojoNames) {
    return new UnresolvedAnyOfComposition(pojoNames);
  }

  public Optional<AnyOfComposition> resolve(
      Function<PList<PojoName>, Optional<PList<Pojo>>> resolve) {
    return resolve.apply(pojoNames).map(AnyOfComposition::fromPojos);
  }
}
