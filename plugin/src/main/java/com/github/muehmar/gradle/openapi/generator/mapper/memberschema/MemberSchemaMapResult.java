package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MemberSchemaMapResult {
  private final Type type;
  private final PList<PojoSchema> pojoSchemas;

  private MemberSchemaMapResult(Type type, PList<PojoSchema> pojoSchemas) {
    this.type = type;
    this.pojoSchemas = pojoSchemas;
  }

  public static MemberSchemaMapResult ofType(Type type) {
    return new MemberSchemaMapResult(type, PList.empty());
  }

  public static MemberSchemaMapResult ofTypeAndOpenApiPojo(Type type, PojoSchema pojoSchema) {
    return new MemberSchemaMapResult(type, PList.single(pojoSchema));
  }

  public MemberSchemaMapResult mapType(UnaryOperator<Type> mapType) {
    return new MemberSchemaMapResult(mapType.apply(type), pojoSchemas);
  }

  public Type getType() {
    return type;
  }

  public PList<PojoSchema> getPojoSchemas() {
    return pojoSchemas;
  }
}
