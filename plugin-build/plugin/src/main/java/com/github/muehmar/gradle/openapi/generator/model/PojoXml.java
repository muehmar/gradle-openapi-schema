package com.github.muehmar.gradle.openapi.generator.model;

import io.swagger.v3.oas.models.media.Schema;
import java.util.Optional;
import lombok.Value;

@Value
public class PojoXml {
  Optional<String> name;

  public static PojoXml fromSchema(Schema<?> schema) {
    if (schema.getXml() == null) {
      return new PojoXml(Optional.empty());
    }
    final Optional<String> name = Optional.ofNullable(schema.getXml().getName());
    return new PojoXml(name);
  }

  public static PojoXml noXmlDefinition() {
    return new PojoXml(Optional.empty());
  }
}
