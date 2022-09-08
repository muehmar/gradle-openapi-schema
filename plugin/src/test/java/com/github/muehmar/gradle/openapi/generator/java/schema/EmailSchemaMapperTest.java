package com.github.muehmar.gradle.openapi.generator.java.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.MappedSchema;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import io.swagger.v3.oas.models.media.EmailSchema;
import org.junit.jupiter.api.Test;

class EmailSchemaMapperTest {
  public static final EmailSchemaMapper EMAIL_SCHEMA_MAPPER = new EmailSchemaMapper(null);

  @Test
  void mapSchema_when_emailSchema_then_correctJavaType() {
    final EmailSchema emailSchema = new EmailSchema();
    final MappedSchema<JavaType> mappedSchema =
        EMAIL_SCHEMA_MAPPER.mapSchema(
            Name.ofString("pojoName"), Name.ofString("pojoMemberName"), emailSchema, null, null);
    assertEquals(JavaTypes.STRING.withConstraints(Constraints.ofEmail()), mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void
      mapSchema_when_emailSchemaWithPatternAndMinMaxLengthConstraints_then_correctJavaTypeAndConstraints() {
    final EmailSchema emailSchema = new EmailSchema();
    emailSchema.pattern("pattern").minLength(5).maxLength(50);
    final MappedSchema<JavaType> mappedSchema =
        EMAIL_SCHEMA_MAPPER.mapSchema(
            Name.ofString("pojoName"), Name.ofString("pojoMemberName"), emailSchema, null, null);
    assertEquals(
        JavaTypes.STRING.withConstraints(
            Constraints.ofEmail()
                .and(Constraints.ofSize(Size.of(5, 50)))
                .and(Constraints.ofPattern(Pattern.ofUnescapedString("pattern")))),
        mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }
}
