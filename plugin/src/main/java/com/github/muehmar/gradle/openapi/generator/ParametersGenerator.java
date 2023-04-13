package com.github.muehmar.gradle.openapi.generator;

import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.writer.GeneratedFile;

public interface ParametersGenerator {
  GeneratedFile generate(Parameter parameter, PojoSettings settings);
}
