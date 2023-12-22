package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.composition.Assertion.assertAllObjectPojos;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import java.util.Optional;

public class JavaOneOfCompositions {
  private JavaOneOfCompositions() {}

  public static JavaOneOfComposition fromPojosAndDiscriminator(
      NonEmptyList<JavaPojo> pojos, UntypedDiscriminator discriminator) {
    return new JavaOneOfComposition(
        new JavaComposition(assertAllObjectPojos(pojos)),
        Optional.of(JavaDiscriminator.wrap(discriminator)));
  }
}
