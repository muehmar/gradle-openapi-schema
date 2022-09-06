package com.github.muehmar.gradle.openapi.generator.mapper.processor;

import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

abstract class BaseSingleSchemaOpenApiProcessor implements SingleSchemaOpenApiProcessor {

  protected NewSchemaProcessResult processPojoProcessResult(
      NewPojoProcessResult pojoProcessResult,
      PojoSettings pojoSettings,
      NewCompleteOpenApiProcessor completeOpenApiProcessor) {
    return pojoProcessResult
        .getOpenApiPojos()
        .map(oaPojo -> completeOpenApiProcessor.process(oaPojo, pojoSettings))
        .foldRight(NewSchemaProcessResult.empty(), NewSchemaProcessResult::concat)
        .addPojo(pojoProcessResult.getPojo());
  }
}
