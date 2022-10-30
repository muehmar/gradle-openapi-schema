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

  public static final JavaArrayType STRING_LIST =
      JavaArrayType.wrap(ArrayType.ofItemType(StringType.noFormat()), TypeMappings.empty());
  public static final JavaBooleanType BOOLEAN = JavaBooleanType.wrap(TypeMappings.empty());
  public static final JavaEnumType ENUM =
      JavaEnumType.wrap(
          EnumType.ofNameAndMembers(
              Name.ofString("Gender"), PList.of("male", "female", "divers", "other")));
  public static final JavaMapType MAP =
      JavaMapType.wrap(
          MapType.ofKeyAndValueType(StringType.noFormat(), StringType.noFormat()),
          TypeMappings.empty());
  public static final JavaNoType NO_TYPE = JavaNoType.create();
  public static final JavaIntegerType INTEGER =
      JavaIntegerType.wrap(IntegerType.formatInteger(), TypeMappings.empty());
  public static final JavaObjectType OBJECT =
      JavaObjectType.wrap(ObjectType.ofName(PojoName.ofName(Name.ofString("UserDto"))));
  public static final JavaStringType STRING =
      JavaStringType.wrap(StringType.noFormat(), TypeMappings.empty());
}
