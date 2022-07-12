package com.github.muehmar.gradle.openapi.generator.settings;

import io.github.muehmar.pojoextension.annotations.PojoExtension;
import java.io.Serializable;
import lombok.Value;

@Value
@PojoExtension
public class ValidationGetter implements Serializable {
  JavaModifier modifier;
  String suffix;
  boolean deprecatedAnnotation;
}
