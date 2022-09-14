package com.github.muehmar.gradle.openapi.generator.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import java.util.Optional;
import java.util.function.Function;

public class ComposedPojo implements Pojo {
  private final PojoName name;
  private final String description;
  private final PList<Pojo> pojos;
  private final CompositionType compositionType;
  private final Optional<Discriminator> discriminator;

  private ComposedPojo(
      PojoName name,
      String description,
      PList<Pojo> pojos,
      CompositionType compositionType,
      Optional<Discriminator> discriminator) {
    this.name = name;
    this.description = description;
    this.pojos = pojos;
    this.compositionType = compositionType;
    this.discriminator = discriminator;
  }

  public static ComposedPojo anyOf(
      PojoName name, String description, PList<Pojo> pojos, Optional<Discriminator> discriminator) {
    return new ComposedPojo(name, description, pojos, CompositionType.ANY_OF, discriminator);
  }

  public static ComposedPojo oneOf(
      PojoName name, String description, PList<Pojo> pojos, Optional<Discriminator> discriminator) {
    return new ComposedPojo(name, description, pojos, CompositionType.ONE_OF, discriminator);
  }

  public enum CompositionType {
    ANY_OF,
    ONE_OF
  }

  @Override
  public PojoName getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public PList<Pojo> getPojos() {
    return pojos;
  }

  public CompositionType getCompositionType() {
    return compositionType;
  }

  public Optional<Discriminator> getDiscriminator() {
    return discriminator;
  }

  @Override
  public Pojo addObjectTypeDescription(PojoName objectTypeName, String description) {
    return new ComposedPojo(
        name,
        description,
        pojos.map(pojo -> pojo.addObjectTypeDescription(objectTypeName, description)),
        compositionType,
        discriminator);
  }

  @Override
  public Pojo inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType) {
    return new ComposedPojo(
        name,
        description,
        pojos.map(
            pojo -> pojo.inlineObjectReference(referenceName, referenceDescription, referenceType)),
        compositionType,
        discriminator);
  }

  @Override
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo,
      Function<ComposedPojo, T> onComposedPojo) {
    return onComposedPojo.apply(this);
  }
}
