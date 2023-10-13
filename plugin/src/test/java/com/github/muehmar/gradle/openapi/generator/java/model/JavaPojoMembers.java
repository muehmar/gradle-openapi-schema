package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember.MemberType.OBJECT_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.*;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;

public class JavaPojoMembers {
  public JavaPojoMembers() {}

  public static JavaPojoMember byteArrayMember() {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("data")))
        .description("data")
        .javaType(
            JavaType.wrap(StringType.ofFormat(StringType.Format.BINARY), TypeMappings.empty()))
        .necessity(Necessity.REQUIRED)
        .nullability(Nullability.NOT_NULLABLE)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember birthdate(
      Constraints constraints, Necessity necessity, Nullability nullability) {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("birthdate")))
        .description("Birthdate")
        .javaType(
            JavaType.wrap(
                StringType.ofFormat(StringType.Format.DATE).withConstraints(constraints),
                TypeMappings.empty()))
        .necessity(necessity)
        .nullability(nullability)
        .type(OBJECT_MEMBER)
        .build();
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

  public static JavaPojoMember optionalBirthdate() {
    return birthdate(OPTIONAL, NOT_NULLABLE);
  }

  public static JavaPojoMember optionalNullableBirthdate() {
    return birthdate(OPTIONAL, NULLABLE);
  }

  public static JavaPojoMember requiredReference() {
    return reference(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember reference(Necessity necessity, Nullability nullability) {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("ref")))
        .description("ref")
        .javaType(
            JavaType.wrap(ObjectType.ofName(pojoName("SomeObject", "Dto")), TypeMappings.empty()))
        .necessity(necessity)
        .nullability(nullability)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredEmail() {
    return email(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember email(Necessity necessity, Nullability nullability) {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("email")))
        .description("email")
        .javaType(
            JavaType.wrap(
                StringType.ofFormat(StringType.Format.EMAIL).withConstraints(Constraints.ofEmail()),
                TypeMappings.empty()))
        .necessity(necessity)
        .nullability(nullability)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredInteger() {
    return integer(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember integer(Necessity necessity, Nullability nullability) {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("intVal")))
        .description("intVal")
        .javaType(
            JavaType.wrap(
                IntegerType.formatInteger()
                    .withConstraints(Constraints.ofMin(new Min(10)).withMax(new Max(50))),
                TypeMappings.empty()))
        .necessity(necessity)
        .nullability(nullability)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredDouble() {
    return doubleMember(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember doubleMember(Necessity necessity, Nullability nullability) {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("doubleVal")))
        .description("doubleVal")
        .javaType(
            JavaType.wrap(
                NumericType.formatDouble()
                    .withConstraints(
                        Constraints.ofDecimalMin(new DecimalMin("12.5", true))
                            .withDecimalMax(new DecimalMax("50.1", false))),
                TypeMappings.empty()))
        .necessity(necessity)
        .nullability(nullability)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember list(
      Type itemType, Necessity necessity, Nullability nullability, Constraints constraints) {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("listVal")))
        .description("List")
        .javaType(
            JavaType.wrap(
                ArrayType.ofItemType(itemType).withConstraints(constraints), TypeMappings.empty()))
        .necessity(necessity)
        .nullability(nullability)
        .type(OBJECT_MEMBER)
        .build();
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
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("mapVal")))
        .description("Map")
        .javaType(
            JavaType.wrap(
                MapType.ofKeyAndValueType(keyType, valueType).withConstraints(constraints),
                TypeMappings.empty()))
        .necessity(necessity)
        .nullability(nullability)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredString() {
    return string(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember string(Necessity necessity, Nullability nullability) {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("stringVal")))
        .description("stringVal")
        .javaType(
            JavaType.wrap(
                StringType.noFormat()
                    .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
                TypeMappings.empty()))
        .necessity(necessity)
        .nullability(nullability)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredNullableString() {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("requiredNullableStringVal")))
        .description("RequiredNullableStringVal")
        .javaType(
            JavaType.wrap(
                StringType.noFormat()
                    .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
                TypeMappings.empty()))
        .necessity(REQUIRED)
        .nullability(NULLABLE)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember optionalString() {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("optionalStringVal")))
        .description("OptionalStringVal")
        .javaType(
            JavaType.wrap(
                StringType.noFormat()
                    .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
                TypeMappings.empty()))
        .necessity(OPTIONAL)
        .nullability(NOT_NULLABLE)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember optionalNullableString() {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("optionalNullableStringVal")))
        .description("OptionalNullableStringVal")
        .javaType(
            JavaType.wrap(
                StringType.noFormat()
                    .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
                TypeMappings.empty()))
        .necessity(OPTIONAL)
        .nullability(NULLABLE)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredColorEnum() {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("color")))
        .description("Color")
        .javaType(
            JavaType.wrap(
                EnumType.ofNameAndMembers(
                    Name.ofString("Color"), PList.of("yellow", "orange", "red")),
                TypeMappings.empty()))
        .necessity(REQUIRED)
        .nullability(NOT_NULLABLE)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredDirectionEnum() {
    return JavaPojoMemberBuilder.create()
        .name(JavaMemberName.wrap(Name.ofString("direction")))
        .description("Direction")
        .javaType(
            JavaType.wrap(
                EnumType.ofNameAndMembers(
                    Name.ofString("Direction"), PList.of("north", "east", "south", "west")),
                TypeMappings.empty()))
        .necessity(REQUIRED)
        .nullability(NOT_NULLABLE)
        .type(OBJECT_MEMBER)
        .build();
  }
}
