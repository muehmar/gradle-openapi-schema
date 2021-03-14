package com.github.muehmar.gradle.openapi.generator.java.schema;

import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.INTEGER;
import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.LONG;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.IntegerSchema;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class IntegerSchemaMapper extends BaseSchemaMapper<IntegerSchema> {

  private static final Map<String, JavaType> formatMap = createFormatMap();

  public IntegerSchemaMapper(JavaSchemaMapper nextTypeMapper) {
    super(IntegerSchema.class, nextTypeMapper);
  }

  @Override
  JavaType mapSpecificSchema(
      String pojoKey,
      String key,
      IntegerSchema schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {
    return Optional.ofNullable(schema.getFormat()).map(formatMap::get).orElse(INTEGER);
  }

  private static Map<String, JavaType> createFormatMap() {
    final Map<String, JavaType> formatMap = new HashMap<>();
    formatMap.put("int32", INTEGER);
    formatMap.put("int64", LONG);
    return formatMap;
  }
}
