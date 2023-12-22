package com.github.muehmar.gradle.openapi.generator.model.composition;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class UntypedDiscriminator {
  private final Name propertyName;
  private final Optional<Map<String, Name>> mapping;

  private UntypedDiscriminator(Name propertyName, Optional<Map<String, Name>> mapping) {
    this.propertyName = propertyName;
    this.mapping = mapping;
  }

  public static UntypedDiscriminator fromPropertyName(Name propertyName) {
    return new UntypedDiscriminator(propertyName, Optional.empty());
  }

  public static UntypedDiscriminator fromPropertyNameAndMapping(
      Name propertyName, Map<String, Name> mapping) {
    return new UntypedDiscriminator(propertyName, Optional.of(mapping));
  }

  public UntypedDiscriminator withMapping(Optional<Map<String, Name>> mapping) {
    return new UntypedDiscriminator(propertyName, mapping);
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
