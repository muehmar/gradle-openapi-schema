package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
/** Data class holding the result of processing a schema as a member of a pojo. */
public class PojoMemberProcessResult {
  private final PojoMember pojoMember;
  private final PList<OpenApiPojo> openApiPojos;

  public PojoMemberProcessResult(PojoMember pojoMember, PList<OpenApiPojo> openApiPojos) {
    this.pojoMember = pojoMember;
    this.openApiPojos = openApiPojos;
  }

  public PojoMember getPojoMember() {
    return pojoMember;
  }

  public PList<OpenApiPojo> getOpenApiPojos() {
    return openApiPojos;
  }
}
