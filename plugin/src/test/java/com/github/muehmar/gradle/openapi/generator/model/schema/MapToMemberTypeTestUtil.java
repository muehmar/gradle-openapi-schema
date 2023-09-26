package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;

import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import io.swagger.v3.oas.models.media.Schema;

public class MapToMemberTypeTestUtil {
  private MapToMemberTypeTestUtil() {}

  public static MemberSchemaMapResult mapToMemberType(Schema<?> schema) {
    return OpenApiSchema.wrapSchema(schema)
        .mapToMemberType(componentName("PojoName", ""), Name.ofString("pojoMemberName"));
  }

  public static MemberSchemaMapResult mapToMemberType(
      ComponentName componentName, Name pojoMemberName, Schema<?> schema) {
    return OpenApiSchema.wrapSchema(schema).mapToMemberType(componentName, pojoMemberName);
  }
}
