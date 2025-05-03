package com.github.muehmar.gradle.openapi.generator.model;

import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.media.XML;
import java.util.Optional;
import lombok.Value;

@Value
public class PojoMemberXml {
  Optional<String> name;
  Optional<Boolean> isAttribute;

  public static PojoMemberXml noDefinition() {
    return new PojoMemberXml(Optional.empty(), Optional.empty());
  }

  public static PojoMemberXml fromSchema(Schema<?> schema) {
    final XML xml = schema.getXml();
    if (xml == null) {
      return PojoMemberXml.noDefinition();
    }
    final Optional<String> name = Optional.ofNullable(xml.getName());
    final Optional<Boolean> isAttribute = Optional.ofNullable(xml.getAttribute());
    return new PojoMemberXml(name, isAttribute);
  }
}
