package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.UnresolvedObjectPojoBuilder.fullUnresolvedObjectPojoBuilder;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.PropertyScopeMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.mapper.UnresolvedMapResult;
import com.github.muehmar.gradle.openapi.generator.model.*;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UnresolvedOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.schema.SchemaCompositions.CompositionMapResult;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
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
    if (schema.getRequired() != null) {
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
  public MapContext mapToPojo(ComponentName name) {
    final PojoMemberMapResults pojoMemberMapResults = extractMembers(name);
    final AdditionalPropertiesMapResult additionalPropertiesMapResult =
        extractAdditionalPropertyMembers(name);
    final PList<Name> requiredAdditionalProperties =
        additionalPropertiesMapResult.getRequiredAdditionalProperties();
    final Constraints constraints = ConstraintsMapper.getPropertyCountConstraints(delegate);
    final SchemaCompositions schemaCompositions = SchemaCompositions.wrap(delegate);

    final CompositionMapResult<UnresolvedAllOfComposition> allOfResult =
        schemaCompositions.getAllOf(name);
    final CompositionMapResult<UnresolvedOneOfComposition> oneOfResult =
        schemaCompositions.getOneOf(name);
    final CompositionMapResult<UnresolvedAnyOfComposition> anyOfResult =
        schemaCompositions.getAnyOf(name);
    final Optional<UntypedDiscriminator> discriminator =
        DiscriminatorDefinition.extractFromSchema(delegate);

    final UnresolvedObjectPojo unresolvedObjectPojo =
        fullUnresolvedObjectPojoBuilder()
            .name(name)
            .description(getDescription())
            .nullability(Nullability.fromBoolean(isNullable()))
            .pojoXml(PojoXml.fromSchema(delegate))
            .members(pojoMemberMapResults.getMembers())
            .requiredAdditionalProperties(requiredAdditionalProperties)
            .constraints(constraints)
            .additionalProperties(additionalPropertiesSchema.asAdditionalProperties(name))
            .allOfComposition(allOfResult.getComposition())
            .oneOfComposition(oneOfResult.getComposition())
            .anyOfComposition(anyOfResult.getComposition())
            .discriminator(discriminator)
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
  public MemberSchemaMapResult mapToMemberType(ComponentName parentComponentName, Name memberName) {
    final Nullability nullability = Nullability.fromBoolean(isNullable());
    if (isMapSchema()) {
      final MemberSchemaMapResult additionalPropertiesMapResult =
          additionalPropertiesSchema.getAdditionalPropertiesMapResult(
              parentComponentName, memberName);
      final Constraints constraints = ConstraintsMapper.getPropertyCountConstraints(delegate);
      final MapType mapType =
          MapType.ofKeyAndValueType(StringType.noFormat(), additionalPropertiesMapResult.getType())
              .withConstraints(constraints)
              .withNullability(nullability);
      return MemberSchemaMapResult.ofTypeAndUnmappedItems(
          mapType, additionalPropertiesMapResult.getUnmappedItems());
    } else {
      final ComponentName openApiPojoName = parentComponentName.deriveMemberSchemaName(memberName);
      final ObjectType objectType =
          StandardObjectType.ofName(openApiPojoName.getPojoName()).withNullability(nullability);
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

  private PojoMemberMapResults extractMembers(ComponentName componentName) {
    final PList<PojoMemberMapResult> results =
        PList.fromIter(properties.entrySet())
            .map(MemberSchema::fromEntry)
            .map(memberSchema -> mapToPojoMember(componentName, memberSchema));
    return new PojoMemberMapResults(results);
  }

  private AdditionalPropertiesMapResult extractAdditionalPropertyMembers(ComponentName name) {
    final MemberSchemaMapResult additionalPropertiesMapResult =
        additionalPropertiesSchema.getAdditionalPropertiesMapResult(name);
    final PList<Name> requiredAdditionalProperties =
        requiredProperties.getRequiredAdditionalPropertyNames().map(Name::ofString);
    return new AdditionalPropertiesMapResult(
        requiredAdditionalProperties, additionalPropertiesMapResult.getUnmappedItems());
  }

  private PojoMemberMapResult mapToPojoMember(
      ComponentName componentName, MemberSchema memberSchema) {
    final Necessity necessity = Necessity.fromBoolean(requiredProperties.isRequired(memberSchema));

    return toPojoMemberFromSchema(
        componentName, memberSchema.getName(), memberSchema.getSchema(), necessity);
  }

  private PojoMemberMapResult toPojoMemberFromSchema(
      ComponentName componentName, Name pojoMemberName, OpenApiSchema schema, Necessity necessity) {
    final MemberSchemaMapResult result = schema.mapToMemberType(componentName, pojoMemberName);
    final PropertyScope propertyScope = PropertyScopeMapper.mapScope(schema.getDelegateSchema());
    final PojoMemberXml pojoMemberXml = PojoMemberXml.fromSchema(schema.getDelegateSchema());

    final Type type = result.getType();

    final PojoMember pojoMember =
        new PojoMember(
            pojoMemberName, schema.getDescription(), type, propertyScope, necessity, pojoMemberXml);
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
    PList<Name> requiredAdditionalProperties;
    UnmappedItems unmappedItems;
  }
}
