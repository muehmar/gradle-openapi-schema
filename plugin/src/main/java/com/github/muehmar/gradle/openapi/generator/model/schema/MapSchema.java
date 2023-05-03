package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.UnmappedItems;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.MapPojo;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.model.specification.SchemaReference;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.NoType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.util.Optionals;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode
@ToString
public class MapSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final AdditionalPropertiesSchema additionalProperties;

  private MapSchema(Schema<?> delegate, AdditionalPropertiesSchema additionalProperties) {
    this.delegate = delegate;
    this.additionalProperties = additionalProperties;
  }

  public static Optional<MapSchema> wrap(Schema<?> schema) {
    final Object additionalProperties = schema.getAdditionalProperties();
    if (SchemaType.OBJECT.matchesType(schema) && schema.getProperties() == null) {

      if (additionalProperties instanceof Schema) {
        final OpenApiSchema openApiSchema =
            OpenApiSchema.wrapSchema((Schema<?>) additionalProperties);
        final AdditionalPropertiesSchema additionalPropertiesSchema =
            AdditionalPropertiesSchema.fromAdditionalPropertiesSchema(openApiSchema);
        final MapSchema mapSchema = new MapSchema(schema, additionalPropertiesSchema);
        return Optional.of(mapSchema);
      } else if (additionalProperties == null || Boolean.TRUE.equals(additionalProperties)) {
        final MapSchema mapSchema =
            new MapSchema(schema, AdditionalPropertiesSchema.fromFreeFormSchema());
        return Optional.of(mapSchema);
      }
    }

    return Optional.empty();
  }

  @Override
  public MapContext mapToPojo(PojoName pojoName) {
    final MapTypeAndUnmappedItems result = determineMapType(pojoName, Name.ofString("Object"));
    final MapPojo mapPojo =
        MapPojo.of(pojoName, getDescription(), result.getMapType().getValue(), getMapConstraints());
    return MapContext.ofPojo(mapPojo).addUnmappedItems(result.getUnmappedItems());
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(PojoName pojoName, Name memberName) {
    return determineMapType(pojoName, memberName).toMemberSchemaMapResult();
  }

  private MapTypeAndUnmappedItems determineMapType(PojoName pojoName, Name memberName) {
    return additionalProperties
        .fold(
            MapSchema::createMapTypeForFreeFormSchema,
            schema -> determineMapTypeFromAdditionalPropertiesSchema(pojoName, memberName, schema))
        .addConstraints(getMapConstraints());
  }

  private static MapTypeAndUnmappedItems createMapTypeForFreeFormSchema() {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), NoType.create());
    return new MapTypeAndUnmappedItems(mapType, UnmappedItems.empty());
  }

  private static MapTypeAndUnmappedItems determineMapTypeFromAdditionalPropertiesSchema(
      PojoName pojoName, Name memberName, OpenApiSchema schema) {
    if (schema instanceof ReferenceSchema) {
      final ReferenceSchema referenceSchema = (ReferenceSchema) schema;
      final SchemaReference schemaReference =
          SchemaReference.fromRefString(referenceSchema.getReference());
      final PojoName mapValuePojoName =
          PojoName.ofNameAndSuffix(schemaReference.getSchemaName(), pojoName.getSuffix());
      final ObjectType mapValueType = ObjectType.ofName(mapValuePojoName);
      final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), mapValueType);
      return MapTypeAndUnmappedItems.ofTypeAndOptionalSpec(
          mapType, schemaReference.getRemoteSpec());
    } else if (schema instanceof ObjectSchema) {
      final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, memberName);
      final ObjectType objectType = ObjectType.ofName(openApiPojoName);
      final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), objectType);
      final PojoSchema pojoSchema = new PojoSchema(openApiPojoName, schema);

      return new MapTypeAndUnmappedItems(mapType, UnmappedItems.ofPojoSchema(pojoSchema));
    } else {
      final MemberSchemaMapResult memberSchemaMapResult =
          schema.mapToMemberType(pojoName, memberName);

      final MapType mapType =
          MapType.ofKeyAndValueType(StringType.noFormat(), memberSchemaMapResult.getType());

      return new MapTypeAndUnmappedItems(mapType, memberSchemaMapResult.getUnmappedItems());
    }
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }

  private Constraints getMapConstraints() {
    return ConstraintsMapper.getPropertyCountConstraints(delegate);
  }

  @Value
  private static class MapTypeAndUnmappedItems {
    MapType mapType;
    UnmappedItems unmappedItems;

    public static MapTypeAndUnmappedItems ofTypeAndOptionalSpec(
        MapType mapType, Optional<OpenApiSpec> spec) {
      return new MapTypeAndUnmappedItems(
          mapType, spec.map(UnmappedItems::ofSpec).orElseGet(UnmappedItems::empty));
    }

    MemberSchemaMapResult toMemberSchemaMapResult() {
      return MemberSchemaMapResult.ofTypeAndUnmappedItems(mapType, unmappedItems);
    }

    MapTypeAndUnmappedItems addConstraints(Constraints constraints) {
      return new MapTypeAndUnmappedItems(mapType.withConstraints(constraints), unmappedItems);
    }
  }

  @EqualsAndHashCode
  @ToString
  private static class AdditionalPropertiesSchema {
    private final Optional<Boolean> isFreeForm;
    private final Optional<OpenApiSchema> additionaPropertiesSchema;

    private AdditionalPropertiesSchema(
        Optional<Boolean> isFreeForm, Optional<OpenApiSchema> additionaPropertiesSchema) {
      this.isFreeForm = isFreeForm;
      this.additionaPropertiesSchema = additionaPropertiesSchema;
    }

    public static AdditionalPropertiesSchema fromFreeFormSchema() {
      return new AdditionalPropertiesSchema(Optional.of(true), Optional.empty());
    }

    public static AdditionalPropertiesSchema fromAdditionalPropertiesSchema(OpenApiSchema schema) {
      return new AdditionalPropertiesSchema(Optional.empty(), Optional.of(schema));
    }

    public <T> T fold(Supplier<T> onFreeForm, Function<OpenApiSchema, T> onSchema) {
      return Optionals.or(
              isFreeForm.map(ignore -> onFreeForm.get()), additionaPropertiesSchema.map(onSchema))
          .orElseThrow(IllegalStateException::new);
    }
  }
}
