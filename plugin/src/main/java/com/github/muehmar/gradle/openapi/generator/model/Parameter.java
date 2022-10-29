package com.github.muehmar.gradle.openapi.generator.model;

import java.util.Optional;
import lombok.Value;

@Value
public class Parameter {
  Name name;
  Type type;
  Optional<Object> defaultValue;
}
