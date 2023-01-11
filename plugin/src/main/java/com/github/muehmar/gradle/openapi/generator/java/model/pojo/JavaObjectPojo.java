package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
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
  private final PojoName name;
  private final String description;
  private final PList<JavaPojoMember> members;
  private final Constraints constraints;

  private JavaObjectPojo(
      PojoName name, String description, PList<JavaPojoMember> members, Constraints constraints) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.members = members;
    this.constraints = constraints;
  }

  public static JavaObjectPojo from(
      PojoName name, String description, PList<JavaPojoMember> members, Constraints constraints) {
    return new JavaObjectPojo(name, description, members, constraints);
  }

  public static JavaObjectPojo wrap(ObjectPojo objectPojo, TypeMappings typeMappings) {
    final PList<JavaPojoMember> members =
        objectPojo.getMembers().map(member -> JavaPojoMember.wrap(member, typeMappings));
    return new JavaObjectPojo(
        objectPojo.getName(), objectPojo.getDescription(), members, objectPojo.getConstraints());
  }

  @Override
  public PojoName getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
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
      Function<JavaFreeFormPojo, T> onFreeFormPojo) {
    return onObjectPojo.apply(this);
  }
}
