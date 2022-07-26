package com.github.muehmar.gradle.openapi.generator.java.generator.data;

import static com.github.muehmar.gradle.openapi.generator.data.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.data.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.data.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.data.Nullability.NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.PojoMembers.optionalNullableString;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.PojoMembers.optionalString;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.PojoMembers.requiredNullableString;
import static com.github.muehmar.gradle.openapi.generator.java.generator.data.PojoMembers.requiredString;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.data.Name;
import com.github.muehmar.gradle.openapi.generator.data.Pojo;
import com.github.muehmar.gradle.openapi.generator.data.PojoMember;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;

public class Pojos {
  private Pojos() {}

  public static Pojo sample() {
    return Pojo.ofObject(
        Name.of("User"),
        "User of the Application.",
        "Dto",
        PList.of(
            new PojoMember(
                Name.of("id"), "ID of this user", JavaType.ofName("long"), REQUIRED, NOT_NULLABLE),
            new PojoMember(
                Name.of("name"), "Name of this user", JavaTypes.STRING, REQUIRED, NOT_NULLABLE),
            new PojoMember(
                Name.of("birthdate"),
                "Name of this user",
                JavaTypes.LOCAL_DATE,
                REQUIRED,
                NOT_NULLABLE),
            new PojoMember(
                Name.of("language"),
                "Preferred language of this user",
                JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                OPTIONAL,
                NULLABLE)));
  }

  public static Pojo array() {
    return Pojo.ofArray(
        Name.of("Array"),
        "Sample Array",
        "Dto",
        new PojoMember(
            Name.of("value"),
            "List of names",
            JavaType.javaList(JavaTypes.STRING),
            REQUIRED,
            NOT_NULLABLE));
  }

  public static Pojo allNecessityAndNullabilityVariants() {
    return Pojo.ofObject(
        Name.of("NecessityAndNullability"),
        "NecessityAndNullability",
        "Dto",
        PList.of(
            requiredString(),
            requiredNullableString(),
            optionalString(),
            optionalNullableString()));
  }
}
