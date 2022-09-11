package com.github.muehmar.gradle.openapi.generator.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class ObjectPojo implements Pojo {
  private final PojoName name;
  private final Optional<String> description;
  private final PList<PojoMember> members;

  private ObjectPojo(PojoName name, Optional<String> description, PList<PojoMember> members) {
    this.name = name;
    this.description = description;
    this.members = members;
  }

  public static ObjectPojo of(PojoName name, String description, PList<PojoMember> members) {
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
  public Pojo addObjectTypeDescription(PojoName objectTypeName, String description) {
    return mapMembers(member -> member.addObjectTypeDescription(objectTypeName, description));
  }

  @Override
  public Pojo inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType) {
    return mapMembers(
        member -> member.inlineObjectReference(referenceName, referenceDescription, referenceType));
  }

  private Pojo mapMembers(UnaryOperator<PojoMember> map) {
    return new ObjectPojo(name, description, members.map(map));
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
