package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.name.SchemaName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;

public class JavaPojos {
  private JavaPojos() {}

  public static JavaObjectPojo sampleObjectPojo1() {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoNames.fromNameAndSuffix("SampleObjectPojo1", "Dto"))
        .schemaName(SchemaName.ofString("SampleObjectPojo1"))
        .description("")
        .members(
            PList.of(
                JavaPojoMembers.requiredString(),
                JavaPojoMembers.requiredInteger(),
                JavaPojoMembers.requiredDouble()))
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
            PList.of(
                JavaPojoMembers.requiredString(),
                JavaPojoMembers.requiredBirthdate(),
                JavaPojoMembers.requiredEmail()))
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
            PList.of(JavaPojoMembers.keywordNameString(), JavaPojoMembers.illegalCharacterString()))
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
        .members(members)
        .type(PojoType.DEFAULT)
        .requiredAdditionalProperties(PList.empty())
        .additionalProperties(additionalProperties)
        .constraints(Constraints.empty())
        .build();
  }

  public static JavaObjectPojo allOfPojo(JavaAllOfComposition javaAllOfComposition) {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoNames.fromNameAndSuffix("OneOfPojo1", "Dto"))
        .schemaName(SchemaName.ofString("OneOfPojo1"))
        .description("")
        .members(PList.empty())
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
        .members(PList.empty())
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
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoNames.fromNameAndSuffix("AnyOfPojo1", "Dto"))
        .schemaName(SchemaName.ofString("AnyOfPojo1"))
        .description("")
        .members(PList.empty())
        .type(PojoType.DEFAULT)
        .requiredAdditionalProperties(PList.empty())
        .additionalProperties(JavaAdditionalProperties.anyTypeAllowed())
        .constraints(Constraints.empty())
        .andOptionals()
        .anyOfComposition(JavaAnyOfComposition.fromPojos(anyOfPojos))
        .build();
  }

  public static JavaObjectPojo anyOfPojo(JavaPojo anyOfPojo, JavaPojo... anyOfPojos) {
    return anyOfPojo(NonEmptyList.single(anyOfPojo).concat(PList.fromArray(anyOfPojos)));
  }

  public static JavaObjectPojo objectPojo(PList<JavaPojoMember> members) {
    return objectPojo(members, JavaAdditionalProperties.anyTypeAllowed());
  }

  public static JavaObjectPojo objectPojo(JavaPojoMember... members) {
    return objectPojo(PList.of(members), JavaAdditionalProperties.anyTypeAllowed());
  }

  public static JavaObjectPojo simpleMapPojo(JavaAdditionalProperties additionalProperties) {
    return withAdditionalProperties(objectPojo(), additionalProperties);
  }

  public static JavaObjectPojo allNecessityAndNullabilityVariants(Constraints constraints) {
    return (JavaObjectPojo)
        JavaPojo.wrap(allNecessityAndNullabilityVariantsPojo(constraints), TypeMappings.empty())
            .getDefaultPojo();
  }

  public static JavaObjectPojo allNecessityAndNullabilityVariants() {
    return allNecessityAndNullabilityVariants(Constraints.empty());
  }

  private static ObjectPojo allNecessityAndNullabilityVariantsPojo(Constraints constraints) {
    return ObjectPojoBuilder.create()
        .name(componentName("NecessityAndNullability", "Dto"))
        .description("NecessityAndNullability")
        .members(
            PList.of(
                requiredString(),
                requiredNullableString(),
                optionalString(),
                optionalNullableString()))
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

  public static JavaObjectPojo withMembers(
      JavaObjectPojo objectPojo, PList<JavaPojoMember> members) {
    return JavaObjectPojoBuilder.create()
        .name(objectPojo.getJavaPojoName())
        .schemaName(SchemaName.ofName(objectPojo.getSchemaName().getOriginalName()))
        .description(objectPojo.getDescription())
        .members(members)
        .type(objectPojo.getType())
        .requiredAdditionalProperties(objectPojo.getRequiredAdditionalProperties())
        .additionalProperties(objectPojo.getAdditionalProperties())
        .constraints(objectPojo.getConstraints())
        .andAllOptionals()
        .allOfComposition(objectPojo.getAllOfComposition())
        .oneOfComposition(objectPojo.getOneOfComposition())
        .anyOfComposition(objectPojo.getAnyOfComposition())
        .build();
  }

  public static JavaObjectPojo withMembers(JavaObjectPojo objectPojo, JavaPojoMember... members) {
    return withMembers(objectPojo, PList.of(members));
  }

  public static JavaObjectPojo withName(JavaObjectPojo objectPojo, PojoName pojoName) {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoName.fromPojoName(pojoName))
        .schemaName(SchemaName.ofName(objectPojo.getSchemaName().getOriginalName()))
        .description(objectPojo.getDescription())
        .members(objectPojo.getMembers())
        .type(objectPojo.getType())
        .requiredAdditionalProperties(objectPojo.getRequiredAdditionalProperties())
        .additionalProperties(objectPojo.getAdditionalProperties())
        .constraints(objectPojo.getConstraints())
        .andAllOptionals()
        .allOfComposition(objectPojo.getAllOfComposition())
        .oneOfComposition(objectPojo.getOneOfComposition())
        .anyOfComposition(objectPojo.getAnyOfComposition())
        .build();
  }

  public static JavaObjectPojo withAdditionalProperties(
      JavaObjectPojo objectPojo, JavaAdditionalProperties additionalProperties) {
    return JavaObjectPojoBuilder.create()
        .name(
            JavaPojoName.fromPojoName(
                PojoName.ofName(Name.ofString(objectPojo.getClassName().asString()))))
        .schemaName(SchemaName.ofName(objectPojo.getSchemaName().getOriginalName()))
        .description(objectPojo.getDescription())
        .members(objectPojo.getMembers())
        .type(objectPojo.getType())
        .requiredAdditionalProperties(objectPojo.getRequiredAdditionalProperties())
        .additionalProperties(additionalProperties)
        .constraints(objectPojo.getConstraints())
        .andAllOptionals()
        .allOfComposition(objectPojo.getAllOfComposition())
        .oneOfComposition(objectPojo.getOneOfComposition())
        .anyOfComposition(objectPojo.getAnyOfComposition())
        .build();
  }

  public static JavaObjectPojo withRequiredAdditionalProperties(
      JavaObjectPojo objectPojo,
      PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties) {
    return JavaObjectPojoBuilder.create()
        .name(
            JavaPojoName.fromPojoName(
                PojoName.ofName(Name.ofString(objectPojo.getClassName().asString()))))
        .schemaName(SchemaName.ofName(objectPojo.getSchemaName().getOriginalName()))
        .description(objectPojo.getDescription())
        .members(objectPojo.getMembers())
        .type(objectPojo.getType())
        .requiredAdditionalProperties(requiredAdditionalProperties)
        .additionalProperties(objectPojo.getAdditionalProperties())
        .constraints(objectPojo.getConstraints())
        .andAllOptionals()
        .allOfComposition(objectPojo.getAllOfComposition())
        .oneOfComposition(objectPojo.getOneOfComposition())
        .anyOfComposition(objectPojo.getAnyOfComposition())
        .build();
  }
}
