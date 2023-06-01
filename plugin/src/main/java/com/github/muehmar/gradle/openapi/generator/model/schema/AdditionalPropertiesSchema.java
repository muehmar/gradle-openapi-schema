package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/** Wraps the additionalProperties object which can be either a boolean or a schema. */
@EqualsAndHashCode
@ToString
class AdditionalPropertiesSchema {
  private final boolean allowed;
  private final Optional<OpenApiSchema> schema;

  private AdditionalPropertiesSchema(boolean allowed, Optional<OpenApiSchema> schema) {
    this.allowed = allowed;
    this.schema = schema;
  }

  public static AdditionalPropertiesSchema wrapNullable(Object object) {
    if (object == null || Boolean.TRUE.equals(object)) {
      return new AdditionalPropertiesSchema(true, Optional.empty());
    } else if (object instanceof Schema) {
      final OpenApiSchema openApiSchema = OpenApiSchema.wrapSchema((Schema<?>) object);
      return new AdditionalPropertiesSchema(true, Optional.of(openApiSchema));
    } else {
      return new AdditionalPropertiesSchema(false, Optional.empty());
    }
  }

  public boolean isAllowed() {
    return allowed;
  }

  public Type getAdditionalPropertiesType(PojoName pojoName) {
    return getAdditionalPropertiesMapResult(pojoName).getType();
  }

  public MemberSchemaMapResult getAdditionalPropertiesMapResult(
      PojoName pojoName, Name memberName) {
    return schema
        .map(s -> mapAdditionalPropertiesSchema(s, pojoName, memberName))
        .orElse(MemberSchemaMapResult.ofType(AnyType.create()));
  }

  public MemberSchemaMapResult getAdditionalPropertiesMapResult(PojoName pojoName) {
    return getAdditionalPropertiesMapResult(pojoName, Name.ofString("Property"));
  }

  private static MemberSchemaMapResult mapAdditionalPropertiesSchema(
      OpenApiSchema schema, PojoName pojoName, Name memberName) {
    final MemberSchemaMapResult result = schema.mapToMemberType(pojoName, memberName);
    if (result.getType().isArrayType()) {
      final PojoName arrayPojoName = PojoName.deriveOpenApiPojoName(pojoName, memberName);
      final ObjectType type = ObjectType.ofName(arrayPojoName);
      final PojoSchema arrayPojoSchema = new PojoSchema(arrayPojoName, schema);
      return MemberSchemaMapResult.ofTypeAndPojoSchema(type, arrayPojoSchema);
    }
    return result;
  }

  public AdditionalProperties asAdditionalProperties(PojoName pojoName) {
    return allowed
        ? AdditionalProperties.allowed(getAdditionalPropertiesType(pojoName))
        : AdditionalProperties.notAllowed();
  }
}
