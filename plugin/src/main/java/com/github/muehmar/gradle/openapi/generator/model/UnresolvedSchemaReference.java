package com.github.muehmar.gradle.openapi.generator.model;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.SchemaName;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class UnresolvedSchemaReference {
  private final ComponentName componentName;
  private final SchemaName schemaName;

  public UnresolvedSchemaReference(ComponentName componentName, SchemaName schemaName) {
    this.componentName = componentName;
    this.schemaName = schemaName;
  }

  public ComponentName getComponentName() {
    return componentName;
  }

  public SchemaName getSchemaName() {
    return schemaName;
  }

  public Optional<Pojo> resolve(PList<Pojo> pojos) {
    return pojos
        .filter(p -> p.getName().getSchemaName().equals(schemaName))
        .map(p -> p.replaceName(componentName))
        .headOption();
  }
}
