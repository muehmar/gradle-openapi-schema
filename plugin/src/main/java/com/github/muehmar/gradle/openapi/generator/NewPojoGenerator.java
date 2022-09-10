package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

@FunctionalInterface
public interface NewPojoGenerator {
  void generatePojo(NewPojo pojo, PojoSettings pojoSettings);
}
