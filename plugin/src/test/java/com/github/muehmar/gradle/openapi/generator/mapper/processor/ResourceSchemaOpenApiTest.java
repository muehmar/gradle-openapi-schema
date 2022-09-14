package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.SpecificationMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.SpecificationMapperImpl;
import com.github.muehmar.gradle.openapi.generator.mapper.reader.ResourceSpecificationReader;
import com.github.muehmar.gradle.openapi.generator.mapper.reader.SwaggerSpecificationParser;
import com.github.muehmar.gradle.openapi.generator.mapper.resolver.MapResultResolverImpl;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import java.util.Comparator;

public abstract class ResourceSchemaOpenApiTest {

  protected static PList<Pojo> processSchema(String mainDirectory, String schema) {
    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            new SwaggerSpecificationParser(new ResourceSpecificationReader(), "Dto"));

    return specificationMapper
        .map(MainDirectory.fromString(mainDirectory), OpenApiSpec.fromString(schema))
        .getPojos()
        .sort(Comparator.comparing(pojo -> pojo.getName().asString()));
  }
}
