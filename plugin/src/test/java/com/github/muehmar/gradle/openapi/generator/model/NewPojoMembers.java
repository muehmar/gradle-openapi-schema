package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;

public class NewPojoMembers {
  private NewPojoMembers() {}

  public static NewPojoMember requiredString() {
    return new NewPojoMember(
        Name.ofString("requiredStringVal"),
        "RequiredStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static NewPojoMember requiredNullableString() {
    return new NewPojoMember(
        Name.ofString("requiredNullableStringVal"),
        "RequiredNullableStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        Necessity.REQUIRED,
        NULLABLE);
  }

  public static NewPojoMember optionalString() {
    return new NewPojoMember(
        Name.ofString("optionalStringVal"),
        "OptionalStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        OPTIONAL,
        NOT_NULLABLE);
  }

  public static NewPojoMember optionalNullableString() {
    return new NewPojoMember(
        Name.ofString("optionalNullableStringVal"),
        "OptionalNullableStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        OPTIONAL,
        NULLABLE);
  }
}
