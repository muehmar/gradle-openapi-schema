package com.github.muehmar.gradle.openapi.generator.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class EnumPojo implements Pojo {
  private final PojoName name;
  private final String description;
  private final PList<String> members;

  private EnumPojo(PojoName name, String description, PList<String> members) {
    this.name = name;
    this.description = description;
    this.members = members;
  }

  public static EnumPojo of(PojoName name, String description, PList<String> members) {
    return new EnumPojo(name, description, members);
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
    return onEnumPojo.apply(this);
  }

  public PList<String> getMembers() {
    return members;
  }
}
