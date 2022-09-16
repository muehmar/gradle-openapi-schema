package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ArrayPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.CompletePojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ComposedPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.EnumPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.MemberPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ObjectPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.PojoSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.reader.SpecificationParser;
import com.github.muehmar.gradle.openapi.generator.mapper.resolver.PojoSchemaMapResultResolver;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;

class PojoMapperImpl implements PojoMapper {

  private final PojoSchemaMapResultResolver resolver;
  private final SpecificationParser specificationParser;

  private static final CompletePojoSchemaMapper COMPLETE_POJO_SCHEMA_MAPPER =
      new ArrayPojoSchemaMapper()
          .or(new ObjectPojoSchemaMapper())
          .or(new ComposedPojoSchemaMapper())
          .or(new EnumPojoSchemaMapper())
          .orLast(new MemberPojoSchemaMapper());

  private PojoMapperImpl(
      PojoSchemaMapResultResolver resolver, SpecificationParser specificationParser) {
    this.resolver = resolver;
    this.specificationParser = specificationParser;
  }

  public static PojoMapper create(
      PojoSchemaMapResultResolver resolver, SpecificationParser specificationParser) {
    return new PojoMapperImpl(resolver, specificationParser);
  }

  @Override
  public PList<Pojo> fromSpecification(MainDirectory mainDirectory, OpenApiSpec mainSpecification) {
    final PojoSchemaMapResult mapResult =
        mapNextSpecification(mainDirectory, PojoSchemaMapResult.ofSpecification(mainSpecification));
    return resolver.resolve(mapResult);
  }

  private PojoSchemaMapResult mapNextSpecification(
      MainDirectory mainDirectory, PojoSchemaMapResult mapResult) {
    return mapResult.popSpecification(
        (currentMapResult, nextSpecification) -> {
          final PList<PojoSchema> pojoSchemas =
              specificationParser.readSchemas(mainDirectory, nextSpecification);
          final PojoSchemaMapResult innerMapResult =
              pojoSchemas
                  .map(COMPLETE_POJO_SCHEMA_MAPPER::process)
                  .foldRight(currentMapResult, PojoSchemaMapResult::concat);
          return mapNextSpecification(mainDirectory, innerMapResult);
        });
  }
}
