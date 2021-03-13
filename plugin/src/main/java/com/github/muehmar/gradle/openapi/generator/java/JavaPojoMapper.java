package com.github.muehmar.gradle.openapi.generator.java;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.Pojo;
import com.github.muehmar.gradle.openapi.generator.PojoMapper;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import io.swagger.v3.oas.models.media.Schema;

public class JavaPojoMapper implements PojoMapper {
  @Override
  public PList<Pojo> fromSchema(String key, Schema<?> schema, PojoSettings pojoSettings) {
    return PList.single(JavaPojo.fromSchema(key, schema, pojoSettings));
  }
}
