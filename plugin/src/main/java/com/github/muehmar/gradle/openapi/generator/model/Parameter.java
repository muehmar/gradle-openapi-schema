package com.github.muehmar.gradle.openapi.generator.model;

import io.swagger.v3.oas.models.media.Schema;
import lombok.Value;

@Value
public class Parameter {
  String name;
  Schema<?> schema;
}
