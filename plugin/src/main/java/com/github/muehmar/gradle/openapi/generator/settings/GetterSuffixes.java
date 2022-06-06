package com.github.muehmar.gradle.openapi.generator.settings;

import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import java.io.Serializable;
import lombok.Value;

@Value
@SafeBuilder
public class GetterSuffixes implements Serializable {
  String requiredSuffix;
  String requiredNullableSuffix;
  String optionalSuffix;
  String optionalNullableSuffix;
}
