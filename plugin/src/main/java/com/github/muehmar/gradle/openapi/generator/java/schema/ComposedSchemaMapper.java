package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.data.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.java.JavaResolver;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ComposedSchema;

public class ComposedSchemaMapper extends BaseSchemaMapper<ComposedSchema> {

  public ComposedSchemaMapper(JavaSchemaMapper nextMapper) {
    super(ComposedSchema.class, nextMapper);
  }

  @Override
  MappedSchema<JavaType> mapSpecificSchema(
      String pojoKey,
      String key,
      ComposedSchema schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {

    final String openApiPojoKey = JavaResolver.toPascalCase(pojoKey, key);
    final JavaType composedSchemaType =
        JavaType.ofOpenApiSchema(openApiPojoKey, pojoSettings.getSuffix());

    final OpenApiPojo openApiPojo = new OpenApiPojo(openApiPojoKey, schema);
    return MappedSchema.ofTypeAndOpenApiPojo(composedSchemaType, openApiPojo);
  }
}
