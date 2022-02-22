package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ObjectSchema;

public class ObjectSchemaMapper extends BaseSchemaMapper<ObjectSchema> {
  public ObjectSchemaMapper(JavaSchemaMapper nextTypeMapper) {
    super(ObjectSchema.class, nextTypeMapper);
  }

  @Override
  MappedSchema<JavaType> mapSpecificSchema(
      Name pojoName,
      Name pojoMemberName,
      ObjectSchema schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {
    final Name openApiPojoName = JavaResolver.toPascalCase(pojoName, pojoMemberName);
    final JavaType objectType = JavaType.ofOpenApiSchema(openApiPojoName, pojoSettings.getSuffix());
    final OpenApiPojo openApiPojo = new OpenApiPojo(openApiPojoName, schema);
    return MappedSchema.ofTypeAndOpenApiPojo(objectType, openApiPojo);
  }
}
