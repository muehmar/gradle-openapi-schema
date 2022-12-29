package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
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

  private JavaObjectPojo(PojoName name, String description, PList<JavaPojoMember> members) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.members = members;
  }

  public static JavaObjectPojo from(
      PojoName name, String description, PList<JavaPojoMember> members) {
    return new JavaObjectPojo(name, description, members);
  }

  public static JavaObjectPojo wrap(ObjectPojo objectPojo, TypeMappings typeMappings) {
    final PList<JavaPojoMember> members =
        objectPojo.getMembers().map(member -> JavaPojoMember.wrap(member, typeMappings));
    return new JavaObjectPojo(objectPojo.getName(), objectPojo.getDescription(), members);
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

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo,
      Function<JavaComposedPojo, T> onComposedPojo) {
    return onObjectPojo.apply(this);
  }
}
