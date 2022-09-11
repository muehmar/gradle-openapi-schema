package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

@FunctionalInterface
public interface PojoGenerator {
  void generatePojo(Pojo pojo, PojoSettings pojoSettings);
}
