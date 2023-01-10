package com.github.muehmar.gradle.openapi.generator.model.pojo;

import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class FreeFormPojo implements Pojo {
  private final PojoName name;
  private final String description;

  private FreeFormPojo(PojoName name, String description) {
    this.name = name;
    this.description = description;
  }

  public static FreeFormPojo of(PojoName name, String description) {
    return new FreeFormPojo(name, description);
  }

  @Override
  public PojoName getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
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
