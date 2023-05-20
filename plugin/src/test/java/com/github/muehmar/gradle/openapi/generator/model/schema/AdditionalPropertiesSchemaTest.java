package com.github.muehmar.gradle.openapi.generator.model.schema;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.ObjectSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class AdditionalPropertiesSchemaTest {

  @ParameterizedTest
  @NullSource
  @ValueSource(booleans = true)
  void wrapNullable_when_nullOrTrue_then_additionalPropertiesAllowedAndAnyType(
      Object additionalProperties) {
    final AdditionalPropertiesSchema additionalPropertiesSchema =
        AdditionalPropertiesSchema.wrapNullable(additionalProperties);

    final Type additionalPropertiesType =
        additionalPropertiesSchema.getAdditionalPropertiesType(
            PojoName.ofNameAndSuffix("Map", "Dto"));

    assertTrue(additionalPropertiesSchema.isAllowed());
    assertEquals(AnyType.create(), additionalPropertiesType);
  }

  @Test
  void wrapNullable_when_false_then_additionalPropertiesNotAllowed() {
    final AdditionalPropertiesSchema additionalPropertiesSchema =
        AdditionalPropertiesSchema.wrapNullable(false);

    assertFalse(additionalPropertiesSchema.isAllowed());
  }

  @Test
  void wrapNullable_when_schema_then_additionalPropertiesAllowedAndCorrectType() {
    final ObjectSchema objectSchema = new ObjectSchema();
    objectSchema.setProperties(Collections.emptyMap());

    final AdditionalPropertiesSchema additionalPropertiesSchema =
        AdditionalPropertiesSchema.wrapNullable(objectSchema);

    final PojoName pojoName = PojoName.ofNameAndSuffix("User", "Dto");
    final Type additionalPropertiesType =
        additionalPropertiesSchema.getAdditionalPropertiesType(pojoName);

    assertTrue(additionalPropertiesSchema.isAllowed());
    assertEquals(
        MapType.ofKeyAndValueType(StringType.noFormat(), AnyType.create()),
        additionalPropertiesType);
  }

  @Test
  void getAdditionalPropertiesMapResult_when_arraySchema_then_newArrayPojoCreated() {
    final ArraySchema arraySchema = new ArraySchema();
    arraySchema.setItems(new StringSchema());

    final AdditionalPropertiesSchema additionalPropertiesSchema =
        AdditionalPropertiesSchema.wrapNullable(arraySchema);

    final PojoName pojoName = PojoName.ofNameAndSuffix("User", "Dto");
    // method call
    final MemberSchemaMapResult mapResult =
        additionalPropertiesSchema.getAdditionalPropertiesMapResult(pojoName);

    final PojoName newPojoSchemaName =
        PojoName.deriveOpenApiPojoName(pojoName, Name.ofString("Property"));
    assertEquals(ObjectType.ofName(newPojoSchemaName), mapResult.getType());

    assertEquals(1, mapResult.getUnmappedItems().getPojoSchemas().size());
    assertEquals(
        new PojoSchema(newPojoSchemaName, arraySchema),
        mapResult.getUnmappedItems().getPojoSchemas().apply(0));
  }
}
