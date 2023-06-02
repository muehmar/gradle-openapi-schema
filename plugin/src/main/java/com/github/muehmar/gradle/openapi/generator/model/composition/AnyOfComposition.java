package com.github.muehmar.gradle.openapi.generator.model.composition;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class AnyOfComposition {
  PList<Pojo> pojos;

  private AnyOfComposition(PList<Pojo> pojos) {
    this.pojos = pojos;
  }

  public static AnyOfComposition fromPojos(PList<Pojo> pojos) {
    return new AnyOfComposition(pojos);
  }

  public PList<Pojo> getPojos() {
    return pojos;
  }
}
