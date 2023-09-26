package com.github.muehmar.gradle.openapi.generator.model.name;

public class ComponentNames {
  private ComponentNames() {}

  public static ComponentName componentName(String schemaString, String suffix) {
    return ComponentName.fromSchemaStringAndSuffix(schemaString, suffix);
  }
}
