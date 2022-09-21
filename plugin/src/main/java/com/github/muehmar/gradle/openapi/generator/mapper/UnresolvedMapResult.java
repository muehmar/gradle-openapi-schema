package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class UnresolvedMapResult {

  private final PList<Pojo> pojos;
  private final PList<ComposedPojo> composedPojos;
  private final PList<PojoMemberReference> pojoMemberReferences;
  private final PList<OpenApiSpec> usedSpecs;

  private UnresolvedMapResult(
      PList<Pojo> pojos,
      PList<ComposedPojo> composedPojos,
      PList<PojoMemberReference> pojoMemberReferences,
      PList<OpenApiSpec> usedSpecs) {
    this.pojos = pojos;
    this.composedPojos = composedPojos;
    this.pojoMemberReferences = pojoMemberReferences;
    this.usedSpecs = usedSpecs;
  }

  public static UnresolvedMapResult empty() {
    return new UnresolvedMapResult(PList.empty(), PList.empty(), PList.empty(), PList.empty());
  }

  public static UnresolvedMapResult ofPojo(Pojo pojo) {
    return new UnresolvedMapResult(PList.single(pojo), PList.empty(), PList.empty(), PList.empty());
  }

  public static UnresolvedMapResult ofComposedPojo(ComposedPojo composedPojo) {
    return new UnresolvedMapResult(
        PList.empty(), PList.single(composedPojo), PList.empty(), PList.empty());
  }

  public static UnresolvedMapResult ofPojoMemberReference(PojoMemberReference pojoMemberReference) {
    return new UnresolvedMapResult(
        PList.empty(), PList.empty(), PList.single(pojoMemberReference), PList.empty());
  }

  public static UnresolvedMapResult ofUsedSpecs(PList<OpenApiSpec> usedSpecs) {
    return new UnresolvedMapResult(PList.empty(), PList.empty(), PList.empty(), usedSpecs);
  }

  public UnresolvedMapResult merge(UnresolvedMapResult other) {
    return new UnresolvedMapResult(
        pojos.concat(other.pojos),
        composedPojos.concat(other.composedPojos),
        pojoMemberReferences.concat(other.pojoMemberReferences),
        usedSpecs.concat(other.usedSpecs));
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

  public PList<OpenApiSpec> getUsedSpecs() {
    return usedSpecs;
  }
}
