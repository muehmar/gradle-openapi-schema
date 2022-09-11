package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.mapper.ConstraintsMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.TypeMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.OpenApiPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import io.swagger.v3.oas.models.media.ArraySchema;
import java.util.Optional;

public class ArrayOpenApiProcessor extends BaseSingleSchemaOpenApiProcessor {

  @Override
  public Optional<SchemaProcessResult> process(
      OpenApiPojo openApiPojo, CompleteOpenApiProcessor completeOpenApiProcessor) {
    if (openApiPojo.getSchema() instanceof ArraySchema) {
      final PojoProcessResult pojoProcessResult =
          fromArraysSchema(openApiPojo.getPojoName(), (ArraySchema) openApiPojo.getSchema());
      return Optional.of(processPojoProcessResult(pojoProcessResult, completeOpenApiProcessor));
    } else {
      return Optional.empty();
    }
  }

  private PojoProcessResult fromArraysSchema(PojoName pojoName, ArraySchema schema) {
    final Constraints constraints = ConstraintsMapper.getMinAndMaxItems(schema);

    final TypeMapResult typeMapResult =
        COMPLETE_TYPE_MAPPER.map(pojoName, Name.ofString("value"), schema.getItems());

    final ArrayPojo pojo =
        ArrayPojo.of(pojoName, schema.getDescription(), typeMapResult.getType(), constraints);
    return new PojoProcessResult(pojo, typeMapResult.getOpenApiPojos());
  }
}
