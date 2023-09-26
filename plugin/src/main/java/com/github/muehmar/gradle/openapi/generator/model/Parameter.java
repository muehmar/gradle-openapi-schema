package com.github.muehmar.gradle.openapi.generator.model;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.Optional;
import lombok.Value;

@Value
public class Parameter {
  Name name;
  Type type;
  Optional<Object> defaultValue;
}
