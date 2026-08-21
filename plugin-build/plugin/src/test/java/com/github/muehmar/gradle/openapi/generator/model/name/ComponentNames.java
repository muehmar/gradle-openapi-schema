package com.github.muehmar.gradle.openapi.generator.model.name;

public class ComponentNames {
  private ComponentNames() {}

  public static ComponentName componentName(String schemaString, String suffix) {
    return ComponentName.fromSchemaStringAndSuffix(schemaString, suffix);
  }

  public static ComponentName componentName(String pojoName, String suffix, String schemaName) {
    return new ComponentName(
        new PojoName(Name.ofString(pojoName), suffix),
        SchemaName.ofName(Name.ofString(schemaName)));
  }
}
