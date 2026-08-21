package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.settings.ExcludedSchemas;

public interface SpecificationMapper {

  MapResult map(
      MainDirectory mainDirectory, OpenApiSpec mainSpecification, ExcludedSchemas excludedSchemas);

  default MapResult map(MainDirectory mainDirectory, OpenApiSpec mainSpecification) {
    return map(
        mainDirectory, mainSpecification, ExcludedSchemas.fromExcludedPojoNames(PList.empty()));
  }
}
