package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class UnresolvedOneOfComposition {
  private final PList<ComponentName> componentNames;
  private final Optional<Discriminator> discriminator;

  private UnresolvedOneOfComposition(
      PList<ComponentName> componentNames, Optional<Discriminator> discriminator) {
    this.componentNames = componentNames;
    this.discriminator = discriminator;
  }

  public static UnresolvedOneOfComposition fromPojoNamesAndDiscriminator(
      PList<ComponentName> componentNames, Optional<Discriminator> discriminator) {
    return new UnresolvedOneOfComposition(componentNames, discriminator);
  }

  public Optional<OneOfComposition> resolve(
      Function<PList<ComponentName>, Optional<PList<Pojo>>> resolve) {
    return resolve
        .apply(componentNames)
        .flatMap(NonEmptyList::fromIter)
        .map(pojos -> new OneOfComposition(pojos, discriminator));
  }

  public Optional<Discriminator> getDiscriminator() {
    return discriminator;
  }
}
