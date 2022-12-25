package com.github.muehmar.gradle.openapi.generator.model;

import ch.bluecare.commons.data.PList;
import io.github.muehmar.pojoextension.annotations.SafeBuilder;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Represents a composition of other definitions, i.e. pojos. Distinguishes between different types
 * of compositions, see {@link CompositionType}. The pojos itself are not yet resolved.
 */
@EqualsAndHashCode
@ToString
@SafeBuilder
public class UnresolvedComposedPojo {
  private final PojoName name;
  private final String description;
  private final CompositionType type;
  private final PList<PojoName> pojoNames;
  private final Optional<Discriminator> discriminator;

  public enum CompositionType {
    ALL_OF("AllOf"),
    ANY_OF("AnyOf"),
    ONE_OF("OneOf");

    private final String value;

    CompositionType(String value) {
      this.value = value;
    }

    public String asPascalCaseName() {
      return value;
    }
  }

  public UnresolvedComposedPojo(
      PojoName name,
      String description,
      CompositionType type,
      PList<PojoName> pojoNames,
      Optional<Discriminator> discriminator) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.pojoNames = pojoNames;
    this.discriminator = discriminator;
  }

  public PojoName getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public String getSuffix() {
    return getName().getSuffix();
  }

  public CompositionType getType() {
    return type;
  }

  public PList<PojoName> getPojoNames() {
    return pojoNames;
  }

  public Optional<Discriminator> getDiscriminator() {
    return discriminator;
  }
}
