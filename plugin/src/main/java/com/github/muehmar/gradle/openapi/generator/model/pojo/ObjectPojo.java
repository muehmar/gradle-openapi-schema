package com.github.muehmar.gradle.openapi.generator.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ObjectPojo implements NewPojo {
  private final Name name;
  private final String description;
  private final String suffix;
  private final PList<PojoMember> members;

  private ObjectPojo(Name name, String description, String suffix, PList<PojoMember> members) {
    this.name = name;
    this.description = description;
    this.suffix = suffix;
    this.members = members;
  }

  @Override
  public Name getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo) {
    return onObjectPojo.apply(this);
  }

  public PList<PojoMember> getMembers() {
    return members;
  }
}
