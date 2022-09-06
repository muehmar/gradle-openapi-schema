package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.TypeMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import io.swagger.v3.oas.models.media.ArraySchema;
import java.util.Optional;

public class ArrayOpenApiProcessor extends BaseSingleSchemaOpenApiProcessor {

  @Override
  public Optional<NewSchemaProcessResult> process(
      OpenApiPojo openApiPojo, NewCompleteOpenApiProcessor completeOpenApiProcessor) {
    if (openApiPojo.getSchema() instanceof ArraySchema) {
      final NewPojoProcessResult pojoProcessResult =
          fromArraysSchema(openApiPojo.getPojoName(), (ArraySchema) openApiPojo.getSchema());
      return Optional.of(processPojoProcessResult(pojoProcessResult, completeOpenApiProcessor));
    } else {
      return Optional.empty();
    }
  }

  private NewPojoProcessResult fromArraysSchema(PojoName pojoName, ArraySchema schema) {

    final TypeMapResult typeMapResult =
        COMPLETE_TYPE_MAPPER.map(pojoName, Name.of("value"), schema.getItems());

    final ArrayPojo pojo = ArrayPojo.of(pojoName, schema.getDescription(), typeMapResult.getType());
    return new NewPojoProcessResult(pojo, typeMapResult.getOpenApiPojos());
  }
}
