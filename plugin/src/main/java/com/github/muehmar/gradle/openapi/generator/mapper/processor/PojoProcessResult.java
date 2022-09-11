package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
/** Data class holding the result of processing a schema as a pojo. */
public class PojoProcessResult {
  private final Pojo pojo;
  private final PList<OpenApiPojo> openApiPojos;

  public PojoProcessResult(Pojo pojo, PList<OpenApiPojo> openApiPojos) {
    this.pojo = pojo;
    this.openApiPojos = openApiPojos;
  }

  public Pojo getPojo() {
    return pojo;
  }

  public PList<OpenApiPojo> getOpenApiPojos() {
    return openApiPojos;
  }
}
