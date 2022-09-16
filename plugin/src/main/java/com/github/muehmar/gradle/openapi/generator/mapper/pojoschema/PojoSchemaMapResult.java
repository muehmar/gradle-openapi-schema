package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class PojoSchemaMapResult {
  private final PList<Pojo> pojos;
  private final PList<ComposedPojo> composedPojos;
  private final PList<PojoMemberReference> pojoMemberReferences;

  public PojoSchemaMapResult(
      PList<Pojo> pojos,
      PList<ComposedPojo> composedPojos,
      PList<PojoMemberReference> pojoMemberReferences) {
    this.pojos = pojos;
    this.composedPojos = composedPojos;
    this.pojoMemberReferences = pojoMemberReferences;
  }

  public static PojoSchemaMapResult empty() {
    return new PojoSchemaMapResult(PList.empty(), PList.empty(), PList.empty());
  }

  public static PojoSchemaMapResult ofPojo(Pojo pojo) {
    return empty().addPojo(pojo);
  }

  public static PojoSchemaMapResult ofComposedPojo(ComposedPojo composedPojo) {
    return empty().addComposedPojo(composedPojo);
  }

  public static PojoSchemaMapResult ofPojoMemberReference(PojoMemberReference pojoMemberReference) {
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

  public PojoSchemaMapResult concat(PojoSchemaMapResult other) {
    return new PojoSchemaMapResult(
        pojos.concat(other.pojos), composedPojos.concat(other.composedPojos), pojoMemberReferences);
  }

  public PojoSchemaMapResult addComposedPojo(ComposedPojo composedPojo) {
    return new PojoSchemaMapResult(pojos, composedPojos.cons(composedPojo), pojoMemberReferences);
  }

  public PojoSchemaMapResult addPojo(Pojo pojo) {
    return new PojoSchemaMapResult(pojos.cons(pojo), composedPojos, pojoMemberReferences);
  }

  public PojoSchemaMapResult addPojoMemberReference(PojoMemberReference pojoMemberReference) {
    return new PojoSchemaMapResult(
        pojos, composedPojos, pojoMemberReferences.cons(pojoMemberReference));
  }
}
