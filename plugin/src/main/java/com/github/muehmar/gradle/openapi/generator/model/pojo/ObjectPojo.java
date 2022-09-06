package com.github.muehmar.gradle.openapi.generator.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.NewPojo;
import com.github.muehmar.gradle.openapi.generator.model.NewPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.NewType;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ObjectPojo implements NewPojo {
  private final PojoName name;
  private final Optional<String> description;
  private final PList<NewPojoMember> members;

  private ObjectPojo(PojoName name, Optional<String> description, PList<NewPojoMember> members) {
    this.name = name;
    this.description = description;
    this.members = members;
  }

  public static ObjectPojo of(PojoName name, String description, PList<NewPojoMember> members) {
    return new ObjectPojo(name, Optional.ofNullable(description), members);
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
    return mapMembers(member -> member.addObjectTypeDescription(objectTypeName, description));
  }

  @Override
  public NewPojo inlineObjectReference(
      PojoName referenceName, String referenceDescription, NewType referenceType) {
    return mapMembers(
        member -> member.inlineObjectReference(referenceName, referenceDescription, referenceType));
  }

  private NewPojo mapMembers(UnaryOperator<NewPojoMember> map) {
    return new ObjectPojo(name, description, members.map(map));
  }

  @Override
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo) {
    return onObjectPojo.apply(this);
  }

  public PList<NewPojoMember> getMembers() {
    return members;
  }
}
