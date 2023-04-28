package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.*;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaObjectPojo implements JavaPojo {
  private final JavaPojoName name;
  private final String description;
  private final PList<JavaPojoMember> members;
  private final PojoType type;
  private final Constraints constraints;

  private JavaObjectPojo(
      JavaPojoName name,
      String description,
      PList<JavaPojoMember> members,
      PojoType type,
      Constraints constraints) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.members = members;
    this.type = type;
    this.constraints = constraints;
  }

  public static JavaObjectPojo from(
      JavaPojoName name,
      String description,
      PList<JavaPojoMember> members,
      PojoType type,
      Constraints constraints) {
    return new JavaObjectPojo(name, description, members, type, constraints);
  }

  public static JavaObjectPojo from(
      PojoName name,
      String description,
      PList<JavaPojoMember> members,
      PojoType type,
      Constraints constraints) {
    return from(JavaPojoName.wrap(name), description, members, type, constraints);
  }

  public static NonEmptyList<JavaObjectPojo> wrap(
      ObjectPojo objectPojo, TypeMappings typeMappings) {
    if (objectPojo.containsNoneDefaultPropertyScope()) {
      return NonEmptyList.of(
          createForType(objectPojo, typeMappings, PojoType.DEFAULT),
          createForType(objectPojo, typeMappings, PojoType.REQUEST),
          createForType(objectPojo, typeMappings, PojoType.RESPONSE));
    } else {
      return NonEmptyList.single(createForType(objectPojo, typeMappings, PojoType.DEFAULT));
    }
  }

  private static JavaObjectPojo createForType(
      ObjectPojo objectPojo, TypeMappings typeMappings, PojoType type) {
    final PList<JavaPojoMember> members =
        objectPojo
            .getMembers()
            .filter(member -> type.includesPropertyScope(member.getPropertyScope()))
            .map(member -> JavaPojoMember.wrap(member, typeMappings));
    return new JavaObjectPojo(
        JavaPojoName.wrap(type.mapName(objectPojo.getName())),
        objectPojo.getDescription(),
        members,
        type,
        objectPojo.getConstraints());
  }

  @Override
  public JavaName getSchemaName() {
    return JavaName.fromName(name.getSchemaName());
  }

  @Override
  public JavaIdentifier getClassName() {
    return name.asJavaName().asIdentifier();
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public PojoType getType() {
    return type;
  }

  public PList<JavaPojoMember> getMembers() {
    return members;
  }

  public Constraints getConstraints() {
    return constraints;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo,
      Function<JavaComposedPojo, T> onComposedPojo,
      Function<JavaMapPojo, T> onFreeFormPojo) {
    return onObjectPojo.apply(this);
  }
}
