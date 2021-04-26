package com.github.muehmar.gradle.openapi.generator.data;

import ch.bluecare.commons.data.PList;
import java.util.Objects;

/**
 * Represents a composition of other definitions, i.e. pojos. Distinguishes between different types
 * of compositions, see {@link CompositionType}.
 */
public class ComposedPojo {
  private final Name name;
  private final String description;
  private final String suffix;
  private final CompositionType type;
  private final PList<Name> pojoNames;
  private final PList<OpenApiPojo> openApiPojos;

  public enum CompositionType {
    ALL_OF,
    ANY_OF,
    ONE_OF;
  }

  public ComposedPojo(
      Name name,
      String description,
      String suffix,
      CompositionType type,
      PList<Name> pojoNames,
      PList<OpenApiPojo> openApiPojos) {
    this.name = name;
    this.description = description;
    this.suffix = suffix;
    this.type = type;
    this.pojoNames = pojoNames;
    this.openApiPojos = openApiPojos;
  }

  public Name getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getSuffix() {
    return suffix;
  }

  public CompositionType getType() {
    return type;
  }

  public PList<Name> getPojoNames() {
    return pojoNames;
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
    ComposedPojo that = (ComposedPojo) o;
    return Objects.equals(name, that.name)
        && Objects.equals(description, that.description)
        && Objects.equals(suffix, that.suffix)
        && type == that.type
        && Objects.equals(pojoNames, that.pojoNames)
        && Objects.equals(openApiPojos, that.openApiPojos);
  }

  @Override
  public int hashCode() {
    return Objects.hash(name, description, suffix, type, pojoNames, openApiPojos);
  }

  @Override
  public String toString() {
    return "ComposedPojo{"
        + "name='"
        + name
        + '\''
        + ", description='"
        + description
        + '\''
        + ", suffix='"
        + suffix
        + '\''
        + ", type="
        + type
        + ", pojoNames="
        + pojoNames
        + ", openApiPojos="
        + openApiPojos
        + '}';
  }
}
