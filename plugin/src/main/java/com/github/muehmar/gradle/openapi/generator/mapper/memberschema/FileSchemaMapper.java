package com.github.muehmar.gradle.openapi.generator.mapper.memberschema;

import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.FileSchema;

public class FileSchemaMapper extends SimpleSchemaMapper<FileSchema> {
  FileSchemaMapper() {
    super(FileSchema.class, StringType.ofFormat(StringType.Format.BINARY));
  }
}
