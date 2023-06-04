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
public class UnresolvedAllOfComposition {
  private final PList<PojoName> pojoNames;

  private UnresolvedAllOfComposition(PList<PojoName> pojoNames) {
    this.pojoNames = pojoNames;
  }

  public static UnresolvedAllOfComposition fromPojoNames(PList<PojoName> pojoNames) {
    return new UnresolvedAllOfComposition(pojoNames);
  }

  public Optional<AllOfComposition> resolve(
      Function<PList<PojoName>, Optional<PList<Pojo>>> resolve) {
    return resolve.apply(pojoNames).map(AllOfComposition::fromPojos);
  }
}
