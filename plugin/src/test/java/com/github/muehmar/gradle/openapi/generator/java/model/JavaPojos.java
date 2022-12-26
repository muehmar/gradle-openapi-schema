package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.UnresolvedComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ComposedPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import java.util.Optional;

public class JavaPojos {
  private JavaPojos() {}

  public static JavaPojo allNecessityAndNullabilityVariants() {
    return JavaPojo.wrap(allNecessityAndNullabilityVariantsPojo(), TypeMappings.empty());
  }

  private static ObjectPojo allNecessityAndNullabilityVariantsPojo() {
    final ObjectPojo allNecessityAndNullabilityVariantsPojo =
        ObjectPojo.of(
            PojoName.ofNameAndSuffix(Name.ofString("NecessityAndNullability"), "Dto"),
            "NecessityAndNullability",
            PList.of(
                requiredString(),
                requiredNullableString(),
                optionalString(),
                optionalNullableString()));
    return allNecessityAndNullabilityVariantsPojo;
  }

  public static JavaPojo arrayPojo() {
    final ArrayPojo arrayPojo =
        ArrayPojo.of(
            PojoName.ofNameAndSuffix(Name.ofString("Posology"), "Dto"),
            "Doses to be taken",
            NumericType.formatDouble(),
            Constraints.empty());
    return JavaArrayPojo.wrap(arrayPojo, TypeMappings.empty());
  }

  public static JavaPojo enumPojo() {
    final EnumPojo enumPojo =
        EnumPojo.of(
            PojoName.ofNameAndSuffix(Name.ofString("Gender"), "Dto"),
            "Gender of person",
            PList.of("male", "female", "divers", "other"));
    return JavaEnumPojo.wrap(enumPojo);
  }

  public static JavaPojo composedPojo(ComposedPojo.CompositionType type) {
    final Name typeName =
        Name.ofString(type.name().toLowerCase().replace("_", "")).startUpperCase();
    final UnresolvedComposedPojo unresolvedComposedPojo =
        new UnresolvedComposedPojo(
            PojoName.ofNameAndSuffix(typeName.prefix("Composed"), "Dto"),
            "Composition Description",
            UnresolvedComposedPojo.CompositionType.ONE_OF,
            PList.empty(),
            Optional.empty());
    final PList<Pojo> pojos = PList.of(allNecessityAndNullabilityVariantsPojo());
    final ComposedPojo composedPojo =
        type.equals(ComposedPojo.CompositionType.ANY_OF)
            ? ComposedPojo.resolvedAnyOf(pojos, unresolvedComposedPojo)
            : ComposedPojo.resolvedOneOf(pojos, unresolvedComposedPojo);
    return JavaPojo.wrap(composedPojo, TypeMappings.empty());
  }
}
