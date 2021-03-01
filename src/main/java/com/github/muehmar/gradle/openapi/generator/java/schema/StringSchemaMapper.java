package com.github.muehmar.gradle.openapi.generator.java.schema;

import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.LOCAL_TIME;
import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.STRING;
import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.URI;
import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.URL;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class StringSchemaMapper extends BaseSchemaMapper<StringSchema> {

  private static final Map<String, JavaType> formatMap = createFormatMap();

  public StringSchemaMapper(JavaSchemaMapper nextTypeMapper) {
    super(StringSchema.class, nextTypeMapper);
  }

  @Override
  JavaType mapSpecificSchema(
      PojoSettings pojoSettings, StringSchema schema, JavaSchemaMapper chain) {
    return Optional.ofNullable(schema.getFormat())
        .map(formatMap::get)
        .orElseGet(() -> getFormatMappedType(pojoSettings, schema).orElse(STRING));
  }

  private Optional<JavaType> getFormatMappedType(PojoSettings pojoSettings, Schema<?> schema) {
    return pojoSettings
        .getFormatTypeMappings()
        .stream()
        .filter(mapping -> mapping.getFormatType().equals(schema.getFormat()))
        .findFirst()
        .map(
            mapping ->
                Optional.ofNullable(mapping.getImports())
                    .map(imports -> JavaType.ofNameAndImport(mapping.getClassType(), imports))
                    .orElseGet(() -> JavaType.ofName(mapping.getClassType())));
  }

  private static Map<String, JavaType> createFormatMap() {
    final Map<String, JavaType> formatMap = new HashMap<>();
    formatMap.put("uri", URI);
    formatMap.put("url", URL);
    formatMap.put("partial-time", LOCAL_TIME);
    return formatMap;
  }
}
