package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
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
}
