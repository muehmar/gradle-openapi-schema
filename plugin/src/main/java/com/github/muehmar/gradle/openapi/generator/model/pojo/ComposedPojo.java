package com.github.muehmar.gradle.openapi.generator.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo;
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

  public static ComposedPojo resolvedAnyOf(
      PList<Pojo> pojos, UnresolvedComposedPojo unresolvedComposedPojo) {
    return resolved(pojos, CompositionType.ANY_OF, unresolvedComposedPojo);
  }

  public static ComposedPojo resolvedOneOf(
      PList<Pojo> pojos, UnresolvedComposedPojo unresolvedComposedPojo) {
    return resolved(pojos, CompositionType.ONE_OF, unresolvedComposedPojo);
  }

  private static ComposedPojo resolved(
      PList<Pojo> resolvedPojos,
      CompositionType compositionType,
      UnresolvedComposedPojo unresolvedComposedPojo) {
    return new ComposedPojo(
        unresolvedComposedPojo.getName(),
        unresolvedComposedPojo.getDescription(),
        resolvedPojos,
        compositionType,
        unresolvedComposedPojo.getDiscriminator());
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
      Function<ComposedPojo, T> onComposedPojo,
      Function<FreeFormPojo, T> onFreeFormPojo) {
    return onComposedPojo.apply(this);
  }
}
