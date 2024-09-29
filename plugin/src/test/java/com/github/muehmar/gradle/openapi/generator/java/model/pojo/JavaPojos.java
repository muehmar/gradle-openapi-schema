package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredColorEnum;
import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalNullableListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredNullableListWithNullableItems;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.EnumConstantName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfCompositions;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfCompositions;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaEnumType;
import com.github.muehmar.gradle.openapi.generator.model.composition.DiscriminatorType;
import com.github.muehmar.gradle.openapi.generator.model.composition.UntypedDiscriminator;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.SchemaName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.ClassTypeMappings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JavaPojos {
  private JavaPojos() {}

  public static JavaObjectPojo sampleObjectPojo1() {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoNames.fromNameAndSuffix("SampleObjectPojo1", "Dto"))
        .schemaName(SchemaName.ofString("SampleObjectPojo1"))
        .description("")
        .members(
            JavaPojoMembers.fromMembers(
                PList.of(
                    TestJavaPojoMembers.requiredString(),
                    TestJavaPojoMembers.requiredInteger(),
                    TestJavaPojoMembers.requiredDouble())))
        .type(PojoType.DEFAULT)
        .requiredAdditionalProperties(PList.empty())
        .additionalProperties(JavaAdditionalProperties.anyTypeAllowed())
        .constraints(Constraints.empty())
        .build();
  }

  public static JavaObjectPojo sampleObjectPojo2() {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoNames.fromNameAndSuffix("SampleObjectPojo2", "Dto"))
        .schemaName(SchemaName.ofString("SampleObjectPojo2"))
        .description("")
        .members(
            JavaPojoMembers.fromMembers(
                PList.of(
                    TestJavaPojoMembers.requiredString(),
                    TestJavaPojoMembers.requiredBirthdate(),
                    TestJavaPojoMembers.requiredEmail())))
        .type(PojoType.DEFAULT)
        .requiredAdditionalProperties(PList.empty())
        .additionalProperties(JavaAdditionalProperties.anyTypeAllowed())
        .constraints(Constraints.empty())
        .build();
  }

  public static JavaObjectPojo illegalIdentifierPojo() {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoNames.fromNameAndSuffix("Illegal?Identifier", "Dto"))
        .schemaName(SchemaName.ofString("Illegal?Identifier"))
        .description("")
        .members(
            JavaPojoMembers.fromMembers(
                PList.of(
                    TestJavaPojoMembers.keywordNameString(),
                    TestJavaPojoMembers.illegalCharacterString())))
        .type(PojoType.DEFAULT)
        .requiredAdditionalProperties(PList.empty())
        .additionalProperties(JavaAdditionalProperties.anyTypeAllowed())
        .constraints(Constraints.empty())
        .build();
  }

  public static JavaObjectPojo objectPojo(
      PList<JavaPojoMember> members, JavaAdditionalProperties additionalProperties) {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoNames.fromNameAndSuffix("ObjectPojo1", "Dto"))
        .schemaName(SchemaName.ofString("ObjectPojo1"))
        .description("")
        .members(JavaPojoMembers.fromMembers(members))
        .type(PojoType.DEFAULT)
        .requiredAdditionalProperties(PList.empty())
        .additionalProperties(additionalProperties)
        .constraints(Constraints.empty())
        .build();
  }

  public static JavaObjectPojo allOfPojo(JavaAllOfComposition javaAllOfComposition) {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoNames.fromNameAndSuffix("AllOfPojo1", "Dto"))
        .schemaName(SchemaName.ofString("AllOfPojo1"))
        .description("")
        .members(JavaPojoMembers.empty())
        .type(PojoType.DEFAULT)
        .requiredAdditionalProperties(PList.empty())
        .additionalProperties(JavaAdditionalProperties.anyTypeAllowed())
        .constraints(Constraints.empty())
        .andOptionals()
        .allOfComposition(javaAllOfComposition)
        .build();
  }

  public static JavaObjectPojo allOfPojo(NonEmptyList<JavaPojo> allOfPojos) {
    return allOfPojo(JavaAllOfComposition.fromPojos(allOfPojos));
  }

  public static JavaObjectPojo allOfPojo(JavaPojo allOfPojo, JavaPojo... allOfPojos) {
    return allOfPojo(NonEmptyList.single(allOfPojo).concat(PList.fromArray(allOfPojos)));
  }

  public static JavaObjectPojo oneOfPojo(JavaOneOfComposition javaOneOfComposition) {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoNames.fromNameAndSuffix("OneOfPojo1", "Dto"))
        .schemaName(SchemaName.ofString("OneOfPojo1"))
        .description("")
        .members(JavaPojoMembers.empty())
        .type(PojoType.DEFAULT)
        .requiredAdditionalProperties(PList.empty())
        .additionalProperties(JavaAdditionalProperties.anyTypeAllowed())
        .constraints(Constraints.empty())
        .andOptionals()
        .oneOfComposition(javaOneOfComposition)
        .build();
  }

  public static JavaObjectPojo oneOfPojo(NonEmptyList<JavaPojo> oneOfPojos) {
    return oneOfPojo(JavaOneOfComposition.fromPojos(oneOfPojos));
  }

  public static JavaObjectPojo oneOfPojo(JavaPojo oneOfPojo, JavaPojo... oneOfPojos) {
    return oneOfPojo(NonEmptyList.single(oneOfPojo).concat(PList.fromArray(oneOfPojos)));
  }

  public static JavaObjectPojo anyOfPojo(NonEmptyList<JavaPojo> anyOfPojos) {
    return anyOfPojo(JavaAnyOfComposition.fromPojos(anyOfPojos));
  }

  public static JavaObjectPojo anyOfPojo(JavaPojo anyOfPojo, JavaPojo... anyOfPojos) {
    return anyOfPojo(NonEmptyList.single(anyOfPojo).concat(PList.fromArray(anyOfPojos)));
  }

  public static JavaObjectPojo anyOfPojo(JavaAnyOfComposition anyOfComposition) {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoNames.fromNameAndSuffix("AnyOfPojo1", "Dto"))
        .schemaName(SchemaName.ofString("AnyOfPojo1"))
        .description("")
        .members(JavaPojoMembers.empty())
        .type(PojoType.DEFAULT)
        .requiredAdditionalProperties(PList.empty())
        .additionalProperties(JavaAdditionalProperties.anyTypeAllowed())
        .constraints(Constraints.empty())
        .andOptionals()
        .anyOfComposition(anyOfComposition)
        .build();
  }

  public static JavaObjectPojo objectPojo(PList<JavaPojoMember> members) {
    return objectPojo(members, JavaAdditionalProperties.anyTypeAllowed());
  }

  public static JavaObjectPojo objectPojo(JavaPojoMember... members) {
    return objectPojo(PList.of(members), JavaAdditionalProperties.anyTypeAllowed());
  }

  public static JavaObjectPojo simpleMapPojo(JavaAdditionalProperties additionalProperties) {
    return objectPojo().withAdditionalProperties(additionalProperties);
  }

  public static JavaObjectPojo allNecessityAndNullabilityVariants(Constraints constraints) {
    return (JavaObjectPojo)
        JavaPojo.wrap(allNecessityAndNullabilityVariantsPojo(constraints), TypeMappings.empty())
            .getDefaultPojo();
  }

  public static JavaObjectPojo allNecessityAndNullabilityVariants() {
    return allNecessityAndNullabilityVariants(Constraints.empty());
  }

  public static JavaObjectPojo allNecessityAndNullabilityVariantsTypeMapped() {
    return allNecessityAndNullabilityVariantsTypeMapped(
        TypeMappings.ofClassTypeMappings(ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION));
  }

  public static JavaObjectPojo allNecessityAndNullabilityVariantsFullyTypeMapped() {
    return allNecessityAndNullabilityVariantsTypeMapped(
        TypeMappings.ofClassTypeMappings(
            ClassTypeMappings.STRING_MAPPING_WITH_CONVERSION,
            ClassTypeMappings.LIST_MAPPING_WITH_CONVERSION));
  }

  public static JavaObjectPojo allNecessityAndNullabilityVariantsTypeMapped(
      TypeMappings typeMappings) {
    return (JavaObjectPojo)
        JavaPojo.wrap(allNecessityAndNullabilityVariantsPojo(Constraints.empty()), typeMappings)
            .getDefaultPojo();
  }

  private static ObjectPojo allNecessityAndNullabilityVariantsPojo(Constraints constraints) {
    return ObjectPojoBuilder.create()
        .name(componentName("NecessityAndNullability", "Dto"))
        .description("NecessityAndNullability")
        .nullability(NOT_NULLABLE)
        .members(
            PList.of(
                requiredString(),
                requiredNullableString(),
                optionalString(),
                optionalNullableString(),
                requiredListWithNullableItems(),
                requiredNullableListWithNullableItems(),
                optionalListWithNullableItems(),
                optionalNullableListWithNullableItems()))
        .requiredAdditionalProperties(PList.empty())
        .constraints(constraints)
        .additionalProperties(anyTypeAllowed())
        .build();
  }

  public static JavaArrayPojo arrayPojo(Constraints constraints) {
    final ArrayPojo arrayPojo =
        ArrayPojo.of(
            componentName("Posology", "Dto"),
            "Doses to be taken",
            NOT_NULLABLE,
            NumericType.formatDouble(),
            constraints);
    return JavaArrayPojo.wrap(arrayPojo, TypeMappings.empty());
  }

  public static JavaArrayPojo arrayPojo() {
    return arrayPojo(Constraints.empty());
  }

  public static JavaPojo enumPojo() {
    final EnumPojo enumPojo =
        EnumPojo.of(
            componentName("Gender", "Dto"),
            "Gender of person",
            PList.of("male", "female", "divers", "other"));
    return JavaEnumPojo.wrap(enumPojo);
  }

  public static JavaObjectPojo oneOfPojoWithEnumDiscriminator() {
    final Map<String, Name> mapping = new HashMap<>();
    mapping.put("yellow", Name.ofString("Yellow"));
    mapping.put("orange", Name.ofString("Orange"));
    final UntypedDiscriminator untypedDiscriminator =
        UntypedDiscriminator.fromPropertyName(requiredColorEnum().getName().getOriginalName())
            .withMapping(Optional.of(mapping));

    final JavaObjectPojo allOfPojo =
        objectPojo(requiredColorEnum()).withName(JavaPojoNames.fromNameAndSuffix("Base", "Dto"));
    final Optional<JavaAllOfComposition> allOfComposition =
        Optional.of(JavaAllOfComposition.fromPojos(NonEmptyList.of(allOfPojo)));
    final EnumType enumType =
        EnumType.ofNameAndMembers(
            requiredColorEnum().getName().getOriginalName(),
            ((JavaEnumType) requiredColorEnum().getJavaType())
                .getMembers()
                .map(EnumConstantName::getOriginalConstant));

    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(
                objectPojo(TestJavaPojoMembers.requiredString())
                    .withAllOfComposition(allOfComposition)
                    .withSchemaName(SchemaName.ofString("Yellow"))
                    .withName(JavaPojoNames.fromNameAndSuffix("Yellow", "Dto")),
                objectPojo(requiredBirthdate())
                    .withAllOfComposition(allOfComposition)
                    .withSchemaName(SchemaName.ofString("Orange"))
                    .withName(JavaPojoNames.fromNameAndSuffix("Orange", "Dto"))),
            untypedDiscriminator,
            DiscriminatorType.fromEnumType(enumType));

    return JavaPojos.oneOfPojo(javaOneOfComposition)
        .withName(JavaPojoNames.fromNameAndSuffix("OneOf", "Dto"));
  }

  public static JavaObjectPojo anyOfPojoWithDiscriminator() {
    final UntypedDiscriminator discriminator =
        UntypedDiscriminator.fromPropertyName(
            TestJavaPojoMembers.requiredString().getName().getOriginalName());
    final JavaAnyOfComposition javaAnyOfComposition =
        JavaAnyOfCompositions.fromPojosAndDiscriminator(
            NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()), discriminator);
    return JavaPojos.anyOfPojo(javaAnyOfComposition);
  }
}
