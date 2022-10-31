package com.github.muehmar.gradle.openapi.generator.mapper;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.CompleteMemberSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.CompleteMemberSchemaMapperFactory;
import com.github.muehmar.gradle.openapi.generator.mapper.memberschema.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ArrayPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.CompletePojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ComposedPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.EnumPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.MemberPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.ObjectPojoSchemaMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.reader.SpecificationParser;
import com.github.muehmar.gradle.openapi.generator.mapper.resolver.MapResultResolver;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.model.ParameterSchema;
import com.github.muehmar.gradle.openapi.generator.model.ParsedSpecification;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.specification.MainDirectory;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.settings.ExcludedSchemas;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

class SpecificationMapperImpl implements SpecificationMapper {

  private final MapResultResolver resolver;
  private final SpecificationParser specificationParser;

  private static final CompletePojoSchemaMapper COMPLETE_POJO_SCHEMA_MAPPER =
      new ArrayPojoSchemaMapper()
          .or(new ObjectPojoSchemaMapper())
          .or(new ComposedPojoSchemaMapper())
          .or(new EnumPojoSchemaMapper())
          .orLast(new MemberPojoSchemaMapper());

  private static final CompleteMemberSchemaMapper COMPLETE_MEMBER_SCHEMA_MAPPER =
      CompleteMemberSchemaMapperFactory.create();

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
          final PList<ParameterSchema> parameterSchemas =
              parsedSpecifications.flatMap(ParsedSpecification::getParameters);
          final MapContext newMapContext =
              ctx.addPojoSchemas(pojoSchemas).addParametersSchemas(parameterSchemas);
          return processMapContext(mainDirectory, newMapContext, excludedSchemas);
        },
        (ctx, schemas) -> {
          final MapContext resultingContext =
              schemas.map(COMPLETE_POJO_SCHEMA_MAPPER::map).reduce(MapContext::merge);
          return processMapContext(mainDirectory, ctx.merge(resultingContext), excludedSchemas);
        },
        (ctx, parameters) -> {
          final PList<Parameter> mappedParameters =
              parameters.toPList().flatMapOptional(this::mapParameterSchema);
          return processMapContext(
              mainDirectory, ctx.addParameters(mappedParameters), excludedSchemas);
        });
  }

  private Optional<Parameter> mapParameterSchema(ParameterSchema parameterSchema) {
    final MemberSchemaMapResult result = mapParameterSchema(parameterSchema.getSchema());

    if (result.getUnmappedItems().nonEmpty()) {
      // No simple parameter types are not yet supported
      return Optional.empty();
    }

    final Type type = result.getType();
    final Optional<Object> defaultValue =
        Optional.ofNullable(parameterSchema.getSchema().getDefault());
    final Parameter parameter = new Parameter(parameterSchema.getName(), type, defaultValue);
    return Optional.of(parameter);
  }

  private MemberSchemaMapResult mapParameterSchema(Schema<?> schema) {
    final Name dummyPojoMemberName = Name.ofString("DummyPojoMemeberName");
    final PojoName dummyPojoName = PojoName.ofName(Name.ofString("DummyPojoName"));
    return COMPLETE_MEMBER_SCHEMA_MAPPER.map(dummyPojoName, dummyPojoMemberName, schema);
  }
}
