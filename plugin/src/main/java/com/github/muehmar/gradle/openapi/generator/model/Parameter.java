package com.github.muehmar.gradle.openapi.generator.model;

import lombok.Value;

@Value
public class Parameter {
  Name name;
  Type type;
}
