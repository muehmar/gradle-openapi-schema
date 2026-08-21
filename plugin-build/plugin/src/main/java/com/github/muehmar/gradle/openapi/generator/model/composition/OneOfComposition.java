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
public class OneOfComposition {
  private final NonEmptyList<Pojo> pojos;

  OneOfComposition(NonEmptyList<Pojo> pojos) {
    this.pojos = pojos;
  }

  public Optional<Discriminator> determineDiscriminator(
      Optional<UntypedDiscriminator> objectPojoDiscriminator) {
    final DiscriminatorDeterminator discriminatorDeterminator =
        new DiscriminatorDeterminator(pojos);
    return discriminatorDeterminator.determineDiscriminator(objectPojoDiscriminator);
  }

  public static OneOfComposition fromPojos(NonEmptyList<Pojo> pojos) {
    return new OneOfComposition(pojos);
  }

  public NonEmptyList<Pojo> getPojos() {
    return pojos;
  }

  public OneOfComposition replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType) {
    final NonEmptyList<Pojo> mappedPojos =
        pojos.map(
            pojo ->
                pojo.replaceObjectType(objectTypeName, newObjectTypeDescription, newObjectType));
    return new OneOfComposition(mappedPojos);
  }

  public OneOfComposition adjustNullablePojo(PojoName nullablePojo) {
    final NonEmptyList<Pojo> mappedPojos = pojos.map(pojo -> pojo.adjustNullablePojo(nullablePojo));
    return new OneOfComposition(mappedPojos);
  }

  public OneOfComposition applyMapping(PojoNameMapping pojoNameMapping) {
    final NonEmptyList<Pojo> mappedPojos = pojos.map(pojo -> pojo.applyMapping(pojoNameMapping));
    return new OneOfComposition(mappedPojos);
  }
}
