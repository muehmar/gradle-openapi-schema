package com.github.muehmar.gradle.openapi.generator.java.model;

import static com.github.muehmar.gradle.openapi.generator.model.NewPojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.NewPojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.model.NewPojoMembers.requiredNullableString;
import static com.github.muehmar.gradle.openapi.generator.model.NewPojoMembers.requiredString;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaArrayPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ArrayPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;

public class JavaPojos {
  private JavaPojos() {}

  public static JavaPojo allNecessityAndNullabilityVariants() {
    return JavaPojo.wrap(
        ObjectPojo.of(
            PojoName.ofNameAndSuffix(Name.ofString("NecessityAndNullability"), "Dto"),
            "NecessityAndNullability",
            PList.of(
                requiredString(),
                requiredNullableString(),
                optionalString(),
                optionalNullableString())),
        TypeMappings.empty());
  }

  public static JavaPojo arrayPojo() {
    final ArrayPojo arrayPojo =
        ArrayPojo.of(
            PojoName.ofNameAndSuffix(Name.ofString("Psology"), "Dto"),
            "Doses to be taken",
            NumericType.formatDouble());
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
}
