package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;

public class JavaPojoMembers {
  public JavaPojoMembers() {}

  public static JavaPojoMember requiredBirthdate() {
    return JavaPojoMember.of(
        Name.ofString("birthdate"),
        "Birthdate",
        JavaType.wrap(StringType.ofFormat(StringType.Format.DATE), TypeMappings.empty()),
        REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static JavaPojoMember requiredNullableBirthdate() {
    return JavaPojoMember.of(
        Name.ofString("birthdate"),
        "Birthdate",
        JavaType.wrap(StringType.ofFormat(StringType.Format.DATE), TypeMappings.empty()),
        REQUIRED,
        NULLABLE);
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
            NumericType.formatInteger()
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

  public static JavaPojoMember requiredStringList() {
    return stringList(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember stringList(Necessity necessity, Nullability nullability) {
    return JavaPojoMember.of(
        Name.ofString("stringListVal"),
        "stringListVal",
        JavaType.wrap(
            ArrayType.ofItemType(StringType.noFormat())
                .withConstraints(Constraints.ofSize(Size.of(1, 50))),
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
