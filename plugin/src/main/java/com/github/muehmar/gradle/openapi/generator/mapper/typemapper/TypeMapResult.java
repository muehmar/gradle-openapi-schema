package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class TypeMapResult {
  private final NewType type;
  private final PList<OpenApiPojo> openApiPojos;

  private TypeMapResult(NewType type, PList<OpenApiPojo> openApiPojos) {
    this.type = type;
    this.openApiPojos = openApiPojos;
  }

  public static TypeMapResult ofType(NewType type) {
    return new TypeMapResult(type, PList.empty());
  }

  public static TypeMapResult ofTypeAndOpenApiPojo(NewType type, OpenApiPojo openApiPojo) {
    return new TypeMapResult(type, PList.single(openApiPojo));
  }

  public TypeMapResult mapType(UnaryOperator<NewType> mapType) {
    return new TypeMapResult(mapType.apply(type), openApiPojos);
  }

  public NewType getType() {
    return type;
  }

  public PList<OpenApiPojo> getOpenApiPojos() {
    return openApiPojos;
  }
}
