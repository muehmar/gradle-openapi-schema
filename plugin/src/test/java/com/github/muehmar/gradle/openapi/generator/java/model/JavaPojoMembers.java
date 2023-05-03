package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;

public class JavaPojoMembers {
  public JavaPojoMembers() {}

  public static JavaPojoMember byteArrayMember() {
    return JavaPojoMember.of(
        Name.ofString("data"),
        "data",
        JavaType.wrap(StringType.ofFormat(StringType.Format.BINARY), TypeMappings.empty()),
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static JavaPojoMember birthdate(
      Constraints constraints, Necessity necessity, Nullability nullability) {
    return JavaPojoMember.of(
        Name.ofString("birthdate"),
        "Birthdate",
        JavaType.wrap(
            StringType.ofFormat(StringType.Format.DATE).withConstraints(constraints),
            TypeMappings.empty()),
        necessity,
        nullability);
  }

  public static JavaPojoMember birthdate(Necessity necessity, Nullability nullability) {
    return birthdate(Constraints.empty(), necessity, nullability);
  }

  public static JavaPojoMember requiredBirthdate() {
    return birthdate(REQUIRED, Nullability.NOT_NULLABLE);
  }

  public static JavaPojoMember requiredNullableBirthdate() {
    return birthdate(REQUIRED, NULLABLE);
  }

  public static JavaPojoMember requiredReference() {
    return reference(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember reference(Necessity necessity, Nullability nullability) {
    return JavaPojoMember.of(
        Name.ofString("ref"),
        "ref",
        JavaType.wrap(
            ObjectType.ofName(PojoName.ofNameAndSuffix(Name.ofString("SomeObject"), "Dto")),
            TypeMappings.empty()),
        necessity,
        nullability);
  }

  public static JavaPojoMember requiredEmail() {
    return email(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember email(Necessity necessity, Nullability nullability) {
    return JavaPojoMember.of(
        Name.ofString("email"),
        "email",
        JavaType.wrap(
            StringType.ofFormat(StringType.Format.EMAIL).withConstraints(Constraints.ofEmail()),
            TypeMappings.empty()),
        necessity,
        nullability);
  }

  public static JavaPojoMember requiredInteger() {
    return integer(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember integer(Necessity necessity, Nullability nullability) {
    return JavaPojoMember.of(
        Name.ofString("intVal"),
        "intVal",
        JavaType.wrap(
            IntegerType.formatInteger()
                .withConstraints(Constraints.ofMin(new Min(10)).withMax(new Max(50))),
            TypeMappings.empty()),
        necessity,
        nullability);
  }

  public static JavaPojoMember requiredDouble() {
    return doubleMember(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember doubleMember(Necessity necessity, Nullability nullability) {
    return JavaPojoMember.of(
        Name.ofString("doubleVal"),
        "doubleVal",
        JavaType.wrap(
            NumericType.formatDouble()
                .withConstraints(
                    Constraints.ofDecimalMin(new DecimalMin("12.5", true))
                        .withDecimalMax(new DecimalMax("50.1", false))),
            TypeMappings.empty()),
        necessity,
        nullability);
  }

  public static JavaPojoMember list(
      Type itemType, Constraints constraints, Necessity necessity, Nullability nullability) {
    return JavaPojoMember.of(
        Name.ofString("listVal"),
        "List",
        JavaType.wrap(
            ArrayType.ofItemType(itemType).withConstraints(constraints), TypeMappings.empty()),
        necessity,
        nullability);
  }

  public static JavaPojoMember map(
      Type keyType, Type valueType, Necessity necessity, Nullability nullability) {
    return map(keyType, valueType, necessity, nullability, Constraints.empty());
  }

  public static JavaPojoMember map(
      Type keyType,
      Type valueType,
      Necessity necessity,
      Nullability nullability,
      Constraints constraints) {
    return JavaPojoMember.of(
        Name.ofString("mapVal"),
        "Map",
        JavaType.wrap(
            MapType.ofKeyAndValueType(keyType, valueType).withConstraints(constraints),
            TypeMappings.empty()),
        necessity,
        nullability);
  }

  public static JavaPojoMember requiredString() {
    return string(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember string(Necessity necessity, Nullability nullability) {
    return JavaPojoMember.of(
        Name.ofString("stringVal"),
        "stringVal",
        JavaType.wrap(
            StringType.noFormat()
                .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
            TypeMappings.empty()),
        necessity,
        nullability);
  }

  public static JavaPojoMember requiredNullableString() {
    return JavaPojoMember.of(
        Name.ofString("requiredNullableStringVal"),
        "RequiredNullableStringVal",
        JavaType.wrap(
            StringType.noFormat()
                .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
            TypeMappings.empty()),
        REQUIRED,
        NULLABLE);
  }

  public static JavaPojoMember optionalString() {
    return JavaPojoMember.of(
        Name.ofString("optionalStringVal"),
        "OptionalStringVal",
        JavaType.wrap(
            StringType.noFormat()
                .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
            TypeMappings.empty()),
        OPTIONAL,
        NOT_NULLABLE);
  }

  public static JavaPojoMember optionalNullableString() {
    return JavaPojoMember.of(
        Name.ofString("optionalNullableStringVal"),
        "OptionalNullableStringVal",
        JavaType.wrap(
            StringType.noFormat()
                .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
            TypeMappings.empty()),
        OPTIONAL,
        NULLABLE);
  }
}
