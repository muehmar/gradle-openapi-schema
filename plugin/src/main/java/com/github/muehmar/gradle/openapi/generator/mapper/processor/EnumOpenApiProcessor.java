package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.StringSchema;
import java.util.Objects;
import java.util.Optional;

public class EnumOpenApiProcessor extends BaseSingleSchemaOpenApiProcessor {
  @Override
  public Optional<NewSchemaProcessResult> process(
      OpenApiPojo openApiPojo,
      PojoSettings pojoSettings,
      NewCompleteOpenApiProcessor completeOpenApiProcessor) {
    final Schema<?> schema = openApiPojo.getSchema();
    if (schema instanceof StringSchema && Objects.nonNull(schema.getEnum())) {
      final StringSchema stringSchema = (StringSchema) schema;
      final EnumPojo enumPojo =
          EnumPojo.of(
              openApiPojo.getPojoName(),
              schema.getDescription(),
              PList.fromIter(stringSchema.getEnum()));
      return Optional.ofNullable(NewSchemaProcessResult.ofPojo(enumPojo));
    } else {
      return Optional.empty();
    }
  }
}
