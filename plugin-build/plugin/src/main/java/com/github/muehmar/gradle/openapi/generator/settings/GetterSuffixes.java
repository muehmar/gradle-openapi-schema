package com.github.muehmar.gradle.openapi.generator.settings;

import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.io.Serializable;
import lombok.Value;

@Value
@PojoBuilder
public class GetterSuffixes implements Serializable {
  String requiredSuffix;
  String requiredNullableSuffix;
  String optionalSuffix;
  String optionalNullableSuffix;
}
