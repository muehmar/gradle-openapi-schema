package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.model.type.AdditionalPropertiesValueType;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
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

  public static AdditionalPropertiesSchema wrapNullable(OpenApiSpec currentSpec, Object object) {
    if (object == null || Boolean.TRUE.equals(object)) {
      return new AdditionalPropertiesSchema(true, Optional.empty());
    } else if (object instanceof Schema) {
      final OpenApiSchema openApiSchema =
          OpenApiSchema.wrapSchema(new SchemaWrapper(currentSpec, (Schema<?>) object));
      return new AdditionalPropertiesSchema(true, Optional.of(openApiSchema));
    } else {
      return new AdditionalPropertiesSchema(false, Optional.empty());
    }
  }

  public boolean isAllowed() {
    return allowed;
  }

  public AdditionalPropertiesValueType getAdditionalPropertiesType(ComponentName name) {
    return getAdditionalPropertiesMapResult(name).getAdditionalPropertiesValueType();
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

  /**
   * Maps the schema of the additional properties to its value type. A container value type is
   * mapped to a dedicated pojo which is referenced as {@link ObjectType}, i.e. the resulting value
   * type is never a container (see {@link AdditionalPropertiesValueType}).
   */
  private static MemberSchemaMapResult mapAdditionalPropertiesSchema(
      OpenApiSchema schema, ComponentName name, Name memberName) {
    final MemberSchemaMapResult result = schema.mapToMemberType(name, memberName);
    return result
        .getType()
        .fold(
            numericType -> result,
            integerType -> result,
            stringType -> result,
            arrayType -> asOwnPojo(schema, name, memberName),
            booleanType -> result,
            objectType -> result,
            enumType -> result,
            mapType -> asOwnPojo(schema, name, memberName),
            anyType -> result);
  }

  /**
   * Creates a dedicated pojo for the given schema and references it as {@link ObjectType}. Used for
   * container value types, which are not supported as value type directly.
   */
  private static MemberSchemaMapResult asOwnPojo(
      OpenApiSchema schema, ComponentName name, Name memberName) {
    final ComponentName componentName = name.deriveMemberSchemaName(memberName);
    final ObjectType type = StandardObjectType.ofName(componentName.getPojoName());
    final PojoSchema pojoSchema = new PojoSchema(componentName, schema);
    return MemberSchemaMapResult.ofTypeAndPojoSchema(type, pojoSchema);
  }

  public AdditionalProperties asAdditionalProperties(ComponentName name) {
    return allowed
        ? AdditionalProperties.allowed(getAdditionalPropertiesType(name))
        : AdditionalProperties.notAllowed();
  }
}
