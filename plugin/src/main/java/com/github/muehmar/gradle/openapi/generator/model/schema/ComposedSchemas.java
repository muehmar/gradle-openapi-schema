package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import ch.bluecare.commons.data.Pair;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItemsBuilder;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.name.SchemaName;
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

  public ComposedSchemasMapResult mapSchemasToPojoNames(ComponentName name, CompositionType type) {
    final PList<SchemaReference> references = determineSchemaReferences(schemas);

    final PList<OpenApiSpec> remoteSpecs =
        references.flatMapOptional(SchemaReference::getRemoteSpec);

    final PList<PojoSchema> pojoSchemas = determinePojoSchemas(name, type, schemas);
    final PList<ComponentName> componentNames =
        determineAllComponentNames(name, references, pojoSchemas);

    final UnmappedItems unmappedItems =
        UnmappedItemsBuilder.create()
            .specifications(remoteSpecs)
            .pojoSchemas(pojoSchemas)
            .parameterSchemas(PList.empty())
            .build();

    return new ComposedSchemasMapResult(unmappedItems, componentNames);
  }

  private static PList<SchemaReference> determineSchemaReferences(PList<OpenApiSchema> schemas) {
    return schemas
        .filter(ReferenceSchema.class::isInstance)
        .map(ReferenceSchema.class::cast)
        .map(
            ref ->
                SchemaReference.fromRefString(
                    ref.getSchemaWrapper().getSpec(), ref.getReference()));
  }

  private static PList<PojoSchema> determinePojoSchemas(
      ComponentName name, CompositionType type, PList<OpenApiSchema> schemas) {
    final PList<OpenApiSchema> inlineDefinitions =
        schemas.filter(schema -> not(schema instanceof ReferenceSchema));

    return inlineDefinitions
        .zipWithIndex()
        .map(IndexedOpenApiSchema::fromPair)
        .map(ioap -> ioap.toPojoSchema(name, type, inlineDefinitions.size()));
  }

  private static PList<ComponentName> determineAllComponentNames(
      ComponentName name, PList<SchemaReference> references, PList<PojoSchema> pojoSchemas) {
    final PList<ComponentName> namesFromReferences =
        references
            .map(SchemaReference::getSchemaName)
            .map(
                schemaName ->
                    ComponentName.fromSchemaStringAndSuffix(
                        schemaName.asString(), name.getPojoName().getSuffix()));
    final PList<ComponentName> namesFromSchemas = pojoSchemas.map(PojoSchema::getName);
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
        ComponentName composedComponentName, CompositionType composedPojoType, int totalPojoCount) {
      final ComponentName componentName =
          deriveComponentName(composedComponentName, composedPojoType, totalPojoCount);
      return new PojoSchema(componentName, schema);
    }

    public ComponentName deriveComponentName(
        ComponentName composedComponentName, CompositionType composedPojoType, int totalPojoCount) {
      final String numberSuffix = totalPojoCount > 1 ? String.format("%d", index) : "";
      final PojoName openApiPojoName =
          composedComponentName
              .getPojoName()
              .appendToName(composedPojoType.asPascalCaseName())
              .appendToName(numberSuffix);
      final SchemaName schemaName =
          SchemaName.ofName(
              composedComponentName
                  .getSchemaName()
                  .asName()
                  .append(composedPojoType.asPascalCaseName())
                  .append(numberSuffix));
      return new ComponentName(openApiPojoName, schemaName);
    }
  }

  @Value
  public static class ComposedSchemasMapResult {
    UnmappedItems unmappedItems;
    PList<ComponentName> componentNames;
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
