package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.data.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import io.swagger.v3.oas.models.media.PasswordSchema;
import org.junit.jupiter.api.Test;

class PasswordSchemaMapperTest {

  private static final PasswordSchemaMapper PASSWORD_SCHEMA_MAPPER = new PasswordSchemaMapper(null);

  @Test
  void
      mapSchema_when_passwordSchemaWithPatternAndMinMaxLengthConstraints_then_correctJavaTypeAndConstraints() {
    final PasswordSchema passwordSchema = new PasswordSchema();
    passwordSchema.pattern("pattern").minLength(5).maxLength(50);
    final MappedSchema<JavaType> mappedSchema =
        PASSWORD_SCHEMA_MAPPER.mapSchema("pojoKey", "key", passwordSchema, null, null);
    assertEquals(
        JavaTypes.STRING.withConstraints(
            Constraints.ofSize(Size.of(5, 50)).and(Constraints.ofPattern(new Pattern("pattern")))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }
}
