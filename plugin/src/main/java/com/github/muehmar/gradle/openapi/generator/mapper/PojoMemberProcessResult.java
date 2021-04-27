package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PojoMemberProcessResult that = (PojoMemberProcessResult) o;
    return Objects.equals(pojoMember, that.pojoMember)
        && Objects.equals(openApiPojos, that.openApiPojos);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pojoMember, openApiPojos);
  }

  @Override
  public String toString() {
    return "PojoMemberProcessResult{"
        + "pojoMember="
        + pojoMember
        + ", openApiPojos="
        + openApiPojos
        + '}';
  }
}
