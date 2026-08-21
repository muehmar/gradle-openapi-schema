package com.github.muehmar.gradle.openapi.generator.model.schema;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.model.schema.SchemaWrappers.wrap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.github.muehmar.gradle.openapi.generator.mapper.MemberSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.specification.OpenApiSpec;
import com.github.muehmar.gradle.openapi.generator.model.type.AnyType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import io.swagger.v3.oas.models.media.ArraySchema;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

class AdditionalPropertiesSchemaTest {
  private static final OpenApiSpec SPEC = OpenApiSpec.fromPath(Path.of("spec.yml"));

  @ParameterizedTest
  @NullSource
  @ValueSource(booleans = true)
  void wrapNullable_when_nullOrTrue_then_additionalPropertiesAllowedAndAnyType(
      Object additionalProperties) {
    final AdditionalPropertiesSchema additionalPropertiesSchema =
        AdditionalPropertiesSchema.wrapNullable(SPEC, additionalProperties);

    final Type additionalPropertiesType =
        additionalPropertiesSchema.getAdditionalPropertiesType(componentName("Map", "Dto"));

    assertTrue(additionalPropertiesSchema.isAllowed());
    assertEquals(AnyType.create(NULLABLE), additionalPropertiesType);
  }

  @Test
  void wrapNullable_when_false_then_additionalPropertiesNotAllowed() {
    final AdditionalPropertiesSchema additionalPropertiesSchema =
        AdditionalPropertiesSchema.wrapNullable(SPEC, false);

    assertFalse(additionalPropertiesSchema.isAllowed());
  }

  @Test
  void getAdditionalPropertiesMapResult_when_arraySchema_then_newArrayPojoCreated() {
    final ArraySchema arraySchema = new ArraySchema();
    arraySchema.setItems(new StringSchema());

    final AdditionalPropertiesSchema additionalPropertiesSchema =
        AdditionalPropertiesSchema.wrapNullable(SPEC, arraySchema);

    final ComponentName componentName = componentName("User", "Dto");
    // method call
    final MemberSchemaMapResult mapResult =
        additionalPropertiesSchema.getAdditionalPropertiesMapResult(componentName);

    final ComponentName newComponentName =
        componentName.deriveMemberSchemaName(Name.ofString("Property"));
    assertEquals(StandardObjectType.ofName(newComponentName.getPojoName()), mapResult.getType());

    assertEquals(1, mapResult.getUnmappedItems().getPojoSchemas().size());
    assertEquals(
        new PojoSchema(newComponentName, wrap(arraySchema)),
        mapResult.getUnmappedItems().getPojoSchemas().apply(0));
  }

  @Test
  void getAdditionalPropertiesMapResult_when_mapSchema_then_newMapPojoCreated() {
    final MapSchema mapSchema = new MapSchema();
    mapSchema.setAdditionalProperties(new StringSchema());

    final AdditionalPropertiesSchema additionalPropertiesSchema =
        AdditionalPropertiesSchema.wrapNullable(OpenApiSpec.fromPath(Path.of(".")), mapSchema);

    final ComponentName componentName = componentName("User", "Dto");
    // method call
    final MemberSchemaMapResult mapResult =
        additionalPropertiesSchema.getAdditionalPropertiesMapResult(componentName);

    final ComponentName newComponentName =
        componentName.deriveMemberSchemaName(Name.ofString("Property"));
    assertEquals(StandardObjectType.ofName(newComponentName.getPojoName()), mapResult.getType());

    assertEquals(1, mapResult.getUnmappedItems().getPojoSchemas().size());
    assertEquals(
        new PojoSchema(newComponentName, wrap(mapSchema)),
        mapResult.getUnmappedItems().getPojoSchemas().apply(0));
  }
}
