package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;

import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.*;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAllOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
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
        .name(JavaPojoName.wrap(PojoName.ofNameAndSuffix("SampleObjectPojo1", "Dto")))
        .description("")
        .members(
            PList.of(
                JavaPojoMembers.requiredString(),
                JavaPojoMembers.requiredInteger(),
                JavaPojoMembers.requiredDouble()))
        .type(PojoType.DEFAULT)
        .additionalProperties(JavaAdditionalProperties.anyTypeAllowed())
        .constraints(Constraints.empty())
        .build();
  }

  public static JavaObjectPojo sampleObjectPojo2() {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoName.wrap(PojoName.ofNameAndSuffix("SampleObjectPojo2", "Dto")))
        .description("")
        .members(
            PList.of(
                JavaPojoMembers.requiredString(),
                JavaPojoMembers.requiredBirthdate(),
                JavaPojoMembers.requiredEmail()))
        .type(PojoType.DEFAULT)
        .additionalProperties(JavaAdditionalProperties.anyTypeAllowed())
        .constraints(Constraints.empty())
        .build();
  }

  public static JavaObjectPojo objectPojo(
      PList<JavaPojoMember> members, JavaAdditionalProperties additionalProperties) {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoName.wrap(PojoName.ofNameAndSuffix("ObjectPojo1", "Dto")))
        .description("")
        .members(members)
        .type(PojoType.DEFAULT)
        .additionalProperties(additionalProperties)
        .constraints(Constraints.empty())
        .build();
  }

  public static JavaObjectPojo allOfPojo(JavaAllOfComposition javaAllOfComposition) {
    return JavaObjectPojoBuilder.create()
        .name(JavaPojoName.wrap(PojoName.ofNameAndSuffix("OneOfPojo1", "Dto")))
        .description("")
        .members(PList.empty())
        .type(PojoType.DEFAULT)
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
        .name(JavaPojoName.wrap(PojoName.ofNameAndSuffix("OneOfPojo1", "Dto")))
        .description("")
        .members(PList.empty())
        .type(PojoType.DEFAULT)
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
        .name(JavaPojoName.wrap(PojoName.ofNameAndSuffix("AnyOfPojo1", "Dto")))
        .description("")
        .members(PList.empty())
        .type(PojoType.DEFAULT)
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
        .name(PojoName.ofNameAndSuffix(Name.ofString("NecessityAndNullability"), "Dto"))
        .description("NecessityAndNullability")
        .members(
            PList.of(
                requiredString(),
                requiredNullableString(),
                optionalString(),
                optionalNullableString()))
        .constraints(constraints)
        .additionalProperties(anyTypeAllowed())
        .build();
  }

  public static JavaArrayPojo arrayPojo(Constraints constraints) {
    final ArrayPojo arrayPojo =
        ArrayPojo.of(
            PojoName.ofNameAndSuffix(Name.ofString("Posology"), "Dto"),
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
            PojoName.ofNameAndSuffix(Name.ofString("Gender"), "Dto"),
            "Gender of person",
            PList.of("male", "female", "divers", "other"));
    return JavaEnumPojo.wrap(enumPojo);
  }

  public static JavaObjectPojo withMembers(
      JavaObjectPojo objectPojo, PList<JavaPojoMember> members) {
    return JavaObjectPojoBuilder.create()
        .name(
            JavaPojoName.wrap(PojoName.ofName(Name.ofString(objectPojo.getClassName().asString()))))
        .description(objectPojo.getDescription())
        .members(members)
        .type(objectPojo.getType())
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
        .name(JavaPojoName.wrap(pojoName))
        .description(objectPojo.getDescription())
        .members(objectPojo.getMembers())
        .type(objectPojo.getType())
        .additionalProperties(objectPojo.getAdditionalProperties())
        .constraints(objectPojo.getConstraints())
        .andAllOptionals()
        .allOfComposition(objectPojo.getAllOfComposition())
        .oneOfComposition(objectPojo.getOneOfComposition())
        .anyOfComposition(objectPojo.getAnyOfComposition())
        .build();
  }
}
