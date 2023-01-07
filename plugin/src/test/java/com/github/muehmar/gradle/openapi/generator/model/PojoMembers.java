package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;

public class PojoMembers {
  private PojoMembers() {}

  public static PojoMember requiredUsername() {
    return new PojoMember(
        Name.ofString("username"),
        "Username",
        StringType.noFormat(),
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static PojoMember requiredBirthdate() {
    return new PojoMember(
        Name.ofString("birthdate"),
        "Birthdate",
        StringType.ofFormat(StringType.Format.DATE),
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static PojoMember requiredString() {
    return new PojoMember(
        Name.ofString("requiredStringVal"),
        "RequiredStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static PojoMember requiredNullableString() {
    return new PojoMember(
        Name.ofString("requiredNullableStringVal"),
        "RequiredNullableStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        Necessity.REQUIRED,
        NULLABLE);
  }

  public static PojoMember optionalString() {
    return new PojoMember(
        Name.ofString("optionalStringVal"),
        "OptionalStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        OPTIONAL,
        NOT_NULLABLE);
  }

  public static PojoMember optionalNullableString() {
    return new PojoMember(
        Name.ofString("optionalNullableStringVal"),
        "OptionalNullableStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        OPTIONAL,
        NULLABLE);
  }
}
