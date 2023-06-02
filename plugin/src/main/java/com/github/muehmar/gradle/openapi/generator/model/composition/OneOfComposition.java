package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class OneOfComposition {
  private final PList<Pojo> pojos;
  private final Optional<Discriminator> discriminator;

  private OneOfComposition(PList<Pojo> pojos, Optional<Discriminator> discriminator) {
    this.pojos = pojos;
    this.discriminator = discriminator;
  }

  public static OneOfComposition fromPojos(PList<Pojo> pojos) {
    return new OneOfComposition(pojos, Optional.empty());
  }

  public static OneOfComposition fromPojosAndDiscriminator(
      PList<Pojo> pojos, Discriminator discriminator) {
    return new OneOfComposition(pojos, Optional.of(discriminator));
  }

  public PList<Pojo> getPojos() {
    return pojos;
  }

  public Optional<Discriminator> getDiscriminator() {
    return discriminator;
  }
}
