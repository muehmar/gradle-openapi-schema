package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class AllOfComposition {
  PList<Pojo> pojos;

  private AllOfComposition(PList<Pojo> pojos) {
    this.pojos = pojos;
  }

  public static AllOfComposition fromPojos(PList<Pojo> pojos) {
    return new AllOfComposition(pojos);
  }

  public PList<Pojo> getPojos() {
    return pojos;
  }
}
