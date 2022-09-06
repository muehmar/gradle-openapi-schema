package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewPojoMemberReference;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
class NewSchemaProcessResult {
  private final PList<NewPojo> pojos;
  private final PList<ComposedPojo> composedPojos;
  private final PList<NewPojoMemberReference> pojoMemberReferences;

  public NewSchemaProcessResult(
      PList<NewPojo> pojos,
      PList<ComposedPojo> composedPojos,
      PList<NewPojoMemberReference> pojoMemberReferences) {
    this.pojos = pojos;
    this.composedPojos = composedPojos;
    this.pojoMemberReferences = pojoMemberReferences;
  }

  public static NewSchemaProcessResult empty() {
    return new NewSchemaProcessResult(PList.empty(), PList.empty(), PList.empty());
  }

  public static NewSchemaProcessResult ofPojo(NewPojo pojo) {
    return empty().addPojo(pojo);
  }

  public static NewSchemaProcessResult ofComposedPojo(ComposedPojo composedPojo) {
    return empty().addComposedPojo(composedPojo);
  }

  public static NewSchemaProcessResult ofPojoMemberReference(
      NewPojoMemberReference pojoMemberReference) {
    return empty().addPojoMemberReference(pojoMemberReference);
  }

  public PList<NewPojo> getPojos() {
    return pojos;
  }

  public PList<ComposedPojo> getComposedPojos() {
    return composedPojos;
  }

  public PList<NewPojoMemberReference> getPojoMemberReferences() {
    return pojoMemberReferences;
  }

  public NewSchemaProcessResult concat(NewSchemaProcessResult other) {
    return new NewSchemaProcessResult(
        pojos.concat(other.pojos), composedPojos.concat(other.composedPojos), pojoMemberReferences);
  }

  public NewSchemaProcessResult addComposedPojo(ComposedPojo composedPojo) {
    return new NewSchemaProcessResult(
        pojos, composedPojos.cons(composedPojo), pojoMemberReferences);
  }

  public NewSchemaProcessResult addPojo(NewPojo pojo) {
    return new NewSchemaProcessResult(pojos.cons(pojo), composedPojos, pojoMemberReferences);
  }

  public NewSchemaProcessResult addPojoMemberReference(NewPojoMemberReference pojoMemberReference) {
    return new NewSchemaProcessResult(
        pojos, composedPojos, pojoMemberReferences.cons(pojoMemberReference));
  }
}
