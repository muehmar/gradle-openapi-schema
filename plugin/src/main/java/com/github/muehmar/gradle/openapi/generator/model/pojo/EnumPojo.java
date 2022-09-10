package com.github.muehmar.gradle.openapi.generator.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class EnumPojo implements NewPojo {
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
    return onEnumPojo.apply(this);
  }

  public PList<String> getMembers() {
    return members;
  }
}