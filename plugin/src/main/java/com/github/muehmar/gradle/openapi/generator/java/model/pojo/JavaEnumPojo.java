package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaEnumPojo implements JavaPojo {
  private final JavaPojoName name;
  private final String description;
  private final PList<EnumConstantName> members;

  private JavaEnumPojo(JavaPojoName name, String description, PList<EnumConstantName> members) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.members = members;
  }

  public static JavaEnumPojo of(
      PojoName name, String description, PList<EnumConstantName> members) {
    return new JavaEnumPojo(JavaPojoName.wrap(name), description, members);
  }

  public static JavaEnumPojo wrap(EnumPojo enumPojo) {
    return new JavaEnumPojo(
        JavaPojoName.wrap(enumPojo.getName()),
        enumPojo.getDescription(),
        enumPojo.getMembers().map(EnumConstantName::ofString));
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
    return PojoType.DEFAULT;
  }

  public PList<EnumConstantName> getMembers() {
    return members;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo,
      Function<JavaComposedPojo, T> onComposedPojo,
      Function<JavaFreeFormPojo, T> onFreeFormPojo) {
    return onEnumPojo.apply(this);
  }
}
