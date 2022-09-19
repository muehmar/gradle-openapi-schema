package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MapResult {

  private final PList<Pojo> pojos;
  private final PList<ComposedPojo> composedPojos;
  private final PList<PojoMemberReference> pojoMemberReferences;

  private MapResult(
      PList<Pojo> pojos,
      PList<ComposedPojo> composedPojos,
      PList<PojoMemberReference> pojoMemberReferences) {
    this.pojos = pojos;
    this.composedPojos = composedPojos;
    this.pojoMemberReferences = pojoMemberReferences;
  }

  public static MapResult empty() {
    return new MapResult(PList.empty(), PList.empty(), PList.empty());
  }

  public static MapResult ofPojo(Pojo pojo) {
    return new MapResult(PList.single(pojo), PList.empty(), PList.empty());
  }

  public static MapResult ofComposedPojo(ComposedPojo composedPojo) {
    return new MapResult(PList.empty(), PList.single(composedPojo), PList.empty());
  }

  public static MapResult ofPojoMemberReference(PojoMemberReference pojoMemberReference) {
    return new MapResult(PList.empty(), PList.empty(), PList.single(pojoMemberReference));
  }

  public MapResult merge(MapResult other) {
    return new MapResult(
        pojos.concat(other.pojos),
        composedPojos.concat(other.composedPojos),
        pojoMemberReferences.concat(other.pojoMemberReferences));
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
}
