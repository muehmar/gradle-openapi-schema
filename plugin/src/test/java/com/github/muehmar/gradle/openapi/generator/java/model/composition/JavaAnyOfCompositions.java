package com.github.muehmar.gradle.openapi.generator.java.model.composition;

import static com.github.muehmar.gradle.openapi.generator.java.model.composition.Assertion.assertAllObjectPojos;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.composition.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.composition.DiscriminatorType;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import java.util.Optional;

public class JavaAnyOfCompositions {
  private JavaAnyOfCompositions() {}

  public static JavaAnyOfComposition fromPojosAndDiscriminator(
      NonEmptyList<JavaPojo> pojos, UntypedDiscriminator discriminator) {
    final DiscriminatorType discriminatorType =
        DiscriminatorType.fromStringType(StringType.noFormat());
    return fromPojosAndDiscriminator(pojos, discriminator, discriminatorType);
  }

  public static JavaAnyOfComposition fromPojosAndDiscriminator(
      NonEmptyList<JavaPojo> pojos,
      UntypedDiscriminator discriminator,
      DiscriminatorType discriminatorType) {
    return new JavaAnyOfComposition(
        new JavaComposition(assertAllObjectPojos(pojos)),
        Optional.of(
            JavaDiscriminator.wrap(
                Discriminator.typeDiscriminator(discriminator, discriminatorType))));
  }
}
