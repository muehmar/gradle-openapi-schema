package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class OneOfComposition {
  private final NonEmptyList<Pojo> pojos;
  private final Optional<Discriminator> discriminator;

  OneOfComposition(NonEmptyList<Pojo> pojos, Optional<Discriminator> discriminator) {
    this.pojos = pojos;
    this.discriminator = discriminator;
  }

  public static OneOfComposition fromPojos(NonEmptyList<Pojo> pojos) {
    return new OneOfComposition(pojos, Optional.empty());
  }

  public static OneOfComposition fromPojosAndDiscriminator(
      NonEmptyList<Pojo> pojos, Discriminator discriminator) {
    return new OneOfComposition(pojos, Optional.of(discriminator));
  }

  public NonEmptyList<Pojo> getPojos() {
    return pojos;
  }

  public Optional<Discriminator> getDiscriminator() {
    return discriminator;
  }
}
