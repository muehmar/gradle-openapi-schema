package com.github.muehmar.gradle.openapi.generator.model.schema;

import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoNames;
import io.swagger.v3.oas.models.media.Schema;

public class MapToMemberTypeTestUtil {
  private MapToMemberTypeTestUtil() {}

  public static MemberSchemaMapResult mapToMemberType(Schema<?> schema) {
    return OpenApiSchema.wrapSchema(schema)
        .mapToMemberType(PojoNames.POJO_NAME, Name.ofString("pojoMemberName"));
  }

  public static MemberSchemaMapResult mapToMemberType(
      PojoName pojoName, Name pojoMemberName, Schema<?> schema) {
    return OpenApiSchema.wrapSchema(schema).mapToMemberType(pojoName, pojoMemberName);
  }
}
