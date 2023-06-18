package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredUsername;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.*;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.Discriminator;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.HashMap;
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

  public static JavaComposedPojo composedPojo(ComposedPojo.CompositionType type) {
    return composedPojo(type, Optional.empty());
  }

  public static JavaComposedPojo composedPojoWithDiscriminator(ComposedPojo.CompositionType type) {
    final Discriminator discriminator =
        Discriminator.fromPropertyName(Name.ofString(requiredString().getName().asString()));
    return composedPojo(type, discriminator);
  }

  public static JavaComposedPojo composedPojoWithDiscriminatorMapping(
      ComposedPojo.CompositionType type) {
    final HashMap<String, Name> mapping = new HashMap<>();
    mapping.put("UserValue", Name.ofString("User"));
    mapping.put(
        "NNVariantsValue",
        allNecessityAndNullabilityVariantsPojo(Constraints.empty()).getName().getName());
    final Discriminator discriminator =
        Discriminator.fromPropertyNameAndMapping(
            Name.ofString(requiredString().getName().asString()), mapping);
    return composedPojo(type, discriminator);
  }

  public static JavaComposedPojo composedPojo(
      ComposedPojo.CompositionType type, Discriminator discriminator) {
    return composedPojo(type, Optional.of(discriminator));
  }

  private static JavaComposedPojo composedPojo(
      ComposedPojo.CompositionType type, Optional<Discriminator> discriminator) {
    final ObjectPojo userObjectPojo =
        ObjectPojoBuilder.create()
            .name(PojoName.ofNameAndSuffix(Name.ofString("User"), "Dto"))
            .description("User")
            .members(PList.of(requiredUsername(), requiredBirthdate()))
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();
    final Name typeName =
        Name.ofString(type.name().toLowerCase().replace("_", "")).startUpperCase();
    final UnresolvedComposedPojo unresolvedComposedPojo =
        new UnresolvedComposedPojo(
            PojoName.ofNameAndSuffix(typeName.prefix("Composed"), "Dto"),
            "Composition Description",
            UnresolvedComposedPojo.CompositionType.ONE_OF,
            PList.empty(),
            Constraints.empty(),
            discriminator);
    final PList<Pojo> pojos =
        PList.of(userObjectPojo, allNecessityAndNullabilityVariantsPojo(Constraints.empty()));
    final ComposedPojo composedPojo =
        type.equals(ComposedPojo.CompositionType.ANY_OF)
            ? ComposedPojo.resolvedAnyOf(pojos, unresolvedComposedPojo)
            : ComposedPojo.resolvedOneOf(pojos, unresolvedComposedPojo);
    return (JavaComposedPojo) JavaPojo.wrap(composedPojo, TypeMappings.empty()).getDefaultPojo();
  }

  public static JavaComposedPojo composedPojo(
      PList<JavaPojo> pojos, ComposedPojo.CompositionType compositionType) {
    return new JavaComposedPojo(
        JavaPojoName.wrap(PojoName.ofNameAndSuffix("ComposedPojo", "Dto")),
        "",
        pojos,
        compositionType,
        PojoType.DEFAULT,
        Constraints.empty(),
        Optional.empty());
  }
}
