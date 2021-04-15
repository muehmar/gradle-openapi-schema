package com.github.muehmar.gradle.openapi.generator.java.schema;

import com.github.muehmar.gradle.openapi.generator.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.EmailSchema;

public class EmailSchemaMapper extends BaseSchemaMapper<EmailSchema> {
  public EmailSchemaMapper(JavaSchemaMapper nextMapper) {
    super(EmailSchema.class, nextMapper);
  }

  @Override
  MappedSchema<JavaType> mapSpecificSchema(
      String pojoKey,
      String key,
      EmailSchema schema,
      PojoSettings pojoSettings,
      JavaSchemaMapper chain) {

    final Constraints patternConstraints = ConstraintsMapper.getPattern(schema);
    final Constraints minAndMaxLengthConstraints = ConstraintsMapper.getMinAndMaxLength(schema);

    final JavaType type =
        JavaTypes.STRING.withConstraints(
            Constraints.ofEmail().and(patternConstraints).and(minAndMaxLengthConstraints));
    return MappedSchema.ofType(type);
  }
}
