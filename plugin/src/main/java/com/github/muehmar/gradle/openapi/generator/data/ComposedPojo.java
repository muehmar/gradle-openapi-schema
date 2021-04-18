package com.github.muehmar.gradle.openapi.generator.data;

import ch.bluecare.commons.data.PList;
import java.util.Objects;

/**
 * Represents a composition of other definitions, i.e. pojos. Distinguishes between different types
 * of compositions, see {@link CompositionType}.
 */
public class ComposedPojo {
  private final String key;
  private final String description;
  private final String suffix;
  private final CompositionType type;
  private final PList<String> pojoNames;
  private final PList<OpenApiPojo> openApiPojos;

  public enum CompositionType {
    ALL_OF,
    ANY_OF,
    ONE_OF;
  }

  public ComposedPojo(
      String key,
      String description,
      String suffix,
      CompositionType type,
      PList<String> pojoNames,
      PList<OpenApiPojo> openApiPojos) {
    this.key = key;
    this.description = description;
    this.suffix = suffix;
    this.type = type;
    this.pojoNames = pojoNames;
    this.openApiPojos = openApiPojos;
  }

  public String getKey() {
    return key;
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

  public PList<String> getPojoNames() {
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
    return Objects.equals(key, that.key)
        && Objects.equals(description, that.description)
        && Objects.equals(suffix, that.suffix)
        && type == that.type
        && Objects.equals(pojoNames, that.pojoNames)
        && Objects.equals(openApiPojos, that.openApiPojos);
  }

  @Override
  public int hashCode() {
    return Objects.hash(key, description, suffix, type, pojoNames, openApiPojos);
  }

  @Override
  public String toString() {
    return "ComposedPojo{"
        + "key='"
        + key
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
