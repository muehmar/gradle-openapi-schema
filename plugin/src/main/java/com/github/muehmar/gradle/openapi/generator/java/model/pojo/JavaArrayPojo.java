package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaArrayType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.function.Function;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class JavaArrayPojo implements JavaPojo {
  private final PojoName name;
  private final String description;
  private final JavaType itemType;
  private final JavaPojoMember arrayPojoMember;

  private JavaArrayPojo(
      PojoName name, String description, JavaType itemType, JavaPojoMember arrayPojoMember) {
    this.name = name;
    this.description = description;
    this.itemType = itemType;
    this.arrayPojoMember = arrayPojoMember;
  }

  public static JavaArrayPojo wrap(ArrayPojo arrayPojo, TypeMappings typeMappings) {
    final JavaType itemType = JavaType.wrap(arrayPojo.getItemType(), typeMappings);
    final JavaPojoMember arrayPojoMember = createItemTypeMember(arrayPojo, typeMappings);
    return new JavaArrayPojo(
        arrayPojo.getName(), arrayPojo.getDescription(), itemType, arrayPojoMember);
  }

  private static JavaPojoMember createItemTypeMember(
      ArrayPojo arrayPojo, TypeMappings typeMappings) {
    final JavaArrayType arrayType =
        JavaArrayType.wrap(ArrayType.ofItemType(arrayPojo.getItemType()), typeMappings);
    final Name name = Name.ofString("value");
    return JavaPojoMember.of(
        name, arrayPojo.getDescription(), arrayType, Necessity.REQUIRED, Nullability.NOT_NULLABLE);
  }

  @Override
  public PojoName getName() {
    return name;
  }

  public String getDescription() {
    return description;
  }

  public JavaPojoMember getArrayPojoMember() {
    return arrayPojoMember;
  }

  @Override
  public <T> T fold(
      Function<JavaArrayPojo, T> onArrayPojo,
      Function<JavaEnumPojo, T> onEnumPojo,
      Function<JavaObjectPojo, T> onObjectPojo) {
    return onArrayPojo.apply(this);
  }
}