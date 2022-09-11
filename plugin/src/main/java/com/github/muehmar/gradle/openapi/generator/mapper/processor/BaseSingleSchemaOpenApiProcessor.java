package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.CompleteTypeMapper;
import com.github.muehmar.gradle.openapi.generator.mapper.typemapper.CompleteTypeMapperFactory;

abstract class BaseSingleSchemaOpenApiProcessor implements SingleSchemaOpenApiProcessor {
  protected static final CompleteTypeMapper COMPLETE_TYPE_MAPPER =
      CompleteTypeMapperFactory.create();

  protected SchemaProcessResult processPojoProcessResult(
      PojoProcessResult pojoProcessResult, CompleteOpenApiProcessor completeOpenApiProcessor) {
    return pojoProcessResult
        .getOpenApiPojos()
        .map(completeOpenApiProcessor::process)
        .foldRight(SchemaProcessResult.empty(), SchemaProcessResult::concat)
        .addPojo(pojoProcessResult.getPojo());
  }
}
