package com.github.muehmar.gradle.openapi.generator.java.schema;

import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.LOCAL_TIME;
import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.STRING;
import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.URI;
import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.URL;

import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.util.Optionals;
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
    return Optionals.or(
            () -> getFromStandardFormat(schema),
            () -> getFormatMappedType(pojoSettings, schema),
            () -> getEnumType(schema))
        .orElse(STRING);
  }

  private Optional<JavaType> getFromStandardFormat(StringSchema schema) {
    return Optional.ofNullable(schema.getFormat()).map(formatMap::get);
  }

  private Optional<JavaType> getFormatMappedType(PojoSettings pojoSettings, StringSchema schema) {
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

  private Optional<JavaType> getEnumType(StringSchema schema) {
    return Optional.ofNullable(schema.getEnum()).map(JavaType::javaEnum);
  }

  private static Map<String, JavaType> createFormatMap() {
    final Map<String, JavaType> formatMap = new HashMap<>();
    formatMap.put("uri", URI);
    formatMap.put("url", URL);
    formatMap.put("partial-time", LOCAL_TIME);
    return formatMap;
  }
}
