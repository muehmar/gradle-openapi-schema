package com.github.muehmar.gradle.openapi.generator.mapper.pojoschema;

import com.github.muehmar.gradle.openapi.generator.mapper.MapContext;
import com.github.muehmar.gradle.openapi.generator.model.PojoSchema;
import com.github.muehmar.gradle.openapi.generator.model.pojo.FreeFormPojo;
import io.swagger.v3.oas.models.media.MapSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;

public class MapPojoSchemaMapper implements SinglePojoSchemaMapper {
  @Override
  public Optional<MapContext> map(PojoSchema pojoSchema) {
    if (pojoSchema.getSchema() instanceof MapSchema) {
      final MapSchema mapSchema = (MapSchema) pojoSchema.getSchema();
      if (mapSchema.getProperties() == null && hasAdditionalPropertiesTrueOrNoSchema(mapSchema)) {
        return Optional.of(freeFormObject(pojoSchema));
      }
    }
    return Optional.empty();
  }

  private MapContext freeFormObject(PojoSchema pojoSchema) {
    final String description =
        Optional.ofNullable(pojoSchema.getSchema().getDescription()).orElse("");
    final FreeFormPojo freeFormPojo = FreeFormPojo.of(pojoSchema.getPojoName(), description);
    return MapContext.ofPojo(freeFormPojo);
  }

  private boolean hasAdditionalPropertiesTrueOrNoSchema(MapSchema mapSchema) {
    final boolean isTrue =
        mapSchema.getAdditionalProperties() instanceof Boolean
            && (Boolean) mapSchema.getAdditionalProperties();

    final boolean noSchema =
        mapSchema.getAdditionalProperties() instanceof Schema
            && (((Schema<?>) mapSchema.getAdditionalProperties()).getProperties() == null
                || ((Schema<?>) mapSchema.getAdditionalProperties()).getAdditionalProperties()
                    == null
                || ((Schema<?>) mapSchema.getAdditionalProperties()).get$ref() == null);
    return isTrue || noSchema;
  }
}
