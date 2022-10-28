package com.github.muehmar.gradle.openapi.generator.model;

import ch.bluecare.commons.data.PList;
import lombok.Value;

@Value
public class ParsedSpecification {
  PList<PojoSchema> pojoSchemas;
  PList<Parameter> parameters;
}
