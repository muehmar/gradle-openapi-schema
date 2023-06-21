package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.*;
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
import java.util.Optional;

public class JavaPojos {
  private JavaPojos() {}

  public static JavaObjectPojo sampleObjectPojo1() {
    return JavaObjectPojo.from(
        PojoName.ofNameAndSuffix("SampleObjectPojo1", "Dto"),
        "",
        PList.of(
            JavaPojoMembers.requiredString(),
            JavaPojoMembers.requiredInteger(),
            JavaPojoMembers.requiredDouble()),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        PojoType.DEFAULT,
        JavaAdditionalProperties.anyTypeAllowed(),
        Constraints.empty());
  }

  public static JavaObjectPojo sampleObjectPojo2() {
    return JavaObjectPojo.from(
        PojoName.ofNameAndSuffix("SampleObjectPojo2", "Dto"),
        "",
        PList.of(
            JavaPojoMembers.requiredString(),
            JavaPojoMembers.requiredBirthdate(),
            JavaPojoMembers.requiredEmail()),
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        PojoType.DEFAULT,
        JavaAdditionalProperties.anyTypeAllowed(),
        Constraints.empty());
  }

  public static JavaObjectPojo objectPojo(
      PList<JavaPojoMember> members, JavaAdditionalProperties additionalProperties) {
    return JavaObjectPojo.from(
        PojoName.ofNameAndSuffix("ObjectPojo1", "Dto"),
        "",
        members,
        Optional.empty(),
        Optional.empty(),
        Optional.empty(),
        PojoType.DEFAULT,
        additionalProperties,
        Constraints.empty());
  }

  public static JavaObjectPojo oneOfPojo(JavaOneOfComposition javaOneOfComposition) {
    return JavaObjectPojo.from(
        PojoName.ofNameAndSuffix("OneOfPojo1", "Dto"),
        "",
        PList.empty(),
        Optional.empty(),
        Optional.of(javaOneOfComposition),
        Optional.empty(),
        PojoType.DEFAULT,
        JavaAdditionalProperties.anyTypeAllowed(),
        Constraints.empty());
  }

  public static JavaObjectPojo oneOfPojo(PList<JavaPojo> oneOfPojos) {
    return oneOfPojo(JavaOneOfComposition.fromPojos(oneOfPojos));
  }

  public static JavaObjectPojo oneOfPojo(JavaPojo... oneOfPojos) {
    return oneOfPojo(PList.fromArray(oneOfPojos));
  }

  public static JavaObjectPojo anyOfPojo(PList<JavaPojo> anyOfPojos) {
    return JavaObjectPojo.from(
        PojoName.ofNameAndSuffix("AnyOfPojo1", "Dto"),
        "",
        PList.empty(),
        Optional.empty(),
        Optional.empty(),
        Optional.of(JavaAnyOfComposition.fromPojos(anyOfPojos)),
        PojoType.DEFAULT,
        JavaAdditionalProperties.anyTypeAllowed(),
        Constraints.empty());
  }

  public static JavaObjectPojo anyOfPojo(JavaPojo... anyOfPojos) {
    return anyOfPojo(PList.fromArray(anyOfPojos));
  }

  public static JavaObjectPojo objectPojo(PList<JavaPojoMember> members) {
    return objectPojo(members, JavaAdditionalProperties.anyTypeAllowed());
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
}
