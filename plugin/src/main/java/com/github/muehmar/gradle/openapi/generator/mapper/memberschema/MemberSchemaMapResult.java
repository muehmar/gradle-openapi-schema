package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import java.util.Optional;
import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MemberSchemaMapResult {
  private final Type type;
  private final PList<PojoSchema> pojoSchemas;
  private final PList<OpenApiSpec> remoteSpecs;

  private MemberSchemaMapResult(
      Type type, PList<PojoSchema> pojoSchemas, PList<OpenApiSpec> remoteSpecs) {
    this.type = type;
    this.pojoSchemas = pojoSchemas;
    this.remoteSpecs = remoteSpecs;
  }

  public static MemberSchemaMapResult ofType(Type type) {
    return new MemberSchemaMapResult(type, PList.empty(), PList.empty());
  }

  public static MemberSchemaMapResult ofTypeAndOpenApiPojo(Type type, PojoSchema pojoSchema) {
    return new MemberSchemaMapResult(type, PList.single(pojoSchema), PList.empty());
  }

  public MemberSchemaMapResult mapType(UnaryOperator<Type> mapType) {
    return new MemberSchemaMapResult(mapType.apply(type), pojoSchemas, remoteSpecs);
  }

  public MemberSchemaMapResult addOpenApiSpec(Optional<OpenApiSpec> spec) {
    return new MemberSchemaMapResult(
        type, pojoSchemas, remoteSpecs.concat(PList.fromOptional(spec)));
  }

  public Type getType() {
    return type;
  }

  public PList<PojoSchema> getPojoSchemas() {
    return pojoSchemas;
  }

  public PList<OpenApiSpec> getRemoteSpecs() {
    return remoteSpecs;
  }
}
