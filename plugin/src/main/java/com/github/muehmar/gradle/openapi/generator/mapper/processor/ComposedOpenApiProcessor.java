package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.ReferenceMapper;
import com.github.muehmar.gradle.openapi.generator.model.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Objects;
import java.util.Optional;

public class ComposedOpenApiProcessor extends BaseSingleSchemaOpenApiProcessor {
  @Override
  public Optional<SchemaProcessResult> process(
      OpenApiPojo openApiPojo, CompleteOpenApiProcessor completeOpenApiProcessor) {
    if (openApiPojo.getSchema() instanceof ComposedSchema) {
      final ComposedPojo composedPojo =
          processComposedSchema(
              openApiPojo.getPojoName(), (ComposedSchema) openApiPojo.getSchema());

      return Optional.of(processComposedPojo(composedPojo, completeOpenApiProcessor));
    } else {
      return Optional.empty();
    }
  }

  private ComposedPojo processComposedSchema(PojoName name, ComposedSchema schema) {
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

  protected ComposedPojo fromComposedSchema(
      PojoName pojoName,
      String description,
      ComposedPojo.CompositionType type,
      PList<Schema<?>> schemas) {

    final PList<PojoName> pojoNames =
        schemas
            .flatMapOptional(
                schema -> Optional.ofNullable(schema.get$ref()).map(ReferenceMapper::getRefName))
            .map(n -> PojoName.ofNameAndSuffix(n, pojoName.getSuffix()));

    final PList<Schema<?>> inlineDefinitions =
        schemas.filter(schema -> Objects.isNull(schema.get$ref()));

    final PList<OpenApiPojo> openApiPojos =
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
                  return new OpenApiPojo(
                      PojoName.ofNameAndSuffix(openApiPojoName, pojoName.getSuffix()), schema);
                });

    return new ComposedPojo(pojoName, description, type, pojoNames, openApiPojos);
  }

  private SchemaProcessResult processComposedPojo(
      ComposedPojo composedPojo, CompleteOpenApiProcessor completeOpenApiProcessor) {
    return composedPojo
        .getOpenApiPojos()
        .map(completeOpenApiProcessor::process)
        .foldRight(SchemaProcessResult.empty(), SchemaProcessResult::concat)
        .addComposedPojo(composedPojo);
  }
}
