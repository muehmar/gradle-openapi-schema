package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.reader.SpecificationParser;
import com.github.muehmar.gradle.openapi.generator.mapper.resolver.MapResultResolver;
import com.github.muehmar.gradle.openapi.generator.model.ParsedSpecification;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.schema.OpenApiSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.settings.ExcludedSchemas;

public class SpecificationMapperImpl implements SpecificationMapper {

  private final MapResultResolver resolver;
  private final SpecificationParser specificationParser;

  private SpecificationMapperImpl(
      MapResultResolver resolver, SpecificationParser specificationParser) {
    this.resolver = resolver;
    this.specificationParser = specificationParser;
  }

  public static SpecificationMapper create(
      MapResultResolver resolver, SpecificationParser specificationParser) {
    return new SpecificationMapperImpl(resolver, specificationParser);
  }

  @Override
  public MapResult map(
      MainDirectory mainDirectory, OpenApiSpec mainSpecification, ExcludedSchemas excludedSchemas) {
    final MapContext mapContext = MapContext.fromInitialSpecification(mainSpecification);
    final UnresolvedMapResult unresolvedMapResult =
        processMapContext(mainDirectory, mapContext, excludedSchemas);
    return resolver.resolve(unresolvedMapResult);
  }

  private UnresolvedMapResult processMapContext(
      MainDirectory mainDirectory, MapContext mapContext, ExcludedSchemas excludedSchemas) {
    return mapContext.onUnmappedItems(
        (ctx, specs) -> {
          final PList<ParsedSpecification> parsedSpecifications =
              specs.toPList().map(spec -> specificationParser.parse(mainDirectory, spec));
          final PList<PojoSchema> pojoSchemas =
              parsedSpecifications
                  .flatMap(ParsedSpecification::getPojoSchemas)
                  .filter(excludedSchemas.getSchemaFilter());
          final MapContext newMapContext = ctx.addPojoSchemas(pojoSchemas);
          return processMapContext(mainDirectory, newMapContext, excludedSchemas);
        },
        (ctx, schemas) -> {
          final MapContext resultingContext =
              schemas.map(PojoSchema::mapToPojo).reduce(MapContext::merge);
          return processMapContext(mainDirectory, ctx.merge(resultingContext), excludedSchemas);
        });
  }

  private MemberSchemaMapResult mapParameterSchema(OpenApiSchema schema) {
    final Name dummyPojoMemberName = Name.ofString("DummyPojoMemeberName");
    final ComponentName dummyParentName =
        ComponentName.fromSchemaStringAndSuffix("DummyParentName", "");
    return schema.mapToMemberType(dummyParentName, dummyPojoMemberName);
  }
}
