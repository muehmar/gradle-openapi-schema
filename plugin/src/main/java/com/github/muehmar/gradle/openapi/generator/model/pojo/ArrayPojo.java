package com.github.muehmar.gradle.openapi.generator.model.pojo;

import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ArrayPojo implements Pojo {
  private final PojoName name;
  private final Optional<String> description;
  private final Type itemType;
  private final Constraints constraints;

  private ArrayPojo(
      PojoName name, Optional<String> description, Type itemType, Constraints constraints) {
    this.name = name;
    this.description = description;
    this.itemType = itemType;
    this.constraints = constraints;
  }

  public static ArrayPojo of(
      PojoName name, String description, Type itemType, Constraints constraints) {
    return new ArrayPojo(name, Optional.ofNullable(description), itemType, constraints);
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
  public Pojo addObjectTypeDescription(PojoName objectTypeName, String description) {
    return this;
  }

  @Override
  public Pojo inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType) {
    return this;
  }

  @Override
  public ArrayPojo applyMapping(PojoNameMapping pojoNameMapping) {
    final PojoName mappedName = pojoNameMapping.map(name);
    final Type mappedItemType = itemType.applyMapping(pojoNameMapping);
    return new ArrayPojo(mappedName, description, mappedItemType, constraints);
  }

  @Override
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo) {
    return onArrayType.apply(this);
  }

  public Type getItemType() {
    return itemType;
  }

  public Constraints getConstraints() {
    return constraints;
  }
}
