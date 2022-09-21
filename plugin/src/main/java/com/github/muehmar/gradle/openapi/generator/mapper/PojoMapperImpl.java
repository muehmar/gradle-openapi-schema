package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ArrayPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.CompletePojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ComposedPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.EnumPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.MemberPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ObjectPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.reader.SpecificationParser;
import com.github.muehmar.gradle.openapi.generator.mapper.resolver.MapResultResolver;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;

class PojoMapperImpl implements PojoMapper {

  private final MapResultResolver resolver;
  private final SpecificationParser specificationParser;

  private static final CompletePojoSchemaMapper COMPLETE_POJO_SCHEMA_MAPPER =
      new ArrayPojoSchemaMapper()
          .or(new ObjectPojoSchemaMapper())
          .or(new ComposedPojoSchemaMapper())
          .or(new EnumPojoSchemaMapper())
          .orLast(new MemberPojoSchemaMapper());

  private PojoMapperImpl(MapResultResolver resolver, SpecificationParser specificationParser) {
    this.resolver = resolver;
    this.specificationParser = specificationParser;
  }

  public static PojoMapper create(
      MapResultResolver resolver, SpecificationParser specificationParser) {
    return new PojoMapperImpl(resolver, specificationParser);
  }

  @Override
  public MapResult fromSpecification(MainDirectory mainDirectory, OpenApiSpec mainSpecification) {
    final MapContext mapContext = MapContext.fromInitialSpecification(mainSpecification);
    final UnresolvedMapResult unresolvedMapResult = processMapContext(mainDirectory, mapContext);
    return resolver.resolve(unresolvedMapResult);
  }

  private UnresolvedMapResult processMapContext(
      MainDirectory mainDirectory, MapContext mapContext) {
    return mapContext.onUnmappedItems(
        (ctx, specs) -> {
          final PList<PojoSchema> pojoSchemas =
              specs.toPList().flatMap(spec -> specificationParser.parse(mainDirectory, spec));
          return processMapContext(mainDirectory, ctx.addPojoSchemas(pojoSchemas));
        },
        (ctx, schemas) -> {
          final MapContext resultingContext =
              schemas.map(COMPLETE_POJO_SCHEMA_MAPPER::map).reduce(MapContext::merge);
          return processMapContext(mainDirectory, ctx.merge(resultingContext));
        });
  }
}
