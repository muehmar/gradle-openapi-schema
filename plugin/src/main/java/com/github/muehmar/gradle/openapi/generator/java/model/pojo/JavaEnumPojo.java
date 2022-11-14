package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaEnumPojo implements JavaPojo {
  private final PojoName name;
  private final String description;
  private final PList<EnumConstantName> members;

  private JavaEnumPojo(PojoName name, String description, PList<EnumConstantName> members) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.members = members;
  }

  public static JavaEnumPojo of(
      PojoName name, String description, PList<EnumConstantName> members) {
    return new JavaEnumPojo(name, description, members);
  }

  public static JavaEnumPojo wrap(EnumPojo enumPojo) {
    return new JavaEnumPojo(
        enumPojo.getName(),
        enumPojo.getDescription(),
        enumPojo.getMembers().map(EnumConstantName::ofString));
  }

  @Override
  public PojoName getName() {
    return name;
  }

  @Override
  public String getDescription() {
    return description;
  }

  public PList<EnumConstantName> getMembers() {
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
