package com.github.muehmar.gradle.openapi.generator.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.name.ComponentName;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.settings.PojoNameMapping;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class EnumPojo implements Pojo {
  private final ComponentName name;
  private final String description;
  private final PList<String> members;

  private EnumPojo(ComponentName name, String description, PList<String> members) {
    this.name = name;
    this.description = description;
    this.members = members;
  }

  public static EnumPojo of(ComponentName name, String description, PList<String> members) {
    return new EnumPojo(name, description, members);
  }

  @Override
  public ComponentName getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public Pojo replaceObjectType(
      PojoName objectTypeName, String newObjectTypeDescription, Type newObjectType) {
    return this;
  }

  @Override
  public Pojo adjustNullablePojo(PojoName nullablePojo) {
    return this;
  }

  @Override
  public EnumPojo applyMapping(PojoNameMapping pojoNameMapping) {
    final ComponentName mappedName = name.applyPojoMapping(pojoNameMapping);
    return new EnumPojo(mappedName, description, members);
  }

  @Override
  public Pojo replaceName(ComponentName name) {
    return new EnumPojo(name, description, members);
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
