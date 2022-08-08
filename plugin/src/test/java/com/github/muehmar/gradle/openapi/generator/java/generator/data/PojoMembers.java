package com.github.muehmar.gradle.openapi.generator.java.generator.data;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Necessity;
import com.github.muehmar.gradle.openapi.generator.model.Nullability;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;

public class PojoMembers {
  private PojoMembers() {}

  public static PojoMember requiredBirthdate() {
    return new PojoMember(
        Name.of("birthdate"),
        "Birthdate",
        JavaTypes.LOCAL_DATE,
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static PojoMember requiredReference() {
    return new PojoMember(
        Name.of("ref"),
        "ref",
        JavaType.ofReference(Name.of("SomeObject"), "Dto"),
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static PojoMember requiredEmail() {
    return new PojoMember(
        Name.of("email"),
        "email",
        JavaTypes.STRING.withConstraints(Constraints.ofEmail()),
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static PojoMember requiredInteger() {
    return new PojoMember(
        Name.of("intVal"),
        "intVal",
        JavaTypes.INTEGER.withConstraints(Constraints.ofMin(new Min(10)).withMax(new Max(50))),
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static PojoMember requiredDouble() {
    return new PojoMember(
        Name.of("doubleVal"),
        "doubleVal",
        JavaTypes.DOUBLE.withConstraints(
            Constraints.ofDecimalMin(new DecimalMin("12.5", true))
                .withDecimalMax(new DecimalMax("50.1", false))),
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static PojoMember requiredStringList() {
    return new PojoMember(
        Name.of("doubleVal"),
        "doubleVal",
        JavaType.javaList(JavaTypes.STRING).withConstraints(Constraints.ofSize(Size.of(1, 50))),
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static PojoMember requiredString() {
    return new PojoMember(
        Name.of("requiredStringVal"),
        "RequiredStringVal",
        JavaTypes.STRING.withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        Necessity.REQUIRED,
        Nullability.NOT_NULLABLE);
  }

  public static PojoMember requiredNullableString() {
    return new PojoMember(
        Name.of("requiredNullableStringVal"),
        "RequiredNullableStringVal",
        JavaTypes.STRING.withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        Necessity.REQUIRED,
        NULLABLE);
  }

  public static PojoMember optionalString() {
    return new PojoMember(
        Name.of("optionalStringVal"),
        "OptionalStringVal",
        JavaTypes.STRING.withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        OPTIONAL,
        NOT_NULLABLE);
  }

  public static PojoMember optionalNullableString() {
    return new PojoMember(
        Name.of("optionalNullableStringVal"),
        "OptionalNullableStringVal",
        JavaTypes.STRING.withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        OPTIONAL,
        NULLABLE);
  }
}
