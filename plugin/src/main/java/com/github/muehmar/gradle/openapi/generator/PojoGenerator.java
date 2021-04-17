package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

/** Generates the classes from the {@link Pojo}'s. */
@FunctionalInterface
public interface PojoGenerator {
  void generatePojo(Pojo pojo, PojoSettings pojoSettings);
}
