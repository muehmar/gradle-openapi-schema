package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class UnresolvedOneOfComposition {
  private final PList<PojoName> pojoNames;
  private final Optional<Discriminator> discriminator;

  private UnresolvedOneOfComposition(
      PList<PojoName> pojoNames, Optional<Discriminator> discriminator) {
    this.pojoNames = pojoNames;
    this.discriminator = discriminator;
  }

  public static UnresolvedOneOfComposition fromPojoNamesAndDiscriminator(
      PList<PojoName> pojoNames, Optional<Discriminator> discriminator) {
    return new UnresolvedOneOfComposition(pojoNames, discriminator);
  }

  public Optional<OneOfComposition> resolve(
      Function<PList<PojoName>, Optional<PList<Pojo>>> resolve) {
    return resolve
        .apply(pojoNames)
        .flatMap(NonEmptyList::fromIter)
        .map(pojos -> new OneOfComposition(pojos, discriminator));
  }

  public Optional<Discriminator> getDiscriminator() {
    return discriminator;
  }
}
