package com.github.muehmar.gradle.openapi.generator.model;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NULLABLE;

import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.type.ArrayType;
import com.github.muehmar.gradle.openapi.generator.model.type.MapType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;

public class PojoMembers {
  private PojoMembers() {}

  public static PojoMember requiredUsername() {
    return new PojoMember(
        Name.ofString("username"),
        "Username",
        StringType.noFormat(),
        PropertyScope.DEFAULT,
        Necessity.REQUIRED,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember requiredBirthdate() {
    return requiredBirthdate(PropertyScope.DEFAULT);
  }

  public static PojoMember requiredBirthdate(PropertyScope propertyScope) {
    return new PojoMember(
        Name.ofString("birthdate"),
        "Birthdate",
        StringType.ofFormat(StringType.Format.DATE),
        propertyScope,
        Necessity.REQUIRED,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember requiredString() {
    return requiredString(PropertyScope.DEFAULT);
  }

  public static PojoMember requiredString(PropertyScope propertyScope) {
    return new PojoMember(
        Name.ofString("requiredStringVal"),
        "RequiredStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        propertyScope,
        Necessity.REQUIRED,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember requiredNullableString() {
    return new PojoMember(
        Name.ofString("requiredNullableStringVal"),
        "RequiredNullableStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")))
            .withNullability(NULLABLE),
        PropertyScope.DEFAULT,
        Necessity.REQUIRED,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember optionalString() {
    return new PojoMember(
        Name.ofString("optionalStringVal"),
        "OptionalStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello"))),
        PropertyScope.DEFAULT,
        OPTIONAL,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember optionalNullableString() {
    return new PojoMember(
        Name.ofString("optionalNullableStringVal"),
        "OptionalNullableStringVal",
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")))
            .withNullability(NULLABLE),
        PropertyScope.DEFAULT,
        OPTIONAL,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember requiredListWithNullableItems() {
    final StringType itemType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")))
            .withNullability(NULLABLE);
    return new PojoMember(
        Name.ofString("requiredListWithNullableItems"),
        "RequiredListWithNullableItems",
        ArrayType.ofItemType(itemType, NOT_NULLABLE)
            .withConstraints(Constraints.ofSize(Size.of(5, 10))),
        PropertyScope.DEFAULT,
        REQUIRED,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember requiredNullableListWithNullableItems() {
    final StringType itemType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")))
            .withNullability(NULLABLE);
    return new PojoMember(
        Name.ofString("requiredNullableListWithNullableItems"),
        "RequiredNullableListWithNullableItems",
        ArrayType.ofItemType(itemType, NULLABLE)
            .withConstraints(Constraints.ofSize(Size.of(5, 10))),
        PropertyScope.DEFAULT,
        REQUIRED,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember optionalListWithNullableItems() {
    final StringType itemType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")))
            .withNullability(NULLABLE);
    return new PojoMember(
        Name.ofString("optionalListWithNullableItems"),
        "OptionalListWithNullableItems",
        ArrayType.ofItemType(itemType, NOT_NULLABLE)
            .withConstraints(Constraints.ofSize(Size.of(5, 10))),
        PropertyScope.DEFAULT,
        OPTIONAL,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember optionalNullableListWithNullableItems() {
    final StringType itemType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")))
            .withNullability(NULLABLE);
    return new PojoMember(
        Name.ofString("optionalNullableListWithNullableItems"),
        "OptionalNullableListWithNullableItems",
        ArrayType.ofItemType(itemType, NULLABLE)
            .withConstraints(Constraints.ofSize(Size.of(5, 10))),
        PropertyScope.DEFAULT,
        OPTIONAL,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember requiredMap() {
    final StringType valueType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")));
    return new PojoMember(
        Name.ofString("requiredMap"),
        "RequiredMap",
        MapType.ofKeyAndValueType(StringType.noFormat(), valueType),
        PropertyScope.DEFAULT,
        Necessity.REQUIRED,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember requiredMapWithNullableValues() {
    final StringType valueType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")))
            .withNullability(NULLABLE);
    return new PojoMember(
        Name.ofString("requiredMapWithNullableValues"),
        "RequiredMapWithNullableValues",
        MapType.ofKeyAndValueType(StringType.noFormat(), valueType),
        PropertyScope.DEFAULT,
        Necessity.REQUIRED,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember requiredNullableMap() {
    final StringType valueType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")));
    return new PojoMember(
        Name.ofString("requiredNullableMap"),
        "RequiredNullableMap",
        MapType.ofKeyAndValueType(StringType.noFormat(), valueType).withNullability(NULLABLE),
        PropertyScope.DEFAULT,
        Necessity.REQUIRED,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember requiredNullableMapWithNullableValues() {
    final StringType valueType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")))
            .withNullability(NULLABLE);
    return new PojoMember(
        Name.ofString("requiredNullableMapWithNullableValues"),
        "RequiredNullableMapWithNullableValues",
        MapType.ofKeyAndValueType(StringType.noFormat(), valueType).withNullability(NULLABLE),
        PropertyScope.DEFAULT,
        Necessity.REQUIRED,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember optionalMap() {
    final StringType valueType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")));
    return new PojoMember(
        Name.ofString("optionalMap"),
        "OptionalMap",
        MapType.ofKeyAndValueType(StringType.noFormat(), valueType),
        PropertyScope.DEFAULT,
        Necessity.OPTIONAL,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember optionalMapWithNullableValues() {
    final StringType valueType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")))
            .withNullability(NULLABLE);
    return new PojoMember(
        Name.ofString("optionalMapWithNullableValues"),
        "OptionalMapWithNullableValues",
        MapType.ofKeyAndValueType(StringType.noFormat(), valueType),
        PropertyScope.DEFAULT,
        Necessity.OPTIONAL,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember optionalNullableMap() {
    final StringType valueType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")));
    return new PojoMember(
        Name.ofString("optionalNullableMap"),
        "OptionalNullableMap",
        MapType.ofKeyAndValueType(StringType.noFormat(), valueType).withNullability(NULLABLE),
        PropertyScope.DEFAULT,
        Necessity.OPTIONAL,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember optionalNullableMapWithNullableValues() {
    final StringType valueType =
        StringType.noFormat()
            .withConstraints(Constraints.ofPattern(Pattern.ofUnescapedString("Hello")))
            .withNullability(NULLABLE);
    return new PojoMember(
        Name.ofString("optionalNullableMapWithNullableValues"),
        "OptionalNullableMapWithNullableValues",
        MapType.ofKeyAndValueType(StringType.noFormat(), valueType).withNullability(NULLABLE),
        PropertyScope.DEFAULT,
        Necessity.OPTIONAL,
        PojoMemberXml.noDefinition());
  }

  public static PojoMember ofType(Type type) {
    return new PojoMember(
        Name.ofString("member"),
        "description",
        type,
        PropertyScope.DEFAULT,
        REQUIRED,
        PojoMemberXml.noDefinition());
  }
}
