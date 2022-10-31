package com.github.muehmar.gradle.openapi.generator;

import lombok.Value;

@Value
public class Generators {
  PojoGenerator pojoGenerator;
  ParametersGenerator parametersGenerator;
}
