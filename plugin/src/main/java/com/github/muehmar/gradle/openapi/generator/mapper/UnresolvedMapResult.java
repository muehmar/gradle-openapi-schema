package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMemberReference;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedSchemaReference;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Result of the mapping from OpenAPI models to internal models. But certain models may not be
 * resolved yet.
 */
@EqualsAndHashCode
@ToString
@PojoBuilder
public class UnresolvedMapResult {

  private final PList<Pojo> pojos;
  private final PList<UnresolvedObjectPojo> unresolvedObjectPojos;
  private final PList<PojoMemberReference> pojoMemberReferences;
  private final PList<UnresolvedSchemaReference> unresolvedSchemaReferences;
  private final PList<Parameter> parameters;
  private final PList<OpenApiSpec> usedSpecs;

  UnresolvedMapResult(
      PList<Pojo> pojos,
      PList<UnresolvedObjectPojo> unresolvedObjectPojos,
      PList<PojoMemberReference> pojoMemberReferences,
      PList<UnresolvedSchemaReference> unresolvedSchemaReferences,
      PList<Parameter> parameters,
      PList<OpenApiSpec> usedSpecs) {
    this.pojos = pojos;
    this.unresolvedObjectPojos = unresolvedObjectPojos;
    this.pojoMemberReferences = pojoMemberReferences;
    this.unresolvedSchemaReferences = unresolvedSchemaReferences;
    this.parameters = parameters;
    this.usedSpecs = usedSpecs;
  }

  public static UnresolvedMapResult empty() {
    return UnresolvedMapResultBuilder.create()
        .pojos(PList.empty())
        .unresolvedObjectPojos(PList.empty())
        .pojoMemberReferences(PList.empty())
        .unresolvedSchemaReferences(PList.empty())
        .parameters(PList.empty())
        .usedSpecs(PList.empty())
        .build();
  }

  public static UnresolvedMapResult ofPojo(Pojo pojo) {
    return UnresolvedMapResultBuilder.create()
        .pojos(PList.single(pojo))
        .unresolvedObjectPojos(PList.empty())
        .pojoMemberReferences(PList.empty())
        .unresolvedSchemaReferences(PList.empty())
        .parameters(PList.empty())
        .usedSpecs(PList.empty())
        .build();
  }

  public static UnresolvedMapResult ofUnresolvedObjectPojo(
      UnresolvedObjectPojo unresolvedObjectPojo) {
    return UnresolvedMapResultBuilder.create()
        .pojos(PList.empty())
        .unresolvedObjectPojos(PList.single(unresolvedObjectPojo))
        .pojoMemberReferences(PList.empty())
        .unresolvedSchemaReferences(PList.empty())
        .parameters(PList.empty())
        .usedSpecs(PList.empty())
        .build();
  }

  public static UnresolvedMapResult ofPojoMemberReference(PojoMemberReference pojoMemberReference) {
    return UnresolvedMapResultBuilder.create()
        .pojos(PList.empty())
        .unresolvedObjectPojos(PList.empty())
        .pojoMemberReferences(PList.single(pojoMemberReference))
        .unresolvedSchemaReferences(PList.empty())
        .parameters(PList.empty())
        .usedSpecs(PList.empty())
        .build();
  }

  public static UnresolvedMapResult ofUnresolvedSchemaReference(
      UnresolvedSchemaReference unresolvedSchemaReference) {
    return UnresolvedMapResultBuilder.create()
        .pojos(PList.empty())
        .unresolvedObjectPojos(PList.empty())
        .pojoMemberReferences(PList.empty())
        .unresolvedSchemaReferences(PList.single(unresolvedSchemaReference))
        .parameters(PList.empty())
        .usedSpecs(PList.empty())
        .build();
  }

  public static UnresolvedMapResult ofUsedSpecs(PList<OpenApiSpec> usedSpecs) {
    return UnresolvedMapResultBuilder.create()
        .pojos(PList.empty())
        .unresolvedObjectPojos(PList.empty())
        .pojoMemberReferences(PList.empty())
        .unresolvedSchemaReferences(PList.empty())
        .parameters(PList.empty())
        .usedSpecs(usedSpecs)
        .build();
  }

  public UnresolvedMapResult merge(UnresolvedMapResult other) {
    return new UnresolvedMapResult(
        pojos.concat(other.pojos),
        unresolvedObjectPojos.concat(other.unresolvedObjectPojos),
        pojoMemberReferences.concat(other.pojoMemberReferences),
        unresolvedSchemaReferences.concat(other.unresolvedSchemaReferences),
        parameters.concat(other.parameters),
        usedSpecs.concat(other.usedSpecs));
  }

  public UnresolvedMapResult addParameters(PList<Parameter> parameters) {
    return new UnresolvedMapResult(
        pojos,
        unresolvedObjectPojos,
        pojoMemberReferences,
        unresolvedSchemaReferences,
        this.parameters.concat(parameters),
        usedSpecs);
  }

  public PList<Pojo> getPojos() {
    return pojos;
  }

  public PList<UnresolvedObjectPojo> getUnresolvedObjectPojos() {
    return unresolvedObjectPojos;
  }

  public PList<PojoMemberReference> getPojoMemberReferences() {
    return pojoMemberReferences;
  }

  public PList<UnresolvedSchemaReference> getUnresolvedSchemaReferences() {
    return unresolvedSchemaReferences;
  }

  public PList<Parameter> getParameters() {
    return parameters;
  }

  public PList<OpenApiSpec> getUsedSpecs() {
    return usedSpecs;
  }
}
