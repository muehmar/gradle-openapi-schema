package com.github.muehmar.gradle.openapi.generator.mapper.typemapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.PasswordSchema;
import org.junit.jupiter.api.Test;

class PasswordSchemaMapperTest extends BaseTypeMapperTest {

  @Test
  void
      mapSchema_when_passwordSchemaWithPatternAndMinMaxLengthConstraints_then_correctJavaTypeAndConstraints() {
    final PasswordSchema passwordSchema = new PasswordSchema();
    passwordSchema.pattern("pattern").minLength(5).maxLength(50);
    final TypeMapResult result = run(passwordSchema);

    final StringType exptectedType =
        StringType.ofFormat(StringType.Format.PASSWORD)
            .withConstraints(
                Constraints.ofSize(Size.of(5, 50))
                    .and(Constraints.ofPattern(Pattern.ofUnescapedString("pattern"))));
    assertEquals(exptectedType, result.getType());
    assertEquals(PList.empty(), result.getOpenApiPojos());
  }
}