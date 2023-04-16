package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

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
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.FreeFormPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.NoType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaFreeFormPojo implements JavaPojo {
  private final JavaPojoName name;
  private final String description;
  private final Constraints constraints;
  private static final Type VALUE_TYPE = NoType.create();

  private JavaFreeFormPojo(JavaPojoName name, String description, Constraints constraints) {
    this.name = name;
    this.description = description;
    this.constraints = constraints;
  }

  public static JavaFreeFormPojo wrap(FreeFormPojo freeFormPojo) {
    return new JavaFreeFormPojo(
        JavaPojoName.wrap(freeFormPojo.getName()),
        freeFormPojo.getDescription(),
        freeFormPojo.getConstraints());
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

  public JavaPojoMember getMember() {
    return JavaPojoMember.of(
        Name.ofString("values"), "", getMemberType(), Necessity.REQUIRED, Nullability.NOT_NULLABLE);
  }

  public JavaType getMemberType() {
    return JavaMapType.wrap(
        MapType.ofKeyAndValueType(StringType.noFormat(), VALUE_TYPE), TypeMappings.empty());
  }

  public JavaType getValueType() {
    return JavaType.wrap(VALUE_TYPE, TypeMappings.empty());
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo,
      Function<JavaComposedPojo, T> onComposedPojo,
      Function<JavaFreeFormPojo, T> onFreeFormPojo) {
    return onFreeFormPojo.apply(this);
  }
}
