package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class TypeMapResult {
  private final Type type;
  private final PList<OpenApiPojo> openApiPojos;

  private TypeMapResult(Type type, PList<OpenApiPojo> openApiPojos) {
    this.type = type;
    this.openApiPojos = openApiPojos;
  }

  public static TypeMapResult ofType(Type type) {
    return new TypeMapResult(type, PList.empty());
  }

  public static TypeMapResult ofTypeAndOpenApiPojo(Type type, OpenApiPojo openApiPojo) {
    return new TypeMapResult(type, PList.single(openApiPojo));
  }

  public TypeMapResult mapType(UnaryOperator<Type> mapType) {
    return new TypeMapResult(mapType.apply(type), openApiPojos);
  }

  public Type getType() {
    return type;
  }

  public PList<OpenApiPojo> getOpenApiPojos() {
    return openApiPojos;
  }
}
