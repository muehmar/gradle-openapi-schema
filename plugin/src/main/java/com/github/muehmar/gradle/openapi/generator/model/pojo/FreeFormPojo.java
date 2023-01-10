package com.github.muehmar.gradle.openapi.generator.model.pojo;

import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FreeFormPojo implements Pojo {
  private final PojoName name;
  private final String description;
  private final Constraints constraints;

  private FreeFormPojo(PojoName name, String description, Constraints constraints) {
    this.name = name;
    this.description = description;
    this.constraints = constraints;
  }

  public static FreeFormPojo of(PojoName name, String description, Constraints constraints) {
    return new FreeFormPojo(name, description, constraints);
  }

  @Override
  public PojoName getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public Constraints getConstraints() {
    return constraints;
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
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo,
      Function<ComposedPojo, T> onComposedPojo,
      Function<FreeFormPojo, T> onFreeFormPojo) {
    return onFreeFormPojo.apply(this);
  }
}
