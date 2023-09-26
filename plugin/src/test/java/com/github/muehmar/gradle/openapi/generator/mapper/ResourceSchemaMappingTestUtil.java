package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.reader.ResourceSpecificationReader;
import com.github.muehmar.gradle.openapi.generator.mapper.reader.SwaggerSpecificationParser;
import com.github.muehmar.gradle.openapi.generator.mapper.resolver.MapResultResolverImpl;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import java.util.Comparator;

public class ResourceSchemaMappingTestUtil {
  private ResourceSchemaMappingTestUtil() {}

  public static PList<Pojo> mapSchema(String mainDirectory, String schema) {
    final SpecificationMapper specificationMapper =
        SpecificationMapperImpl.create(
            new MapResultResolverImpl(),
            new SwaggerSpecificationParser(new ResourceSpecificationReader(), "Dto"));

    return specificationMapper
        .map(MainDirectory.fromString(mainDirectory), OpenApiSpec.fromString(schema))
        .getPojos()
        .sort(Comparator.comparing(pojo -> pojo.getName().getPojoName().asString()));
  }
}
