package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
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

  public Type getAdditionalPropertiesType(ComponentName name) {
    return getAdditionalPropertiesMapResult(name).getType();
  }

  public MemberSchemaMapResult getAdditionalPropertiesMapResult(
      ComponentName name, Name memberName) {
    return schema
        .map(s -> mapAdditionalPropertiesSchema(s, name, memberName))
        .orElse(MemberSchemaMapResult.ofType(AnyType.create(NULLABLE)));
  }

  public MemberSchemaMapResult getAdditionalPropertiesMapResult(ComponentName name) {
    return getAdditionalPropertiesMapResult(name, Name.ofString("Property"));
  }

  private static MemberSchemaMapResult mapAdditionalPropertiesSchema(
      OpenApiSchema schema, ComponentName name, Name memberName) {
    final MemberSchemaMapResult result = schema.mapToMemberType(name, memberName);
    if (result.getType().isArrayType()) {
      final ComponentName arrayComponentName = name.deriveMemberSchemaName(memberName);
      final ObjectType type = ObjectType.ofName(arrayComponentName.getPojoName());
      final PojoSchema arrayPojoSchema = new PojoSchema(arrayComponentName, schema);
      return MemberSchemaMapResult.ofTypeAndPojoSchema(type, arrayPojoSchema);
    }
    return result;
  }

  public AdditionalProperties asAdditionalProperties(ComponentName name) {
    return allowed
        ? AdditionalProperties.allowed(getAdditionalPropertiesType(name))
        : AdditionalProperties.notAllowed();
  }
}
