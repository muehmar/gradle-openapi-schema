package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaObjectPojo implements JavaPojo {
  private final ObjectPojo objectPojo;
  private final PList<JavaPojoMember> members;

  private JavaObjectPojo(ObjectPojo objectPojo, PList<JavaPojoMember> members) {
    this.objectPojo = objectPojo;
    this.members = members;
  }

  public static JavaObjectPojo wrap(ObjectPojo objectPojo, TypeMappings typeMappings) {
    final PList<JavaPojoMember> members =
        objectPojo.getMembers().map(member -> JavaPojoMember.wrap(member, typeMappings));
    return new JavaObjectPojo(objectPojo, members);
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo) {
    return onObjectPojo.apply(this);
  }
}
