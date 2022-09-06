package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
/** Data class holding the result of processing a schema as a pojo. */
public class NewPojoProcessResult {
  private final NewPojo pojo;
  private final PList<OpenApiPojo> openApiPojos;

  public NewPojoProcessResult(NewPojo pojo, PList<OpenApiPojo> openApiPojos) {
    this.pojo = pojo;
    this.openApiPojos = openApiPojos;
  }

  public NewPojo getPojo() {
    return pojo;
  }

  public PList<OpenApiPojo> getOpenApiPojos() {
    return openApiPojos;
  }
}
