package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import io.swagger.v3.oas.models.media.Schema;
import java.nio.file.Paths;

public class SchemaWrappers {
  private SchemaWrappers() {}

  public static SchemaWrapper wrap(Schema<?> wrapper) {
    return new SchemaWrapper(OpenApiSpec.fromPath(Paths.get(".")), wrapper);
  }
}
