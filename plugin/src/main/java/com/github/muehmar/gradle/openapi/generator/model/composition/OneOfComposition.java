package com.github.muehmar.gradle.openapi.generator.model.composition;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode
@ToString
public class OneOfComposition {
  private final NonEmptyList<Pojo> pojos;

  OneOfComposition(NonEmptyList<Pojo> pojos) {
    this.pojos = pojos;
  }

  public Optional<Discriminator> determineDiscriminator(
      Optional<Discriminator> objectPojoDiscriminator) {
    if (objectPojoDiscriminator.isPresent()) {
      assertRequiredDiscriminatorMember(pojos, objectPojoDiscriminator);
      return objectPojoDiscriminator;
    }

    final Optional<Discriminator> discriminatorFromPojos =
        determineDiscriminatorFromComposedPojos();
    assertRequiredDiscriminatorMember(pojos, discriminatorFromPojos);
    return discriminatorFromPojos;
  }

  private Optional<Discriminator> determineDiscriminatorFromComposedPojos() {
    final CompositionDiscriminators compositionDiscriminators =
        new CompositionDiscriminators(
            pojos.map(OneOfComposition::findDiscriminatorsForPojo).toPList());
    return compositionDiscriminators.findCommonDiscriminator();
  }

  private static PojoDiscriminators findDiscriminatorsForPojo(Pojo pojo) {
    final Optional<Discriminator> objectPojoDiscriminator =
        pojo.asObjectPojo().flatMap(ObjectPojo::getDiscriminator);
    final PojoDiscriminators nestedDiscriminators =
        pojo.asObjectPojo()
            .flatMap(ObjectPojo::getAllOfComposition)
            .map(AllOfComposition::getPojos)
            .map(NonEmptyList::toPList)
            .orElse(PList.empty())
            .map(OneOfComposition::findDiscriminatorsForPojo)
            .reduce(PojoDiscriminators::merge)
            .orElseGet(PojoDiscriminators::empty);
    return PojoDiscriminators.fromOptional(objectPojoDiscriminator).merge(nestedDiscriminators);
  }

  public static OneOfComposition fromPojos(NonEmptyList<Pojo> pojos) {
    return new OneOfComposition(pojos);
  }

  public NonEmptyList<Pojo> getPojos() {
    return pojos;
  }

  public OneOfComposition inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType) {
    final NonEmptyList<Pojo> mappedPojos =
        pojos.map(
            pojo -> pojo.inlineObjectReference(referenceName, referenceDescription, referenceType));
    return new OneOfComposition(mappedPojos);
  }

  public OneOfComposition applyMapping(PojoNameMapping pojoNameMapping) {
    final NonEmptyList<Pojo> mappedPojos = pojos.map(pojo -> pojo.applyMapping(pojoNameMapping));
    return new OneOfComposition(mappedPojos);
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
                                      .getMembersAndAllOfMembers()
                                      .find(
                                          pojoMember ->
                                              pojoMember.getName().equals(disc.getPropertyName()))
                                      .filter(PojoMember::isRequired);
                              if (not(discriminatorMember.isPresent())) {
                                throw new IllegalArgumentException(
                                    String.format(
                                        "Invalid schema: Pojo %s does not have a required property named %s used by the discriminator.",
                                        objectPojo.getName().getSchemaName(),
                                        disc.getPropertyName()));
                              }
                            })));
  }

  @Value
  private static class PojoDiscriminators {
    PList<Discriminator> discriminators;

    static PojoDiscriminators empty() {
      return new PojoDiscriminators(PList.empty());
    }

    static PojoDiscriminators fromOptional(Optional<Discriminator> discriminator) {
      return new PojoDiscriminators(PList.fromOptional(discriminator));
    }

    PojoDiscriminators merge(PojoDiscriminators other) {
      return new PojoDiscriminators(discriminators.concat(other.discriminators));
    }
  }

  @Value
  private static class CompositionDiscriminators {
    PList<PojoDiscriminators> discriminators;

    public Optional<Discriminator> findCommonDiscriminator() {
      return discriminators
          .headOption()
          .flatMap(
              pojoDiscriminators -> {
                final PList<PojoDiscriminators> remaining = discriminators.tail();
                for (Discriminator discriminator : pojoDiscriminators.getDiscriminators()) {
                  final boolean existsInAll =
                      remaining.forall(
                          other -> other.getDiscriminators().exists(discriminator::equals));
                  if (existsInAll) {
                    return Optional.of(discriminator);
                  }
                }
                return Optional.empty();
              });
    }
  }
}
