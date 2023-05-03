package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaIdentifier;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaArrayPojo implements JavaPojo {
  private final JavaPojoName name;
  private final String description;
  private final JavaType itemType;
  private final Constraints constraints;
  private final JavaPojoMember arrayPojoMember;

  private JavaArrayPojo(
      JavaPojoName name,
      String description,
      JavaType itemType,
      Constraints constraints,
      JavaPojoMember arrayPojoMember) {
    this.name = name;
    this.description = Optional.ofNullable(description).orElse("");
    this.itemType = itemType;
    this.constraints = constraints;
    this.arrayPojoMember = arrayPojoMember;
  }

  public static JavaArrayPojo wrap(ArrayPojo arrayPojo, TypeMappings typeMappings) {
    final JavaType itemType = JavaType.wrap(arrayPojo.getItemType(), typeMappings);
    final JavaPojoMember arrayPojoMember = createItemTypeMember(arrayPojo, typeMappings);
    return new JavaArrayPojo(
        JavaPojoName.wrap(arrayPojo.getName()),
        arrayPojo.getDescription(),
        itemType,
        arrayPojo.getConstraints(),
        arrayPojoMember);
  }

  private static JavaPojoMember createItemTypeMember(
      ArrayPojo arrayPojo, TypeMappings typeMappings) {
    final ArrayType arrayType =
        ArrayType.ofItemType(arrayPojo.getItemType()).withConstraints(arrayPojo.getConstraints());
    final JavaArrayType javaArrayType = JavaArrayType.wrap(arrayType, typeMappings);
    final Name name = Name.ofString("value");
    return JavaPojoMember.of(
        name,
        arrayPojo.getDescription(),
        javaArrayType,
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
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

  public JavaPojoMember getArrayPojoMember() {
    return arrayPojoMember;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo,
      Function<JavaComposedPojo, T> onComposedPojo,
      Function<JavaFreeFormPojo, T> onFreeFormPojo) {
    return onArrayPojo.apply(this);
  }

  public Constraints getConstraints() {
    return constraints;
  }
}
