package com.github.muehmar.gradle.openapi.generator.mapper.resolver;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.mapper.pojoschema.PojoSchemaMapResult;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;

public interface PojoSchemaMapResultResolver {
  PList<Pojo> resolve(PojoSchemaMapResult pojoSchemaMapResult);
}
