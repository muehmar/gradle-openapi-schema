package com.github.muehmar.gradle.openapi.generator.model;

import ch.bluecare.commons.data.PList;

public class ParsedSpecifications {
  private ParsedSpecifications() {}

  public static ParsedSpecification fromPojoSchemas(PojoSchema... pojoSchemas) {
    return new ParsedSpecification(PList.of(pojoSchemas), PList.empty());
  }

  public static ParsedSpecification fromPojoSchemas(Iterable<PojoSchema> pojoSchemas) {
    return new ParsedSpecification(PList.fromIter(pojoSchemas), PList.empty());
  }
}
