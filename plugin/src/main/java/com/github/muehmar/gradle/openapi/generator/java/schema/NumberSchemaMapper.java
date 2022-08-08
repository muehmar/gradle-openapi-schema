package com.github.muehmar.gradle.openapi.generator.java.schema;

import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.DOUBLE;
import static com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes.FLOAT;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.NumberSchema;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class NumberSchemaMapper extends BaseSchemaMapper<NumberSchema> {
  private static final Map<String, JavaType> formatMap = createFormatMap();

  public NumberSchemaMapper(JavaSchemaMapper nextTypeMapper) {
    super(NumberSchema.class, nextTypeMapper);
  }

  @Override
  MappedSchema<JavaType> mapSpecificSchema(
      Name pojoName,
      Name pojoMemberName,
      NumberSchema schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {

    final Constraints constraints = ConstraintsMapper.getMinimumAndMaximum(schema);

    final JavaType javaType =
        Optional.ofNullable(schema.getFormat())
            .map(formatMap::get)
            .orElse(FLOAT)
            .withConstraints(constraints);
    return MappedSchema.ofType(javaType);
  }

  private static Map<String, JavaType> createFormatMap() {
    final Map<String, JavaType> formatMap = new HashMap<>();
    formatMap.put("float", FLOAT);
    formatMap.put("double", DOUBLE);
    return formatMap;
  }
}
