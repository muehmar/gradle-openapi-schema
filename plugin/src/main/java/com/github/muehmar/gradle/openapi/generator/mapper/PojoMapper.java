package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;

public interface PojoMapper {

  MapResult fromSpecification(
      MainDirectory mainDirectory,
      OpenApiSpec mainSpecification,
      PList<PojoName> excludedPojoNames);

  default MapResult fromSpecification(MainDirectory mainDirectory, OpenApiSpec mainSpecification) {
    return fromSpecification(mainDirectory, mainSpecification, PList.empty());
  }
}
