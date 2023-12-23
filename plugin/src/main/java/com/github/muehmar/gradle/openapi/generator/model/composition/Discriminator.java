package com.github.muehmar.gradle.openapi.generator.model.composition;

import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Discriminator {
  private final Name propertyName;
  private final DiscriminatorType type;
  private final Optional<Map<String, Name>> mapping;

  private Discriminator(
      Name propertyName, DiscriminatorType type, Optional<Map<String, Name>> mapping) {
    this.propertyName = propertyName;
    this.type = type;
    this.mapping = mapping;
  }

  public static Discriminator typeDiscriminator(
      UntypedDiscriminator untypedDiscriminator, DiscriminatorType type) {
    return new Discriminator(
        untypedDiscriminator.getPropertyName(), type, untypedDiscriminator.getMapping());
  }

  public Name getPropertyName() {
    return propertyName;
  }

  public DiscriminatorType getType() {
    return type;
  }

  public String getValueForSchemaName(Name schemaName) {
    return mapping.orElse(Collections.emptyMap()).entrySet().stream()
        .filter(e -> e.getValue().equals(schemaName))
        .findFirst()
        .map(Map.Entry::getKey)
        .orElse(schemaName.asString());
  }
}
