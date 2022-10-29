package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItemsBuilder;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.model.specification.SchemaReference;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Objects;
import java.util.Optional;

public class ComposedPojoSchemaMapper implements SinglePojoSchemaMapper {
  @Override
  public Optional<MapContext> map(PojoSchema pojoSchema) {
    if (pojoSchema.getSchema() instanceof ComposedSchema) {
      final MapContext mapContext =
          processComposedSchema(pojoSchema.getPojoName(), (ComposedSchema) pojoSchema.getSchema());

      return Optional.of(mapContext);
    } else {
      return Optional.empty();
    }
  }

  private MapContext processComposedSchema(PojoName name, ComposedSchema schema) {
    if (schema.getOneOf() != null) {
      return fromComposedSchema(
          name,
          schema.getDescription(),
          ComposedPojo.CompositionType.ONE_OF,
          PList.fromIter(schema.getOneOf()).map(s -> (Schema<?>) s));
    }

    if (schema.getAnyOf() != null) {
      return fromComposedSchema(
          name,
          schema.getDescription(),
          ComposedPojo.CompositionType.ANY_OF,
          PList.fromIter(schema.getAnyOf()).map(s -> (Schema<?>) s));
    }

    if (schema.getAllOf() != null) {
      return fromComposedSchema(
          name,
          schema.getDescription(),
          ComposedPojo.CompositionType.ALL_OF,
          PList.fromIter(schema.getAllOf()).map(s -> (Schema<?>) s));
    }

    throw new IllegalArgumentException("Composed schema without any schema definitions");
  }

  private MapContext fromComposedSchema(
      PojoName pojoName,
      String description,
      ComposedPojo.CompositionType type,
      PList<Schema<?>> schemas) {

    final PList<SchemaReference> references =
        schemas
            .flatMapOptional(schema -> Optional.ofNullable(schema.get$ref()))
            .map(SchemaReference::fromRefString);

    final PList<PojoName> referencePojoNames =
        references.map(
            schemaReference ->
                PojoName.ofNameAndSuffix(schemaReference.getSchemaName(), pojoName.getSuffix()));

    final PList<OpenApiSpec> remoteSpecs =
        references.flatMapOptional(SchemaReference::getRemoteSpec);

    final PList<Schema<?>> inlineDefinitions =
        schemas.filter(schema -> Objects.isNull(schema.get$ref()));

    final PList<PojoSchema> pojoSchemas =
        inlineDefinitions
            .zipWithIndex()
            .map(
                p -> {
                  final Schema<?> schema = p.first();
                  final Integer index = p.second();
                  final String openApiPojoNameSuffix =
                      inlineDefinitions.size() > 1 ? "" + index : "";
                  final Name openApiPojoName =
                      pojoName
                          .getName()
                          .append(type.asPascalCaseName())
                          .append(openApiPojoNameSuffix);
                  return new PojoSchema(
                      PojoName.ofNameAndSuffix(openApiPojoName, pojoName.getSuffix()), schema);
                });

    final PList<PojoName> pojoSchemaPojoNames = pojoSchemas.map(PojoSchema::getPojoName);
    final PList<PojoName> allPojoNames = referencePojoNames.concat(pojoSchemaPojoNames);

    final ComposedPojo composedPojo = new ComposedPojo(pojoName, description, type, allPojoNames);
    final UnresolvedMapResult unresolvedMapResult =
        UnresolvedMapResult.ofComposedPojo(composedPojo);
    final UnmappedItems unmappedItems =
        UnmappedItemsBuilder.create()
            .specifications(remoteSpecs)
            .pojoSchemas(pojoSchemas)
            .parameterSchemas(PList.empty())
            .build();
    return MapContext.fromUnmappedItemsAndResult(unmappedItems, unresolvedMapResult);
  }
}
