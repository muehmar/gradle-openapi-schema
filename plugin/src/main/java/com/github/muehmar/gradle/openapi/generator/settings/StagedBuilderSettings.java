package com.github.muehmar.gradle.openapi.generator.settings;

import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.io.Serializable;
import lombok.Value;

@PojoBuilder
@Value
public class StagedBuilderSettings implements Serializable {
  boolean enabled;
}
