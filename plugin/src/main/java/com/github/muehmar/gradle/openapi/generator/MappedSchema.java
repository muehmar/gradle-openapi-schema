package com.github.muehmar.gradle.openapi.generator;

import ch.bluecare.commons.data.PList;
import java.util.Objects;
import java.util.function.UnaryOperator;

public class MappedSchema<T extends Type> {
  private final T type;
  private final PList<OpenApiPojo> openApiPojos;

  public MappedSchema(T type, PList<OpenApiPojo> openApiPojos) {
    this.type = type;
    this.openApiPojos = openApiPojos;
  }

  public static <R extends Type> MappedSchema<R> ofType(R type) {
    return new MappedSchema<>(type, PList.empty());
  }

  public static <R extends Type> MappedSchema<R> ofTypeAndOpenApiPojo(
      R type, OpenApiPojo openApiPojo) {
    return new MappedSchema<>(type, PList.single(openApiPojo));
  }

  public static <R extends Type> MappedSchema<R> ofTypeAndOpenApiPojos(
      R type, PList<OpenApiPojo> openApiPojos) {
    return new MappedSchema<>(type, openApiPojos);
  }

  public T getType() {
    return type;
  }

  public PList<OpenApiPojo> getOpenApiPojos() {
    return openApiPojos;
  }

  public MappedSchema<T> addOpenApiPojo(OpenApiPojo openApiPojo) {
    return new MappedSchema<>(type, openApiPojos.cons(openApiPojo));
  }

  public MappedSchema<T> addOpenApiPojos(PList<OpenApiPojo> openApiPojos) {
    return new MappedSchema<>(type, openApiPojos.concat(openApiPojos));
  }

  public MappedSchema<T> mapType(UnaryOperator<T> mapType) {
    return new MappedSchema<>(mapType.apply(type), openApiPojos);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    MappedSchema<?> that = (MappedSchema<?>) o;
    return Objects.equals(type, that.type) && Objects.equals(openApiPojos, that.openApiPojos);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, openApiPojos);
  }

  @Override
  public String toString() {
    return "MappedSchema{" + "type=" + type + ", openApiPojos=" + openApiPojos + '}';
  }
}
