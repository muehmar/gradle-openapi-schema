package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.NewPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
/** Data class holding the result of processing a schema as a member of a pojo. */
public class NewPojoMemberProcessResult {
  private final NewPojoMember pojoMember;
  private final PList<OpenApiPojo> openApiPojos;

  public NewPojoMemberProcessResult(NewPojoMember pojoMember, PList<OpenApiPojo> openApiPojos) {
    this.pojoMember = pojoMember;
    this.openApiPojos = openApiPojos;
  }

  public NewPojoMember getPojoMember() {
    return pojoMember;
  }

  public PList<OpenApiPojo> getOpenApiPojos() {
    return openApiPojos;
  }
}
