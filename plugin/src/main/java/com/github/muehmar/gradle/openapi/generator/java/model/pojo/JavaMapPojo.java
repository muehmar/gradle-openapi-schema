package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.EqualsGenerator;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeContentBuilder;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.HashCodeGenerator;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaMapType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.MapPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaMapPojo implements JavaPojo {
  private final JavaPojoName name;
  private final String description;
  private final JavaType valueType;
  private final Constraints constraints;

  private JavaMapPojo(
      JavaPojoName name, String description, JavaType valueType, Constraints constraints) {
    this.name = name;
    this.description = description;
    this.valueType = valueType;
    this.constraints = constraints;
  }

  public static JavaMapPojo wrap(MapPojo mapPojo, TypeMappings typeMappings) {
    return new JavaMapPojo(
        JavaPojoName.wrap(mapPojo.getName()),
        mapPojo.getDescription(),
        JavaType.wrap(mapPojo.getValueType(), typeMappings),
        mapPojo.getConstraints());
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

  public Constraints getConstraints() {
    return constraints;
  }

  public HashCodeGenerator.HashCodeContent getHashCodeContent() {
    return HashCodeContentBuilder.create().members(PList.single(getMember())).build();
  }

  public EqualsGenerator.EqualsContent getEqualsContent() {
    return EqualsContentBuilder.create()
        .className(getClassName())
        .members(PList.single(getMember()))
        .build();
  }

  public JavaPojoMember getMember() {
    return JavaPojoMember.of(
        Name.ofString("values"), "", getMemberType(), Necessity.REQUIRED, Nullability.NOT_NULLABLE);
  }

  private JavaType getMemberType() {
    final MapType mapType = MapType.ofKeyAndValueType(StringType.noFormat(), valueType.getType());
    return JavaMapType.wrap(mapType, TypeMappings.empty());
  }

  public JavaType getValueType() {
    return valueType;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo,
      Function<JavaComposedPojo, T> onComposedPojo,
      Function<JavaMapPojo, T> onFreeFormPojo) {
    return onFreeFormPojo.apply(this);
  }
}
