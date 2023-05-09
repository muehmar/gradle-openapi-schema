package com.github.muehmar.gradle.openapi.generator.java.model.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredUsername;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoName;
import com.github.muehmar.gradle.openapi.generator.java.model.PojoType;
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

  public static JavaObjectPojo objectPojo(PList<JavaPojoMember> members) {
    return JavaObjectPojo.from(
        PojoName.ofNameAndSuffix("ObjectPojo1", "Dto"),
        "",
        members,
        PojoType.DEFAULT,
        Constraints.empty());
  }

  public static JavaPojo allNecessityAndNullabilityVariants(Constraints constraints) {
    return JavaPojo.wrap(allNecessityAndNullabilityVariantsPojo(constraints), TypeMappings.empty())
        .head();
  }

  public static JavaPojo allNecessityAndNullabilityVariants() {
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
    return (JavaComposedPojo) JavaPojo.wrap(composedPojo, TypeMappings.empty()).head();
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
