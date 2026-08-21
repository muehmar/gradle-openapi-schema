package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class AllOfComposition {
  NonEmptyList<Pojo> pojos;

  private AllOfComposition(NonEmptyList<Pojo> pojos) {
    this.pojos = pojos;
  }

  public static AllOfComposition fromPojos(NonEmptyList<Pojo> pojos) {
    return new AllOfComposition(pojos);
  }

  public NonEmptyList<Pojo> getPojos() {
    return pojos;
  }

  public AllOfComposition replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType) {
    final NonEmptyList<Pojo> mappedPojos =
        pojos.map(
            pojo ->
                pojo.replaceObjectType(objectTypeName, newObjectTypeDescription, newObjectType));
    return new AllOfComposition(mappedPojos);
  }

  public AllOfComposition adjustNullablePojo(PojoName nullablePojo) {
    final NonEmptyList<Pojo> mappedPojos = pojos.map(pojo -> pojo.adjustNullablePojo(nullablePojo));
    return new AllOfComposition(mappedPojos);
  }

  public AllOfComposition applyMapping(PojoNameMapping pojoNameMapping) {
    final NonEmptyList<Pojo> mappedPojos = pojos.map(pojo -> pojo.applyMapping(pojoNameMapping));
    return new AllOfComposition(mappedPojos);
  }
}
