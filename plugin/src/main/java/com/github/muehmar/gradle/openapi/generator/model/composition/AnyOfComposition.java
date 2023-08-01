package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class AnyOfComposition {
  NonEmptyList<Pojo> pojos;

  private AnyOfComposition(NonEmptyList<Pojo> pojos) {
    this.pojos = pojos;
  }

  public static AnyOfComposition fromPojos(NonEmptyList<Pojo> pojos) {
    return new AnyOfComposition(pojos);
  }

  public NonEmptyList<Pojo> getPojos() {
    return pojos;
  }
}
