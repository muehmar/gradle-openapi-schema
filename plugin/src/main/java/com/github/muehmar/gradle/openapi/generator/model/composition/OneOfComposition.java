package com.github.muehmar.gradle.openapi.generator.model.composition;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
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

    assertRequiredDiscriminatorMember(pojos, discriminator);
  }

  private static void assertRequiredDiscriminatorMember(
      NonEmptyList<Pojo> pojos, Optional<Discriminator> discriminator) {
    discriminator.ifPresent(
        disc ->
            pojos.forEach(
                pojo ->
                    pojo.asObjectPojo()
                        .ifPresent(
                            objectPojo -> {
                              final Optional<PojoMember> discriminatorMember =
                                  objectPojo
                                      .getMembers()
                                      .find(
                                          pojoMember ->
                                              pojoMember.getName().equals(disc.getPropertyName()))
                                      .filter(PojoMember::isRequired);
                              if (not(discriminatorMember.isPresent())) {
                                throw new IllegalArgumentException(
                                    String.format(
                                        "Invalid schema: Pojo %s does not have a required property named %s used by the discriminator.",
                                        objectPojo.getName(), disc.getPropertyName()));
                              }
                            })));
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

  public OneOfComposition inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType) {
    final NonEmptyList<Pojo> mappedPojos =
        pojos.map(
            pojo -> pojo.inlineObjectReference(referenceName, referenceDescription, referenceType));
    return new OneOfComposition(mappedPojos, discriminator);
  }

  public OneOfComposition applyMapping(PojoNameMapping pojoNameMapping) {
    final NonEmptyList<Pojo> mappedPojos = pojos.map(pojo -> pojo.applyMapping(pojoNameMapping));
    return new OneOfComposition(mappedPojos, discriminator);
  }
}
