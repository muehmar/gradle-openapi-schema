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
  private final Optional<Map<String, PojoName>> mapping;

  private Discriminator(Name propertyName, Optional<Map<String, PojoName>> mapping) {
    this.propertyName = propertyName;
    this.mapping = mapping;
  }

  public static Discriminator fromPropertyName(Name propertyName) {
    return new Discriminator(propertyName, Optional.empty());
  }

  public static Discriminator fromPropertyNameAndMapping(
      Name propertyName, Map<String, PojoName> mapping) {
    return new Discriminator(propertyName, Optional.of(mapping));
  }

  public Discriminator withMapping(Optional<Map<String, PojoName>> mapping) {
    return new Discriminator(propertyName, mapping);
  }

  public Name getPropertyName() {
    return propertyName;
  }

  public String getValueForPojoName(PojoName pojoName) {
    return mapping.orElse(Collections.emptyMap()).entrySet().stream()
        .filter(e -> e.getValue().equals(pojoName))
        .findFirst()
        .map(Map.Entry::getKey)
        .orElse(pojoName.getName().asString());
  }
}
