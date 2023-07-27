package com.github.muehmar.gradle.openapi.generator.java.model.type;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;

public class JavaTypes {
  private JavaTypes() {}

  public static JavaStringType stringType() {
    return JavaStringType.wrap(StringType.noFormat(), TypeMappings.empty());
  }

  public static JavaArrayType stringListType() {
    return JavaArrayType.wrap(ArrayType.ofItemType(StringType.noFormat()), TypeMappings.empty());
  }

  public static JavaBooleanType booleanType() {
    return JavaBooleanType.wrap(TypeMappings.empty());
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
    return JavaObjectType.wrap(ObjectType.ofName(PojoName.ofName(Name.ofString("UserDto"))));
  }
}
