package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.FileSchema;

public class FileSchemaMapper extends SimpleSchemaMapper<FileSchema> {
  public FileSchemaMapper(JavaSchemaMapper nextMapper) {
    super(FileSchema.class, JavaTypes.STRING, nextMapper);
  }
}
