package com.github.muehmar.gradle.openapi.generator;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Parameter;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;

public interface ParametersGenerator {
  void generate(PList<Parameter> parameters, PojoSettings settings);
}
