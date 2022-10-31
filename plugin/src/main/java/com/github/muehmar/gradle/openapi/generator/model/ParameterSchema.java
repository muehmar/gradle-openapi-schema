package com.github.muehmar.gradle.openapi.generator.model;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Value;

@Value
public class ParameterSchema {
  Name name;
  Schema<?> schema;
}
