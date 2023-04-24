package com.github.muehmar.gradle.openapi.generator.model;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Discriminator {
  private final Name propertyName;
  private final Optional<Map<String, Name>> mapping;

  private Discriminator(Name propertyName, Optional<Map<String, Name>> mapping) {
    this.propertyName = propertyName;
    this.mapping = mapping;
  }

  public static Discriminator fromPropertyName(Name propertyName) {
    return new Discriminator(propertyName, Optional.empty());
  }

  public static Discriminator fromPropertyNameAndMapping(
      Name propertyName, Map<String, Name> mapping) {
    return new Discriminator(propertyName, Optional.of(mapping));
  }

  public Discriminator withMapping(Optional<Map<String, Name>> mapping) {
    return new Discriminator(propertyName, mapping);
  }

  public Name getPropertyName() {
    return propertyName;
  }

  public String getValueForSchemaName(Name schemaName) {
    return mapping.orElse(Collections.emptyMap()).entrySet().stream()
        .filter(e -> e.getValue().equals(schemaName))
        .findFirst()
        .map(Map.Entry::getKey)
        .orElse(schemaName.asString());
  }
}
