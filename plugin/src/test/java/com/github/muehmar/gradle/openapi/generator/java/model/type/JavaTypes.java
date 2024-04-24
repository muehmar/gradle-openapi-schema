package com.github.muehmar.gradle.openapi.generator.java.model.type;

import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

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

  public static JavaStringType date(Constraints constraints) {
    return JavaStringType.wrap(
        StringType.ofFormat(StringType.Format.DATE).withConstraints(constraints),
        TypeMappings.empty());
  }

  public static JavaArrayType stringListType() {
    return JavaArrayType.wrap(
        ArrayType.ofItemType(StringType.noFormat(), NOT_NULLABLE), TypeMappings.empty());
  }

  public static JavaBooleanType booleanType() {
    return JavaBooleanType.wrap(BooleanType.create(NOT_NULLABLE), TypeMappings.empty());
  }

  public static JavaEnumType enumType() {
    return JavaEnumType.wrap(
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
        StandardObjectType.ofName(PojoName.ofName(Name.ofString("UserDto"))));
  }

  public static JavaAnyType anyType() {
    return JavaAnyType.javaAnyType(NOT_NULLABLE);
  }
}
