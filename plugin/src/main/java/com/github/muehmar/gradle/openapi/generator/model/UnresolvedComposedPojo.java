package com.github.muehmar.gradle.openapi.generator.model;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.Optional;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Represents a composition of other definitions, i.e. pojos. Distinguishes between different types
 * of compositions, see {@link CompositionType}. The pojos itself are not yet resolved.
 */
@EqualsAndHashCode
@ToString
@PojoBuilder
public class UnresolvedComposedPojo {
  private final PojoName name;
  private final String description;
  private final CompositionType type;
  private final PList<PojoName> pojoNames;
  private final Constraints constraints;
  private final Optional<UntypedDiscriminator> discriminator;

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
      Constraints constraints,
      Optional<UntypedDiscriminator> discriminator) {
    this.name = name;
    this.description = description;
    this.type = type;
    this.pojoNames = pojoNames;
    this.constraints = constraints;
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

  public Constraints getConstraints() {
    return constraints;
  }

  public Optional<UntypedDiscriminator> getDiscriminator() {
    return discriminator;
  }
}
