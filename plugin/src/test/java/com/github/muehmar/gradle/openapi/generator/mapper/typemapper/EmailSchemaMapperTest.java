package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.EmailSchema;
import org.junit.jupiter.api.Test;

class EmailSchemaMapperTest extends BaseTypeMapperTest {

  @Test
  void mapSchema_when_emailSchema_then_correctType() {
    final EmailSchema emailSchema = new EmailSchema();
    final TypeMapResult mappedSchema = run(emailSchema);
    final StringType expectedType =
        StringType.ofFormat(StringType.Format.EMAIL).withConstraints(Constraints.ofEmail());
    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }

  @Test
  void
      mapSchema_when_emailSchemaWithPatternAndMinMaxLengthConstraints_then_correctTypeAndConstraints() {
    final EmailSchema emailSchema = new EmailSchema();
    emailSchema.pattern("pattern").minLength(5).maxLength(50);
    final TypeMapResult mappedSchema = run(emailSchema);
    final StringType expectedType =
        StringType.ofFormat(StringType.Format.EMAIL)
            .withConstraints(
                Constraints.ofEmail()
                    .and(Constraints.ofSize(Size.of(5, 50)))
                    .and(Constraints.ofPattern(Pattern.ofUnescapedString("pattern"))));
    assertEquals(expectedType, mappedSchema.getType());
    assertEquals(PList.empty(), mappedSchema.getOpenApiPojos());
  }
}
