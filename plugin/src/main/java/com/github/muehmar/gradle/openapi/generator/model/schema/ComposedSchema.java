package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import ch.bluecare.commons.data.Pair;
import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
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
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode
@ToString
public class ComposedSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final Type type;
  private final PList<OpenApiSchema> schemas;

  public ComposedSchema(Schema<?> delegate, Type type, PList<OpenApiSchema> schemas) {
    this.delegate = delegate;
    this.type = type;
    this.schemas = schemas;
  }

  public static Optional<ComposedSchema> wrap(Schema<?> schema) {
    final List<Schema> allOf = schema.getAllOf();
    final List<Schema> anyOf = schema.getAnyOf();
    final List<Schema> oneOf = schema.getOneOf();

    if (allOf != null) {
      final ComposedSchema composedSchema =
          new ComposedSchema(
              schema, Type.ALL_OF, PList.fromIter(allOf).map(OpenApiSchema::wrapSchema));
      return Optional.of(composedSchema);
    } else if (anyOf != null) {
      final ComposedSchema composedSchema =
          new ComposedSchema(
              schema, Type.ANY_OF, PList.fromIter(anyOf).map(OpenApiSchema::wrapSchema));
      return Optional.of(composedSchema);
    } else if (oneOf != null) {
      final ComposedSchema composedSchema =
          new ComposedSchema(
              schema, Type.ONE_OF, PList.fromIter(oneOf).map(OpenApiSchema::wrapSchema));
      return Optional.of(composedSchema);
    }

    return Optional.empty();
  }

  @Override
  public MapContext mapToPojo(PojoName pojoName) {
    return fromComposedSchema(pojoName, type.asComposedPojoType(), schemas);
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(PojoName pojoName, Name memberName) {
    final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, memberName);
    final ObjectType objectType = ObjectType.ofName(openApiPojoName);
    final PojoSchema pojoSchema = new PojoSchema(openApiPojoName, this);
    return MemberSchemaMapResult.ofTypeAndPojoSchema(objectType, pojoSchema);
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }

  private MapContext fromComposedSchema(
      PojoName pojoName,
      UnresolvedComposedPojo.CompositionType type,
      PList<OpenApiSchema> schemas) {

    final PList<SchemaReference> references = determineSchemaReferences(schemas);

    final PList<OpenApiSpec> remoteSpecs =
        references.flatMapOptional(SchemaReference::getRemoteSpec);

    final PList<PojoSchema> pojoSchemas = determinePojoSchemas(pojoName, type, schemas);

    final Optional<Discriminator> discriminator = extractDiscriminator();
    final PList<PojoName> pojoNames = determinePojoNames(pojoName, references, pojoSchemas);

    final UnmappedItems unmappedItems =
        UnmappedItemsBuilder.create()
            .specifications(remoteSpecs)
            .pojoSchemas(pojoSchemas)
            .parameterSchemas(PList.empty())
            .build();

    final UnresolvedMapResult unresolvedMapResult =
        createMapResult(pojoName, type, discriminator, pojoNames);
    return MapContext.fromUnmappedItemsAndResult(unmappedItems, unresolvedMapResult);
  }

  private UnresolvedMapResult createMapResult(
      PojoName pojoName,
      UnresolvedComposedPojo.CompositionType type,
      Optional<Discriminator> discriminator,
      PList<PojoName> pojoNames) {
    final UnresolvedComposedPojo unresolvedComposedPojo =
        new UnresolvedComposedPojo(
            pojoName,
            getDescription(),
            type,
            pojoNames,
            ConstraintsMapper.getPropertyCountConstraints(delegate),
            discriminator);
    return UnresolvedMapResult.ofUnresolvedComposedPojo(unresolvedComposedPojo);
  }

  private static PList<PojoName> determinePojoNames(
      PojoName pojoName, PList<SchemaReference> references, PList<PojoSchema> pojoSchemas) {
    final PList<PojoName> namesFromReferences =
        references
            .map(SchemaReference::getSchemaName)
            .map(n -> PojoName.ofNameAndSuffix(n, pojoName.getSuffix()));
    final PList<PojoName> namesFromSchemas = pojoSchemas.map(PojoSchema::getPojoName);
    return namesFromReferences.concat(namesFromSchemas);
  }

  private static PList<PojoSchema> determinePojoSchemas(
      PojoName pojoName,
      UnresolvedComposedPojo.CompositionType type,
      PList<OpenApiSchema> schemas) {
    final PList<OpenApiSchema> inlineDefinitions =
        schemas.filter(schema -> not(schema instanceof ReferenceSchema));

    return inlineDefinitions
        .zipWithIndex()
        .map(IndexedOpenApiSchema::fromPair)
        .map(ioap -> ioap.toPojoSchema(pojoName, type, inlineDefinitions.size()));
  }

  private static PList<SchemaReference> determineSchemaReferences(PList<OpenApiSchema> schemas) {
    return schemas
        .filter(ReferenceSchema.class::isInstance)
        .map(ReferenceSchema.class::cast)
        .map(ReferenceSchema::getReference)
        .map(SchemaReference::fromRefString);
  }

  private Optional<Discriminator> extractDiscriminator() {
    return Optional.ofNullable(delegate.getDiscriminator())
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

  private enum Type {
    ALL_OF(UnresolvedComposedPojo.CompositionType.ALL_OF),
    ONE_OF(UnresolvedComposedPojo.CompositionType.ONE_OF),
    ANY_OF(UnresolvedComposedPojo.CompositionType.ANY_OF);

    private final UnresolvedComposedPojo.CompositionType pojoType;

    Type(UnresolvedComposedPojo.CompositionType pojoType) {
      this.pojoType = pojoType;
    }

    UnresolvedComposedPojo.CompositionType asComposedPojoType() {
      return pojoType;
    }
  }

  @Value
  private static class IndexedOpenApiSchema {
    OpenApiSchema schema;
    int index;

    public static IndexedOpenApiSchema fromPair(Pair<OpenApiSchema, Integer> p) {
      return new IndexedOpenApiSchema(p.first(), p.second());
    }

    public PojoSchema toPojoSchema(
        PojoName composedPojoName,
        UnresolvedComposedPojo.CompositionType composedPojoType,
        int totalPojoCount) {
      final PojoName pojoName = getPojoName(composedPojoName, composedPojoType, totalPojoCount);
      return new PojoSchema(pojoName, schema);
    }

    public PojoName getPojoName(
        PojoName composedPojoName,
        UnresolvedComposedPojo.CompositionType composedPojoType,
        int totalPojoCount) {
      final String numberSuffix = totalPojoCount > 1 ? String.format("%d", index) : "";
      final Name openApiPojoName =
          composedPojoName
              .getName()
              .append(composedPojoType.asPascalCaseName())
              .append(numberSuffix);
      return PojoName.ofNameAndSuffix(openApiPojoName, composedPojoName.getSuffix());
    }
  }
}
