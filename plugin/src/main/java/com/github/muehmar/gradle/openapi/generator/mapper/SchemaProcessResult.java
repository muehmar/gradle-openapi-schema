package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMemberReference;
import java.util.Objects;

class SchemaProcessResult {
  private final PList<Pojo> pojos;
  private final PList<ComposedPojo> composedPojos;
  private final PList<PojoMemberReference> pojoMemberReferences;

  public SchemaProcessResult(
      PList<Pojo> pojos,
      PList<ComposedPojo> composedPojos,
      PList<PojoMemberReference> pojoMemberReferences) {
    this.pojos = pojos;
    this.composedPojos = composedPojos;
    this.pojoMemberReferences = pojoMemberReferences;
  }

  public static SchemaProcessResult empty() {
    return new SchemaProcessResult(PList.empty(), PList.empty(), PList.empty());
  }

  public static SchemaProcessResult ofPojo(Pojo pojo) {
    return empty().addPojo(pojo);
  }

  public static SchemaProcessResult ofComposedPojo(ComposedPojo composedPojo) {
    return empty().addComposedPojo(composedPojo);
  }

  public static SchemaProcessResult ofPojoMemberReference(PojoMemberReference pojoMemberReference) {
    return empty().addPojoMemberReference(pojoMemberReference);
  }

  public PList<Pojo> getPojos() {
    return pojos;
  }

  public PList<ComposedPojo> getComposedPojos() {
    return composedPojos;
  }

  public PList<PojoMemberReference> getPojoMemberReferences() {
    return pojoMemberReferences;
  }

  public SchemaProcessResult concat(SchemaProcessResult other) {
    return new SchemaProcessResult(
        pojos.concat(other.pojos), composedPojos.concat(other.composedPojos), pojoMemberReferences);
  }

  public SchemaProcessResult addComposedPojo(ComposedPojo composedPojo) {
    return new SchemaProcessResult(pojos, composedPojos.cons(composedPojo), pojoMemberReferences);
  }

  public SchemaProcessResult addPojo(Pojo pojo) {
    return new SchemaProcessResult(pojos.cons(pojo), composedPojos, pojoMemberReferences);
  }

  public SchemaProcessResult addPojoMemberReference(PojoMemberReference pojoMemberReference) {
    return new SchemaProcessResult(
        pojos, composedPojos, pojoMemberReferences.cons(pojoMemberReference));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    SchemaProcessResult that = (SchemaProcessResult) o;
    return Objects.equals(pojos, that.pojos)
        && Objects.equals(composedPojos, that.composedPojos)
        && Objects.equals(pojoMemberReferences, that.pojoMemberReferences);
  }

  @Override
  public int hashCode() {
    return Objects.hash(pojos, composedPojos, pojoMemberReferences);
  }

  @Override
  public String toString() {
    return "SchemaProcessResult{"
        + "pojos="
        + pojos
        + ", composedPojos="
        + composedPojos
        + ", pojoMemberReferences="
        + pojoMemberReferences
        + '}';
  }
}
