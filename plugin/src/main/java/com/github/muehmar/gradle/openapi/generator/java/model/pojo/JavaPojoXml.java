package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import java.util.Optional;
import lombok.Value;

@Value
public class JavaPojoXml {
  Optional<String> name;

  public static JavaPojoXml noXmlDefinition() {
    return new JavaPojoXml(Optional.empty());
  }
}
