package com.github.muehmar.gradle.openapi.generator.java.model.member;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember.MemberType.OBJECT_MEMBER;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMemberBuilder.javaPojoMemberBuilder;
import static com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames.invoiceName;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.QualifiedClassName;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaStringType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMembers;
import com.github.muehmar.gradle.openapi.generator.model.Type;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.*;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.stream.Stream;
import org.junit.jupiter.params.provider.Arguments;

public class TestJavaPojoMembers {
  public TestJavaPojoMembers() {}

  public static JavaPojoMember byteArrayMember() {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("data"))
        .description("data")
        .javaType(
            JavaType.wrap(StringType.ofFormat(StringType.Format.BINARY), TypeMappings.empty()))
        .necessity(Necessity.REQUIRED)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember birthdate(
      Constraints constraints, Necessity necessity, Nullability nullability) {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("birthdate"))
        .description("Birthdate")
        .javaType(JavaTypes.date(constraints).withNullability(nullability))
        .necessity(necessity)
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
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("ref"))
        .description("ref")
        .javaType(
            JavaType.wrap(
                    StandardObjectType.ofName(pojoName("SomeObject", "Dto")), TypeMappings.empty())
                .withNullability(nullability))
        .necessity(necessity)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredEmail() {
    return email(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember email(Necessity necessity, Nullability nullability) {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("email"))
        .description("email")
        .javaType(
            JavaType.wrap(
                    StringType.ofFormat(StringType.Format.EMAIL)
                        .withConstraints(Constraints.ofEmail()),
                    TypeMappings.empty())
                .withNullability(nullability))
        .necessity(necessity)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredInteger() {
    return integer(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember integer(Necessity necessity, Nullability nullability) {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("intVal"))
        .description("intVal")
        .javaType(
            JavaType.wrap(
                    IntegerType.formatInteger()
                        .withConstraints(Constraints.ofMin(new Min(10)).withMax(new Max(50))),
                    TypeMappings.empty())
                .withNullability(nullability))
        .necessity(necessity)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredDouble() {
    return doubleMember(REQUIRED, NOT_NULLABLE);
  }

  public static JavaPojoMember doubleMember(Necessity necessity, Nullability nullability) {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("doubleVal"))
        .description("doubleVal")
        .javaType(
            JavaType.wrap(
                    NumericType.formatDouble()
                        .withConstraints(
                            Constraints.ofDecimalMin(new DecimalMin("12.5", true))
                                .withDecimalMax(new DecimalMax("50.1", false))),
                    TypeMappings.empty())
                .withNullability(nullability))
        .necessity(necessity)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredStringList() {
    return requiredStringList(TypeMappings.empty());
  }

  public static JavaPojoMember requiredStringList(TypeMappings typeMappings) {
    return list(
            StringType.noFormat()
                .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
            Necessity.REQUIRED,
            Nullability.NOT_NULLABLE,
            Constraints.ofSize(Size.ofMin(1)),
            typeMappings)
        .withName(JavaName.fromString("requiredStringList"));
  }

  public static JavaPojoMember requiredNullableStringList() {
    return requiredNullableStringList(TypeMappings.empty());
  }

  public static JavaPojoMember requiredNullableStringList(TypeMappings typeMappings) {
    return list(
            StringType.noFormat()
                .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
            Necessity.REQUIRED,
            NULLABLE,
            Constraints.ofSize(Size.ofMin(1)),
            typeMappings)
        .withName(JavaName.fromString("requiredNullableStringList"));
  }

  public static JavaPojoMember optionalStringList() {
    return optionalStringList(TypeMappings.empty());
  }

  public static JavaPojoMember optionalStringList(TypeMappings typeMappings) {
    return list(
            StringType.noFormat()
                .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
            OPTIONAL,
            NOT_NULLABLE,
            Constraints.ofSize(Size.ofMin(1)),
            typeMappings)
        .withName(JavaName.fromString("optionalStringList"));
  }

  public static JavaPojoMember optionalNullableStringList() {
    return optionalNullableStringList(TypeMappings.empty());
  }

  public static JavaPojoMember optionalNullableStringList(TypeMappings typeMappings) {
    return list(
            StringType.noFormat()
                .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
            OPTIONAL,
            NULLABLE,
            Constraints.ofSize(Size.ofMin(1)),
            typeMappings)
        .withName(JavaName.fromString("optionalNullableStringList"));
  }

  public static JavaPojoMember list(Type itemType, Necessity necessity, Nullability nullability) {
    return list(itemType, necessity, nullability, Constraints.empty(), TypeMappings.empty());
  }

  public static JavaPojoMember list(
      Type itemType, Necessity necessity, Nullability nullability, Constraints constraints) {
    return list(itemType, necessity, nullability, constraints, TypeMappings.empty());
  }

  public static JavaPojoMember list(
      Type itemType,
      Necessity necessity,
      Nullability nullability,
      Constraints constraints,
      TypeMappings typeMappings) {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("listVal"))
        .description("List")
        .javaType(
            JavaType.wrap(
                    ArrayType.ofItemType(itemType, NOT_NULLABLE).withConstraints(constraints),
                    typeMappings)
                .withNullability(nullability))
        .necessity(necessity)
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
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("mapVal"))
        .description("Map")
        .javaType(
            JavaType.wrap(
                    MapType.ofKeyAndValueType(keyType, valueType).withConstraints(constraints),
                    TypeMappings.empty())
                .withNullability(nullability))
        .necessity(necessity)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredMap() {
    return requiredMap(TypeMappings.empty());
  }

  public static JavaPojoMember requiredMap(TypeMappings typeMappings) {
    return JavaPojoMember.wrap(PojoMembers.requiredMap(), invoiceName(), typeMappings);
  }

  public static JavaPojoMember requiredNullableMap() {
    return requiredNullableMap(TypeMappings.empty());
  }

  public static JavaPojoMember requiredNullableMap(TypeMappings typeMappings) {
    return JavaPojoMember.wrap(PojoMembers.requiredNullableMap(), invoiceName(), typeMappings);
  }

  public static JavaPojoMember optionalMap() {
    return optionalMap(TypeMappings.empty());
  }

  public static JavaPojoMember optionalMap(TypeMappings typeMappings) {
    return JavaPojoMember.wrap(PojoMembers.optionalMap(), invoiceName(), typeMappings);
  }

  public static JavaPojoMember optionalNullableMap() {
    return optionalNullableMap(TypeMappings.empty());
  }

  public static JavaPojoMember optionalNullableMap(TypeMappings typeMappings) {
    return JavaPojoMember.wrap(PojoMembers.optionalNullableMap(), invoiceName(), typeMappings);
  }

  public static JavaPojoMember requiredString() {
    return requiredString(TypeMappings.empty());
  }

  public static JavaPojoMember requiredString(TypeMappings typeMappings) {
    return string(REQUIRED, NOT_NULLABLE, typeMappings);
  }

  public static JavaPojoMember string(
      Necessity necessity, Nullability nullability, TypeMappings typeMappings) {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("stringVal"))
        .description("stringVal")
        .javaType(
            JavaType.wrap(
                    StringType.noFormat()
                        .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
                    typeMappings)
                .withNullability(nullability))
        .necessity(necessity)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredNullableString() {
    return requiredNullableString(TypeMappings.empty());
  }

  public static JavaPojoMember requiredNullableString(TypeMappings typeMappings) {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("requiredNullableStringVal"))
        .description("RequiredNullableStringVal")
        .javaType(
            JavaType.wrap(
                    StringType.noFormat()
                        .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
                    typeMappings)
                .withNullability(NULLABLE))
        .necessity(REQUIRED)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember optionalString() {
    return optionalString(TypeMappings.empty());
  }

  public static JavaPojoMember optionalString(TypeMappings typeMappings) {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("optionalStringVal"))
        .description("OptionalStringVal")
        .javaType(
            JavaType.wrap(
                StringType.noFormat()
                    .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
                typeMappings))
        .necessity(OPTIONAL)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember optionalNullableString() {
    return optionalNullableString(TypeMappings.empty());
  }

  public static JavaPojoMember optionalNullableString(TypeMappings typeMappings) {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("optionalNullableStringVal"))
        .description("OptionalNullableStringVal")
        .javaType(
            JavaType.wrap(
                    StringType.noFormat()
                        .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
                    typeMappings)
                .withNullability(NULLABLE))
        .necessity(OPTIONAL)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredColorEnum() {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("color"))
        .description("Color")
        .javaType(
            JavaType.wrap(
                EnumType.ofNameAndMembers(
                    Name.ofString("Color"), PList.of("yellow", "orange", "red")),
                TypeMappings.empty()))
        .necessity(REQUIRED)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredDirectionEnum() {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("direction"))
        .description("Direction")
        .javaType(
            JavaType.wrap(
                EnumType.ofNameAndMembers(
                    Name.ofString("Direction"), PList.of("north", "east", "south", "west")),
                TypeMappings.empty()))
        .necessity(REQUIRED)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember requiredListWithNullableItems(TypeMappings typeMappings) {
    return JavaPojoMember.wrap(
        PojoMembers.requiredListWithNullableItems(), invoiceName(), typeMappings);
  }

  public static JavaPojoMember requiredListWithNullableItems() {
    return requiredListWithNullableItems(TypeMappings.empty());
  }

  public static JavaPojoMember requiredNullableListWithNullableItems(TypeMappings typeMappings) {
    return JavaPojoMember.wrap(
        PojoMembers.requiredNullableListWithNullableItems(), invoiceName(), typeMappings);
  }

  public static JavaPojoMember requiredNullableListWithNullableItems() {
    return requiredNullableListWithNullableItems(TypeMappings.empty());
  }

  public static JavaPojoMember optionalListWithNullableItems(TypeMappings typeMappings) {
    return JavaPojoMember.wrap(
        PojoMembers.optionalListWithNullableItems(), invoiceName(), typeMappings);
  }

  public static JavaPojoMember optionalListWithNullableItems() {
    return optionalListWithNullableItems(TypeMappings.empty());
  }

  public static JavaPojoMember optionalNullableListWithNullableItems(TypeMappings typeMappings) {
    return JavaPojoMember.wrap(
        PojoMembers.optionalNullableListWithNullableItems(), invoiceName(), typeMappings);
  }

  public static JavaPojoMember optionalNullableListWithNullableItems() {
    return optionalNullableListWithNullableItems(TypeMappings.empty());
  }

  public static JavaPojoMember keywordNameString() {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("switch"))
        .description("Switch")
        .javaType(JavaStringType.noFormat().withNullability(NULLABLE))
        .necessity(OPTIONAL)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember illegalCharacterString() {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("point."))
        .description("Point")
        .javaType(JavaStringType.noFormat().withNullability(NULLABLE))
        .necessity(OPTIONAL)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static JavaPojoMember object(QualifiedClassName objectClassName) {
    return object(JavaObjectType.fromClassName(objectClassName));
  }

  public static JavaPojoMember object(ObjectType objectType) {
    return object(JavaObjectType.wrap(objectType));
  }

  private static JavaPojoMember object(JavaObjectType objectType) {
    return javaPojoMemberBuilder()
        .pojoName(invoiceName())
        .name(JavaName.fromString("object"))
        .description("Object")
        .javaType(objectType)
        .necessity(REQUIRED)
        .type(OBJECT_MEMBER)
        .build();
  }

  public static PList<JavaPojoMember> allNecessityAndNullabilityVariants() {
    return PList.of(
        requiredString(),
        requiredNullableString(),
        optionalString(),
        optionalNullableString(),
        requiredListWithNullableItems(),
        requiredNullableListWithNullableItems(),
        optionalListWithNullableItems(),
        optionalNullableListWithNullableItems());
  }

  public static PList<JavaPojoMember> allNecessityAndNullabilityVariants(
      TypeMappings typeMappings) {
    return PList.of(
        requiredString(typeMappings),
        requiredNullableString(typeMappings),
        optionalString(typeMappings),
        optionalNullableString(typeMappings),
        requiredListWithNullableItems(typeMappings),
        requiredNullableListWithNullableItems(typeMappings),
        optionalListWithNullableItems(typeMappings),
        optionalNullableListWithNullableItems(typeMappings));
  }

  public static Stream<Arguments> allNecessityAndNullabilityVariantsTestSource() {
    return allNecessityAndNullabilityVariants().map(Arguments::of).toStream();
  }
}
