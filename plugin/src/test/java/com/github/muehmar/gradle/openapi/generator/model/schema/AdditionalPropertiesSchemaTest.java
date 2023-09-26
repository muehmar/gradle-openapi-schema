package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
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
        additionalPropertiesSchema.getAdditionalPropertiesType(componentName("Map", "Dto"));

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

    final ComponentName componentName = componentName("User", "Dto");
    final Type additionalPropertiesType =
        additionalPropertiesSchema.getAdditionalPropertiesType(componentName);

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

    final ComponentName componentName = componentName("User", "Dto");
    // method call
    final MemberSchemaMapResult mapResult =
        additionalPropertiesSchema.getAdditionalPropertiesMapResult(componentName);

    final ComponentName newComponentName =
        componentName.deriveMemberSchemaName(Name.ofString("Property"));
    assertEquals(ObjectType.ofName(newComponentName.getPojoName()), mapResult.getType());

    assertEquals(1, mapResult.getUnmappedItems().getPojoSchemas().size());
    assertEquals(
        new PojoSchema(newComponentName, arraySchema),
        mapResult.getUnmappedItems().getPojoSchemas().apply(0));
  }
}
