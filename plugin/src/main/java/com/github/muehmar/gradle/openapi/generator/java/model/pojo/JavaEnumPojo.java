package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaEnumPojo implements JavaPojo {
  private final PojoName name;
  private final String description;
  private final PList<String> members;

  private JavaEnumPojo(PojoName name, String description, PList<String> members) {
    this.name = name;
    this.description = description;
    this.members = members;
  }

  public static JavaEnumPojo wrap(EnumPojo enumPojo) {
    return new JavaEnumPojo(enumPojo.getName(), enumPojo.getDescription(), enumPojo.getMembers());
  }

  @Override
  public PojoName getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public PList<String> getMembers() {
    return members;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo) {
    return onEnumPojo.apply(this);
  }
}
