package com.github.muehmar.gradle.openapi.generator.mapper;

import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;

public interface PojoMapper {

  MapResult fromSpecification(MainDirectory mainDirectory, OpenApiSpec mainSpecification);
}
