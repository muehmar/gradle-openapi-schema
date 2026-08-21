package com.github.muehmar.gradle.openapi.generator.settings;

import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.io.Serializable;
import lombok.Value;
import lombok.With;

@Value
@With
@PojoBuilder
public class ValidationMethods implements Serializable {
  JavaModifier modifier;
  String getterSuffix;
  boolean deprecatedAnnotation;
}
