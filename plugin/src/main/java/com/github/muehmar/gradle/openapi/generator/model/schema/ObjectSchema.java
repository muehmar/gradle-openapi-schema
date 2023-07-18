package com.github.muehmar.gradle.openapi.generator.model.schema;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.PropertyScopeMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.schema.SchemaCompositions.CompositionMapResult;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode
@ToString
public class ObjectSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final Map<String, Schema> properties;
  private final RequiredProperties requiredProperties;
  private final AdditionalPropertiesSchema additionalPropertiesSchema;

  private ObjectSchema(
      Schema<?> delegate,
      Map<String, Schema> properties,
      RequiredProperties requiredProperties,
      AdditionalPropertiesSchema additionalPropertiesSchema) {
    this.delegate = delegate;
    this.properties = properties;
    this.requiredProperties = requiredProperties;
    this.additionalPropertiesSchema = additionalPropertiesSchema;
  }

  public static Optional<ObjectSchema> wrap(Schema<?> schema) {
    if (isObjectSchema(schema)) {
      final Map<String, Schema> properties =
          Optional.ofNullable(schema.getProperties()).orElseGet(Collections::emptyMap);

      final RequiredProperties requiredProperties =
          RequiredPropertiesBuilder.create()
              .propertyNames(properties.keySet())
              .requiredPropertyNamesNullable(schema.getRequired())
              .build();

      final AdditionalPropertiesSchema additionalPropertiesSchema =
          AdditionalPropertiesSchema.wrapNullable(schema.getAdditionalProperties());

      final ObjectSchema objectSchema =
          new ObjectSchema(schema, properties, requiredProperties, additionalPropertiesSchema);
      return Optional.of(objectSchema);
    }
    return Optional.empty();
  }

  private static boolean isObjectSchema(Schema<?> schema) {
    if (schema.getProperties() != null) {
      return true;
    }
    if (schema.getAdditionalProperties() != null) {
      return true;
    }
    if (schema.getAllOf() != null) {
      return true;
    }
    if (schema.getOneOf() != null) {
      return true;
    }
    if (schema.getAnyOf() != null) {
      return true;
    }
    return SchemaType.OBJECT.matchesType(schema);
  }

  @Override
  public MapContext mapToPojo(PojoName pojoName) {
    final PojoMemberMapResults pojoMemberMapResults = extractMembers(pojoName);
    final AdditionalPropertiesMapResult additionalPropertiesMapResult =
        extractAdditionalPropertyMembers(pojoName);
    final PList<PojoMember> members =
        pojoMemberMapResults.getMembers().concat(additionalPropertiesMapResult.getMembers());
    final Constraints constraints = ConstraintsMapper.getPropertyCountConstraints(delegate);
    final SchemaCompositions schemaCompositions = SchemaCompositions.wrap(delegate);

    final CompositionMapResult<UnresolvedAllOfComposition> allOfResult =
        schemaCompositions.getAllOf(pojoName);
    final CompositionMapResult<UnresolvedOneOfComposition> oneOfResult =
        schemaCompositions.getOneOf(pojoName);
    final CompositionMapResult<UnresolvedAnyOfComposition> anyOfResult =
        schemaCompositions.getAnyOf(pojoName);

    final UnresolvedObjectPojo unresolvedObjectPojo =
        UnresolvedObjectPojoBuilder.create()
            .name(pojoName)
            .description(getDescription())
            .members(members)
            .constraints(constraints)
            .additionalProperties(additionalPropertiesSchema.asAdditionalProperties(pojoName))
            .andAllOptionals()
            .allOfComposition(allOfResult.getComposition())
            .oneOfComposition(oneOfResult.getComposition())
            .anyOfComposition(anyOfResult.getComposition())
            .build();

    final UnmappedItems unmappedItems =
        pojoMemberMapResults
            .getUnmappedItems()
            .merge(additionalPropertiesMapResult.getUnmappedItems())
            .merge(allOfResult.getUnmappedItems())
            .merge(oneOfResult.getUnmappedItems())
            .merge(anyOfResult.getUnmappedItems());

    return MapContext.fromUnmappedItemsAndResult(
        unmappedItems, UnresolvedMapResult.ofUnresolvedObjectPojo(unresolvedObjectPojo));
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(PojoName pojoName, Name memberName) {
    if (isMapSchema()) {
      final MemberSchemaMapResult additionalPropertiesMapResult =
          additionalPropertiesSchema.getAdditionalPropertiesMapResult(pojoName, memberName);
      final Constraints constraints = ConstraintsMapper.getPropertyCountConstraints(delegate);
      final MapType mapType =
          MapType.ofKeyAndValueType(StringType.noFormat(), additionalPropertiesMapResult.getType())
              .withConstraints(constraints);
      return MemberSchemaMapResult.ofTypeAndUnmappedItems(
          mapType, additionalPropertiesMapResult.getUnmappedItems());
    } else {
      final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, memberName);
      final ObjectType objectType = ObjectType.ofName(openApiPojoName);
      final PojoSchema pojoSchema = new PojoSchema(openApiPojoName, this);
      return MemberSchemaMapResult.ofTypeAndPojoSchema(objectType, pojoSchema);
    }
  }

  private boolean isMapSchema() {
    return properties.isEmpty()
        && requiredProperties.getRequiredAdditionalPropertyNames().isEmpty()
        && delegate.getAllOf() == null
        && delegate.getOneOf() == null
        && delegate.getAnyOf() == null;
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }

  private PojoMemberMapResults extractMembers(PojoName pojoName) {
    final PList<PojoMemberMapResult> results =
        PList.fromIter(properties.entrySet())
            .map(MemberSchema::fromEntry)
            .map(memberSchema -> mapToPojoMember(memberSchema, pojoName));
    return new PojoMemberMapResults(results);
  }

  private AdditionalPropertiesMapResult extractAdditionalPropertyMembers(PojoName pojoName) {
    final MemberSchemaMapResult additionalPropertiesMapResult =
        additionalPropertiesSchema.getAdditionalPropertiesMapResult(pojoName);
    final PList<PojoMember> additionalPropertyMembers =
        requiredProperties
            .getRequiredAdditionalPropertyNames()
            .map(Name::ofString)
            .map(
                name ->
                    PojoMember.additionalPropertyForNameAndType(
                        name, additionalPropertiesMapResult.getType()));
    return new AdditionalPropertiesMapResult(
        additionalPropertyMembers, additionalPropertiesMapResult.getUnmappedItems());
  }

  private PojoMemberMapResult mapToPojoMember(MemberSchema memberSchema, PojoName pojoName) {
    final Necessity necessity = Necessity.fromBoolean(requiredProperties.isRequired(memberSchema));

    final Nullability nullability =
        Nullability.fromNullableBoolean(memberSchema.getSchema().isNullable());

    return toPojoMemberFromSchema(
        pojoName, memberSchema.getName(), memberSchema.getSchema(), necessity, nullability);
  }

  private PojoMemberMapResult toPojoMemberFromSchema(
      PojoName pojoName,
      Name pojoMemberName,
      OpenApiSchema schema,
      Necessity necessity,
      Nullability nullability) {
    final MemberSchemaMapResult result = schema.mapToMemberType(pojoName, pojoMemberName);
    final PropertyScope propertyScope = PropertyScopeMapper.mapScope(schema.getDelegateSchema());

    final Type type = result.getType();

    final PojoMember pojoMember =
        new PojoMember(
            pojoMemberName, schema.getDescription(), type, propertyScope, necessity, nullability);
    return new PojoMemberMapResult(pojoMember, result.getUnmappedItems());
  }

  @Value
  private static class PojoMemberMapResult {
    PojoMember pojoMember;
    UnmappedItems unmappedItems;
  }

  @Value
  private static class PojoMemberMapResults {
    PList<PojoMemberMapResult> results;

    PList<PojoMember> getMembers() {
      return results.map(PojoMemberMapResult::getPojoMember);
    }

    UnmappedItems getUnmappedItems() {
      return results
          .map(PojoMemberMapResult::getUnmappedItems)
          .reduce(UnmappedItems::merge)
          .orElse(UnmappedItems.empty());
    }
  }

  @Value
  private static class AdditionalPropertiesMapResult {
    PList<PojoMember> members;
    UnmappedItems unmappedItems;
  }
}
