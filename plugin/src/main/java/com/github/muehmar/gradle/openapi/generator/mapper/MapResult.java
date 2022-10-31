package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MapResult {
  private final PList<Pojo> pojos;
  private final PList<Parameter> parameters;
  private final PList<OpenApiSpec> usedSpecs;

  private MapResult(PList<Pojo> pojos, PList<Parameter> parameters, PList<OpenApiSpec> usedSpecs) {
    this.pojos = pojos;
    this.parameters = parameters;
    this.usedSpecs = usedSpecs;
  }

  public static MapResult of(
      PList<Pojo> pojos, PList<Parameter> parameters, PList<OpenApiSpec> usedSpecs) {
    return new MapResult(pojos, parameters, usedSpecs);
  }

  public PList<Pojo> getPojos() {
    return pojos;
  }

  public PList<Parameter> getParameters() {
    return parameters;
  }

  public PList<OpenApiSpec> getUsedSpecs() {
    return usedSpecs;
  }
}
