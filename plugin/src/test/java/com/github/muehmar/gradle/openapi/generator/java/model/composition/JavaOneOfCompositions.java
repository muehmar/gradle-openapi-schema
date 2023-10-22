package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import java.util.Optional;

public class JavaOneOfCompositions {
  private JavaOneOfCompositions() {}

  public static JavaOneOfComposition fromPojosAndDiscriminator(
      NonEmptyList<JavaPojo> pojos, Discriminator discriminator) {
    return new JavaOneOfComposition(pojos, Optional.of(JavaDiscriminator.wrap(discriminator)));
  }
}
