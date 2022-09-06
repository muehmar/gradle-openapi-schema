package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.CompleteTypeMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.TypeMapResult;
import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.TypeMapper;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.ArraySchema;
import java.util.Optional;

public class ArrayOpenApiProcessor extends BaseSingleSchemaOpenApiProcessor {
  private static final TypeMapper COMPLETE_TYPE_MAPPER = CompleteTypeMapper.create();

  @Override
  public Optional<NewSchemaProcessResult> process(
      OpenApiPojo openApiPojo,
      PojoSettings pojoSettings,
      NewCompleteOpenApiProcessor completeOpenApiProcessor) {
    if (openApiPojo.getSchema() instanceof ArraySchema) {
      final NewPojoProcessResult pojoProcessResult =
          fromArraysSchema(openApiPojo.getPojoName(), (ArraySchema) openApiPojo.getSchema());
      return Optional.of(
          processPojoProcessResult(pojoProcessResult, pojoSettings, completeOpenApiProcessor));
    } else {
      return Optional.empty();
    }
  }

  private NewPojoProcessResult fromArraysSchema(PojoName pojoName, ArraySchema schema) {

    final TypeMapResult typeMapResult =
        COMPLETE_TYPE_MAPPER.mapThrowing(pojoName, Name.of("value"), schema);

    final ArrayPojo pojo = ArrayPojo.of(pojoName, schema.getDescription(), typeMapResult.getType());
    return new NewPojoProcessResult(pojo, typeMapResult.getOpenApiPojos());
  }
}
