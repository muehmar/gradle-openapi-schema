package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.exception.OpenApiGeneratorException;
import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.specification.SchemaReference;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class MapSchema implements OpenApiSchema {
  private final Schema<?> delegate;
  private final OpenApiSchema additionalProperties;

  private MapSchema(Schema<?> delegate, OpenApiSchema additionalProperties) {
    this.delegate = delegate;
    this.additionalProperties = additionalProperties;
  }

  public static Optional<MapSchema> wrap(Schema<?> schema) {
    final Object additionalProperties = schema.getAdditionalProperties();
    if (SchemaType.OBJECT.matchesType(schema)
        && additionalProperties instanceof Schema
        && schema.getProperties() == null) {

      final MapSchema mapSchema =
          new MapSchema(schema, OpenApiSchema.wrapSchema((Schema<?>) additionalProperties));
      return Optional.of(mapSchema);
    }

    return Optional.empty();
  }

  @Override
  public MapContext mapToPojo(PojoName pojoName) {
    throw new OpenApiGeneratorException("A map schema is currently not supported as root schema.");
  }

  @Override
  public MemberSchemaMapResult mapToMemberType(PojoName pojoName, Name memberName) {
    if (additionalProperties instanceof ReferenceSchema) {
      final ReferenceSchema referenceSchema = (ReferenceSchema) additionalProperties;
      final SchemaReference schemaReference =
          SchemaReference.fromRefString(referenceSchema.getReference());
      final PojoName mapValuePojoName =
          PojoName.ofNameAndSuffix(schemaReference.getSchemaName(), pojoName.getSuffix());
      final ObjectType mapValueType = ObjectType.ofName(mapValuePojoName);
      final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), mapValueType);
      return MemberSchemaMapResult.ofType(mapType).addOpenApiSpec(schemaReference.getRemoteSpec());
    } else if (additionalProperties instanceof ObjectSchema) {
      final PojoName openApiPojoName = PojoName.deriveOpenApiPojoName(pojoName, memberName);
      final ObjectType objectType = ObjectType.ofName(openApiPojoName);
      final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), objectType);
      final PojoSchema pojoSchema = new PojoSchema(openApiPojoName, additionalProperties);

      return MemberSchemaMapResult.ofTypeAndPojoSchema(mapType, pojoSchema);
    } else {
      return additionalProperties
          .mapToMemberType(pojoName, memberName)
          .mapType(type -> MapType.ofKeyAndValueType(StringType.noFormat(), type));
    }
  }

  @Override
  public Schema<?> getDelegateSchema() {
    return delegate;
  }
}
