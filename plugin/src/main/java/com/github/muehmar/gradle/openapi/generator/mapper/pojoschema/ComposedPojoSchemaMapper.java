package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItemsBuilder;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.model.specification.SchemaReference;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
          schema,
          UnresolvedComposedPojo.CompositionType.ONE_OF,
          PList.fromIter(schema.getOneOf()).map(s -> (Schema<?>) s));
    }

    if (schema.getAnyOf() != null) {
      return fromComposedSchema(
          name,
          schema,
          UnresolvedComposedPojo.CompositionType.ANY_OF,
          PList.fromIter(schema.getAnyOf()).map(s -> (Schema<?>) s));
    }

    if (schema.getAllOf() != null) {
      return fromComposedSchema(
          name,
          schema,
          UnresolvedComposedPojo.CompositionType.ALL_OF,
          PList.fromIter(schema.getAllOf()).map(s -> (Schema<?>) s));
    }

    throw new IllegalArgumentException("Composed schema without any schema definitions");
  }

  protected MapContext fromComposedSchema(
      PojoName pojoName,
      ComposedSchema composedSchema,
      UnresolvedComposedPojo.CompositionType type,
      PList<Schema<?>> schemas) {

    final PList<SchemaReference> references =
        schemas.flatMapOptional(
            schema -> Optional.ofNullable(schema.get$ref()).map(SchemaReference::fromRefString));

    final PList<PojoName> pojoNames =
        references
            .map(SchemaReference::getSchemaName)
            .map(n -> PojoName.ofNameAndSuffix(n, pojoName.getSuffix()));

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

    final Optional<Discriminator> discriminator = extractDiscriminator(composedSchema);

    final UnmappedItems unmappedItems =
        UnmappedItemsBuilder.create()
            .specifications(remoteSpecs)
            .pojoSchemas(pojoSchemas)
            .parameterSchemas(PList.empty())
            .build();

    final UnresolvedComposedPojo unresolvedComposedPojo =
        new UnresolvedComposedPojo(
            pojoName,
            composedSchema.getDescription(),
            type,
            pojoNames.concat(pojoSchemas.map(PojoSchema::getPojoName)),
            ConstraintsMapper.getPropertyCountConstraints(composedSchema),
            discriminator);
    final UnresolvedMapResult unresolvedMapResult =
        UnresolvedMapResult.ofUnresolvedComposedPojo(unresolvedComposedPojo);
    return MapContext.fromUnmappedItemsAndResult(unmappedItems, unresolvedMapResult);
  }

  private Optional<Discriminator> extractDiscriminator(ComposedSchema composedSchema) {
    return Optional.ofNullable(composedSchema.getDiscriminator())
        .filter(discriminator -> discriminator.getPropertyName() != null)
        .map(this::fromOpenApiDiscriminator);
  }

  private Discriminator fromOpenApiDiscriminator(
      io.swagger.v3.oas.models.media.Discriminator oasDiscriminator) {
    final Name propertyName = Name.ofString(oasDiscriminator.getPropertyName());
    final Optional<Map<String, Name>> pojoNameMapping =
        Optional.ofNullable(oasDiscriminator.getMapping())
            .map(this::fromOpenApiDiscriminatorMapping);
    return Discriminator.fromPropertyName(propertyName).withMapping(pojoNameMapping);
  }

  private Map<String, Name> fromOpenApiDiscriminatorMapping(Map<String, String> mapping) {
    return mapping.entrySet().stream()
        .collect(Collectors.toMap(Map.Entry::getKey, e -> mapMappingReference(e.getValue())));
  }

  private Name mapMappingReference(String reference) {
    return SchemaReference.fromRefString(reference).getSchemaName();
  }
}
