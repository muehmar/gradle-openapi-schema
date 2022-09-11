package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
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
}
