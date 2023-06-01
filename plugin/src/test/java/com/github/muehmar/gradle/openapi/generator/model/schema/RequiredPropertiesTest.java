package com.github.muehmar.gradle.openapi.generator.model.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import io.swagger.v3.oas.models.media.Schema;
import org.junit.jupiter.api.Test;

class RequiredPropertiesTest {
  private static final RequiredProperties REQUIRED_PROPERTIES =
      RequiredPropertiesBuilder.create()
          .propertyNames(PList.of("user", "admin"))
          .requiredPropertyNamesNullable(PList.of("admin", "super-admin"))
          .build();

  @Test
  void
      getRequiredAdditionalPropertyNames_when_hasRequiredAdditionalProperty_then_returnCorrectList() {
    assertEquals(PList.of("super-admin"), REQUIRED_PROPERTIES.getRequiredAdditionalPropertyNames());
  }

  @Test
  void isRequired_when_memberSchemaWithRequiredName_then_returnTrue() {
    final MemberSchema memberSchema =
        new MemberSchema(Name.ofString("admin"), OpenApiSchema.wrapSchema(new Schema<>()));
    assertTrue(REQUIRED_PROPERTIES.isRequired(memberSchema));
  }

  @Test
  void isRequired_when_memberSchemaWithNotRequiredName_then_returnFalse() {
    final MemberSchema memberSchema =
        new MemberSchema(Name.ofString("user"), OpenApiSchema.wrapSchema(new Schema<>()));
    assertFalse(REQUIRED_PROPERTIES.isRequired(memberSchema));
  }
}
