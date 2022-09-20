package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MapResult {
  private final PList<Pojo> pojos;
  private final PList<OpenApiSpec> usedSpecs;

  private MapResult(PList<Pojo> pojos, PList<OpenApiSpec> usedSpecs) {
    this.pojos = pojos;
    this.usedSpecs = usedSpecs;
  }

  public static MapResult of(PList<Pojo> pojos, PList<OpenApiSpec> usedSpecs) {
    return new MapResult(pojos, usedSpecs);
  }

  public PList<Pojo> getPojos() {
    return pojos;
  }

  public PList<OpenApiSpec> getUsedSpecs() {
    return usedSpecs;
  }
}
