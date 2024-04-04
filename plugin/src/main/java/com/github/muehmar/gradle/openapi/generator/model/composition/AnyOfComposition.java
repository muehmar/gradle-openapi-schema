package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class AnyOfComposition {
  private final NonEmptyList<Pojo> pojos;

  private AnyOfComposition(NonEmptyList<Pojo> pojos) {
    this.pojos = pojos;
  }

  public static AnyOfComposition fromPojos(NonEmptyList<Pojo> pojos) {
    return new AnyOfComposition(pojos);
  }

  public Optional<Discriminator> determineDiscriminator(
      Optional<UntypedDiscriminator> objectPojoDiscriminator) {
    final DiscriminatorDeterminator discriminatorDeterminator =
        new DiscriminatorDeterminator(pojos);
    return discriminatorDeterminator.determineDiscriminator(objectPojoDiscriminator);
  }

  public NonEmptyList<Pojo> getPojos() {
    return pojos;
  }

  public AnyOfComposition inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType) {
    final NonEmptyList<Pojo> mappedPojos =
        pojos.map(
            pojo -> pojo.inlineObjectReference(referenceName, referenceDescription, referenceType));
    return new AnyOfComposition(mappedPojos);
  }

  public AnyOfComposition adjustNullablePojo(PojoName nullablePojo) {
    final NonEmptyList<Pojo> mappedPojos = pojos.map(pojo -> pojo.adjustNullablePojo(nullablePojo));
    return new AnyOfComposition(mappedPojos);
  }

  public AnyOfComposition applyMapping(PojoNameMapping pojoNameMapping) {
    final NonEmptyList<Pojo> mappedPojos = pojos.map(pojo -> pojo.applyMapping(pojoNameMapping));
    return new AnyOfComposition(mappedPojos);
  }
}
