package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.BooleanType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;

public class JavaTypes {
  private JavaTypes() {}

  public static JavaStringType stringType() {
    return JavaStringType.wrap(StringType.noFormat(), TypeMappings.empty());
  }

  public static JavaStringType nullableStringType() {
    return JavaStringType.wrap(
        StringType.noFormat().withNullability(NULLABLE), TypeMappings.empty());
  }

  public static JavaStringType date(Constraints constraints) {
    return JavaStringType.wrap(
        StringType.ofFormat(StringType.Format.DATE).withConstraints(constraints),
        TypeMappings.empty());
  }

  public static JavaStringType dateTime(Constraints constraints) {
    return JavaStringType.wrap(
        StringType.ofFormat(StringType.Format.DATE_TIME).withConstraints(constraints),
        TypeMappings.empty());
  }

  public static JavaArrayType nullableStringListType() {
    return JavaArrayType.wrap(
        ArrayType.ofItemType(StringType.noFormat(), NULLABLE), TypeMappings.empty());
  }

  public static JavaBooleanType booleanType() {
    return JavaBooleanType.wrap(BooleanType.create(NOT_NULLABLE), TypeMappings.empty());
  }

  public static JavaEnumType enumType() {
    return JavaEnumType.wrapForDiscriminator(
        EnumType.ofNameAndMembers(
            Name.ofString("Gender"), PList.of("male", "female", "divers", "other")));
  }

  public static JavaMapType mapType() {
    return JavaMapType.wrap(
        MapType.ofKeyAndValueType(StringType.noFormat(), StringType.noFormat()),
        TypeMappings.empty());
  }

  public static JavaIntegerType integerType() {
    return JavaIntegerType.wrap(IntegerType.formatInteger(), TypeMappings.empty());
  }

  public static JavaObjectType objectType() {
    return JavaObjectType.wrap(
        StandardObjectType.ofName(PojoName.ofName(Name.ofString("UserDto"))), TypeMappings.empty());
  }

  public static JavaObjectType nullableObjectType() {
    return JavaObjectType.wrap(
        StandardObjectType.ofName(PojoName.ofName(Name.ofString("UserDto")))
            .withNullability(NULLABLE),
        TypeMappings.empty());
  }

  /**
   * The type referencing the dedicated pojo which is created for a container as additional-property
   * value type, i.e. the type such a value type is actually mapped to.
   */
  public static JavaObjectType containerValuePojoType() {
    return JavaObjectType.wrap(
        StandardObjectType.ofName(PojoName.ofName(Name.ofString("UserPropertyDto"))),
        TypeMappings.empty());
  }

  public static JavaAnyType anyType() {
    return JavaAnyType.javaAnyType(NOT_NULLABLE);
  }

  public static JavaAnyType nullableAnyType() {
    return JavaAnyType.javaAnyType(NULLABLE);
  }
}
