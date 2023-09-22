package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import ch.bluecare.commons.data.Pair;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItemsBuilder;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.model.specification.SchemaReference;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

/** Collection of composed schemas. */
@EqualsAndHashCode
@ToString
class ComposedSchemas {
  private final PList<OpenApiSchema> schemas;

  private ComposedSchemas(PList<OpenApiSchema> schemas) {
    this.schemas = schemas;
  }

  public static ComposedSchemas fromSchemas(PList<OpenApiSchema> schemas) {
    return new ComposedSchemas(schemas);
  }

  public ComposedSchemasMapResult mapSchemasToPojoNames(PojoName pojoName, CompositionType type) {
    final PList<SchemaReference> references = determineSchemaReferences(schemas);

    final PList<OpenApiSpec> remoteSpecs =
        references.flatMapOptional(SchemaReference::getRemoteSpec);

    final PList<PojoSchema> pojoSchemas = determinePojoSchemas(pojoName, type, schemas);
    final PList<PojoName> pojoNames = determinePojoNames(pojoName, references, pojoSchemas);

    final UnmappedItems unmappedItems =
        UnmappedItemsBuilder.create()
            .specifications(remoteSpecs)
            .pojoSchemas(pojoSchemas)
            .parameterSchemas(PList.empty())
            .build();

    return new ComposedSchemasMapResult(unmappedItems, pojoNames);
  }

  private static PList<SchemaReference> determineSchemaReferences(PList<OpenApiSchema> schemas) {
    return schemas
        .filter(ReferenceSchema.class::isInstance)
        .map(ReferenceSchema.class::cast)
        .map(ReferenceSchema::getReference)
        .map(SchemaReference::fromRefString);
  }

  private static PList<PojoSchema> determinePojoSchemas(
      PojoName pojoName, CompositionType type, PList<OpenApiSchema> schemas) {
    final PList<OpenApiSchema> inlineDefinitions =
        schemas.filter(schema -> not(schema instanceof ReferenceSchema));

    return inlineDefinitions
        .zipWithIndex()
        .map(IndexedOpenApiSchema::fromPair)
        .map(ioap -> ioap.toPojoSchema(pojoName, type, inlineDefinitions.size()));
  }

  private static PList<PojoName> determinePojoNames(
      PojoName pojoName, PList<SchemaReference> references, PList<PojoSchema> pojoSchemas) {
    final PList<PojoName> namesFromReferences =
        references
            .map(SchemaReference::getSchemaName)
            .map(schemaName -> PojoName.ofNameAndSuffix(schemaName, pojoName.getSuffix()));
    final PList<PojoName> namesFromSchemas = pojoSchemas.map(PojoSchema::getPojoName);
    return namesFromReferences.concat(namesFromSchemas);
  }

  @Value
  private static class IndexedOpenApiSchema {
    OpenApiSchema schema;
    int index;

    public static IndexedOpenApiSchema fromPair(Pair<OpenApiSchema, Integer> p) {
      return new IndexedOpenApiSchema(p.first(), p.second());
    }

    public PojoSchema toPojoSchema(
        PojoName composedPojoName, CompositionType composedPojoType, int totalPojoCount) {
      final PojoName pojoName = getPojoName(composedPojoName, composedPojoType, totalPojoCount);
      return new PojoSchema(pojoName, schema);
    }

    public PojoName getPojoName(
        PojoName composedPojoName, CompositionType composedPojoType, int totalPojoCount) {
      final String numberSuffix = totalPojoCount > 1 ? String.format("%d", index) : "";
      final Name openApiPojoName =
          composedPojoName
              .getName()
              .append(composedPojoType.asPascalCaseName())
              .append(numberSuffix);
      return PojoName.ofNameAndSuffix(openApiPojoName, composedPojoName.getSuffix());
    }
  }

  @Value
  public static class ComposedSchemasMapResult {
    UnmappedItems unmappedItems;
    PList<PojoName> pojoNames;
  }

  public enum CompositionType {
    ALL_OF("AllOf"),
    ANY_OF("AnyOf"),
    ONE_OF("OneOf");

    private final String value;

    CompositionType(String value) {
      this.value = value;
    }

    public String asPascalCaseName() {
      return value;
    }
  }
}
