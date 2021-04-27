package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import java.util.Objects;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PojoProcessResult that = (PojoProcessResult) o;
    return Objects.equals(pojo, that.pojo) && Objects.equals(openApiPojos, that.openApiPojos);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pojo, openApiPojos);
  }

  @Override
  public String toString() {
    return "PojoProcessResult{" + "pojo=" + pojo + ", openApiPojos=" + openApiPojos + '}';
  }
}
