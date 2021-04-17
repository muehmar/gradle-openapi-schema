package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.PasswordSchema;

public class PasswordSchemaMapper extends BaseSchemaMapper<PasswordSchema> {
  public PasswordSchemaMapper(JavaSchemaMapper nextMapper) {
    super(PasswordSchema.class, nextMapper);
  }

  @Override
  MappedSchema<JavaType> mapSpecificSchema(
      String pojoKey,
      String key,
      PasswordSchema schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {

    final Constraints patternConstraints = ConstraintsMapper.getPattern(schema);
    final Constraints minAndMaxLengthConstraints = ConstraintsMapper.getMinAndMaxLength(schema);

    final JavaType type =
        JavaTypes.STRING.withConstraints(patternConstraints.and(minAndMaxLengthConstraints));
    return MappedSchema.ofType(type);
  }
}
