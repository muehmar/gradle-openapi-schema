package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo.EnumGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.model.name.SchemaName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaEnumPojo implements JavaPojo {
  private final JavaPojoName name;
  private final SchemaName schemaName;
  private final String description;
  private final PList<EnumConstantName> members;

  private JavaEnumPojo(
      JavaPojoName name,
      SchemaName schemaName,
      String description,
      PList<EnumConstantName> members) {
    this.name = name;
    this.schemaName = schemaName;
    this.description = Optional.ofNullable(description).orElse("");
    this.members = members;
  }

  public static JavaEnumPojo wrap(EnumPojo enumPojo) {
    return new JavaEnumPojo(
        JavaPojoName.wrap(enumPojo.getName().getPojoName()),
        enumPojo.getName().getSchemaName(),
        enumPojo.getDescription(),
        enumPojo.getMembers().map(EnumConstantName::ofString));
  }

  @Override
  public JavaName getSchemaName() {
    return JavaName.fromName(schemaName.asName());
  }

  @Override
  public JavaIdentifier getClassName() {
    return name.asIdentifier();
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

  public EnumGenerator.EnumContent asEnumContent() {
    return EnumContentBuilder.create()
        .className(getClassName())
        .description(description)
        .members(members)
        .build();
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo) {
    return onEnumPojo.apply(this);
  }
}
