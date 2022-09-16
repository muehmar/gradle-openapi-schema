package com.github.muehmar.gradle.openapi.generator.mapper.reader;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;

public interface SpecificationParser {
  PList<PojoSchema> readSchemas(MainDirectory mainDirectory, OpenApiSpec spec);
}
