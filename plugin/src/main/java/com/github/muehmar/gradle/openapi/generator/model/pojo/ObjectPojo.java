package com.github.muehmar.gradle.openapi.generator.model.pojo;

import static com.github.muehmar.gradle.openapi.util.Booleans.not;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import io.github.muehmar.pojobuilder.annotations.PojoBuilder;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@PojoBuilder
public class ObjectPojo implements Pojo {
  private final PojoName name;
  private final String description;
  private final PList<PojoMember> members;
  private final Constraints constraints;
  private final AdditionalProperties additionalProperties;

  ObjectPojo(
      PojoName name,
      String description,
      PList<PojoMember> members,
      Constraints constraints,
      AdditionalProperties additionalProperties) {
    this.name = name;
    this.description = description;
    this.members = members;
    this.constraints = constraints;
    this.additionalProperties = additionalProperties;
  }

  @Override
  public PojoName getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public PList<PojoMember> getMembers() {
    return members;
  }

  public Constraints getConstraints() {
    return constraints;
  }

  public AdditionalProperties getAdditionalProperties() {
    return additionalProperties;
  }

  public boolean allowsAdditionalProperties() {
    return additionalProperties.isAllowed();
  }

  @Override
  public Pojo addObjectTypeDescription(PojoName objectTypeName, String description) {
    return mapMembers(member -> member.addObjectTypeDescription(objectTypeName, description));
  }

  @Override
  public Pojo inlineObjectReference(
      PojoName referenceName, String referenceDescription, Type referenceType) {
    return mapMembers(
            member ->
                member.inlineObjectReference(referenceName, referenceDescription, referenceType))
        .mapAdditionalProperties(
            props -> props.inlineObjectReference(referenceName, referenceType));
  }

  private ObjectPojo mapMembers(UnaryOperator<PojoMember> map) {
    return new ObjectPojo(name, description, members.map(map), constraints, additionalProperties);
  }

  private ObjectPojo mapAdditionalProperties(UnaryOperator<AdditionalProperties> map) {
    return new ObjectPojo(name, description, members, constraints, map.apply(additionalProperties));
  }

  @Override
  public <T> T fold(
      Function<ObjectPojo, T> onObjectPojo,
      Function<ArrayPojo, T> onArrayType,
      Function<EnumPojo, T> onEnumPojo,
      Function<ComposedPojo, T> onComposedPojo,
      Function<MapPojo, T> onMapPojo) {
    return onObjectPojo.apply(this);
  }

  public boolean containsNoneDefaultPropertyScope() {
    return members.exists(member -> not(member.isDefaultScope()));
  }
}
