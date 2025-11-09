package com.github.muehmar.gradle.openapi.generator.java.model.type.api;

import com.github.muehmar.gradle.openapi.generator.java.model.name.ParameterizedApiClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import lombok.Value;

@Value
public class PluginApiType {
  QualifiedClassName className;
  ParameterizedApiClassName parameterizedClassName;
  ToApiTypeConversion toApiTypeConversion;
  FromApiTypeConversion fromApiTypeConversion;
}
