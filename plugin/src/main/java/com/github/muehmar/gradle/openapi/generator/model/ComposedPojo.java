package com.github.muehmar.gradle.openapi.generator.model;

import ch.bluecare.commons.data.PList;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Represents a composition of other definitions, i.e. pojos. Distinguishes between different types
 * of compositions, see {@link CompositionType}.
 */
@EqualsAndHashCode
@ToString
public class ComposedPojo {
  private final PojoName name;
  private final String description;
  private final CompositionType type;
  private final PList<PojoName> pojoNames;
  private final PList<OpenApiPojo> openApiPojos;

  public enum CompositionType {
    ALL_OF,
    ANY_OF,
    ONE_OF;
  }

  public ComposedPojo(
      PojoName name,
      String description,
      CompositionType type,
      PList<PojoName> pojoNames,
      PList<OpenApiPojo> openApiPojos) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.pojoNames = pojoNames;
    this.openApiPojos = openApiPojos;
  }

  public PojoName getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getSuffix() {
    return getName().getSuffix();
  }

  public CompositionType getType() {
    return type;
  }

  public PList<PojoName> getPojoNames() {
    return pojoNames;
  }

  public PList<OpenApiPojo> getOpenApiPojos() {
    return openApiPojos;
  }
}