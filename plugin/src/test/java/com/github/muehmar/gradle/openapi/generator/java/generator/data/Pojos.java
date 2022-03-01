package com.github.muehmar.gradle.openapi.generator.java.generator.data;

import static com.github.muehmar.gradle.openapi.generator.data.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.data.Necessity.REQUIRED;

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
            new PojoMember(Name.of("id"), "ID of this user", JavaType.ofName("long"), REQUIRED),
            new PojoMember(Name.of("name"), "Name of this user", JavaTypes.STRING, REQUIRED),
            new PojoMember(
                Name.of("language"),
                "Preferred language of this user",
                JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                OPTIONAL)));
  }
}