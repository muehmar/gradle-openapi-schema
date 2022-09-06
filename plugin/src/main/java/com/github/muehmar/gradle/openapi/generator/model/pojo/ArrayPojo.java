package com.github.muehmar.gradle.openapi.generator.model.pojo;

import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ArrayPojo implements NewPojo {
  private final PojoName name;
  private final Optional<String> description;
  private final NewType itemType;

  private ArrayPojo(PojoName name, Optional<String> description, NewType itemType) {
    this.name = name;
    this.description = description;
    this.itemType = itemType;
  }

  public static ArrayPojo of(PojoName name, String description, NewType itemType) {
    return new ArrayPojo(name, Optional.ofNullable(description), itemType);
  }

  @Override
  public PojoName getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description.orElse("");
  }

  @Override
  public NewPojo addObjectTypeDescription(PojoName objectTypeName, String description) {
    return this;
  }

  @Override
  public NewPojo inlineObjectReference(
      PojoName referenceName, String referenceDescription, NewType referenceType) {
    return this;
  }

  @Override
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo) {
    return onArrayType.apply(this);
  }

  public NewType getItemType() {
    return itemType;
  }
}
