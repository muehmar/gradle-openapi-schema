package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.Resources;
import com.github.muehmar.gradle.openapi.generator.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.java.generator.data.Pojos;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaType;
import com.github.muehmar.gradle.openapi.generator.java.type.JavaTypes;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.settings.EnumDescriptionSettings;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.writer.TestStringWriter;
import org.junit.jupiter.api.Test;

class JavaPojoGeneratorTest {

  @Test
  void generatePojo_when_minimalPojoSetting_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(false)
            .withEnableConstraints(false);

    final Pojo pojo =
        Pojo.ofObject(
            Name.ofString("User"),
            "User of the Application. This description is intentionally longer to see if its wrapped to a new line.",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.ofString("id"),
                    "ID of this user",
                    JavaType.ofName("long"),
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("name"),
                    "Name of this user",
                    JavaTypes.STRING,
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("language"),
                    "Preferred language of this user",
                    JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                    OPTIONAL,
                    NOT_NULLABLE)));

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(Resources.readString("/java/pojos/UserDtoMinimal.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_jsonSupportJackson_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withEnableSafeBuilder(false)
            .withEnableConstraints(false);

    final Pojo pojo =
        Pojo.ofObject(
            Name.ofString("User"),
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.ofString("id"),
                    "ID of this user",
                    JavaType.ofName("long"),
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("name"),
                    "Name of this user",
                    JavaTypes.STRING,
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("language"),
                    "Preferred language of this user",
                    JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                    OPTIONAL,
                    NOT_NULLABLE)));

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(
        Resources.readString("/java/pojos/UserDtoJsonSupportJackson.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_enabledSafeBuilder_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(true)
            .withEnableConstraints(false);

    final Pojo pojo =
        Pojo.ofObject(
            Name.ofString("User"),
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.ofString("id"),
                    "ID of this user",
                    JavaType.ofName("long"),
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("name"),
                    "Name of this user",
                    JavaTypes.STRING,
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("language"),
                    "Preferred language of this user",
                    JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                    OPTIONAL,
                    NOT_NULLABLE)));

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(
        Resources.readString("/java/pojos/UserDtoEnabledSafeBuilder.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_enableConstraints_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(false)
            .withEnableConstraints(true);

    final Pojo pojo =
        Pojo.ofObject(
            Name.ofString("User"),
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.ofString("id"),
                    "ID of this user",
                    JavaType.ofName("long").withConstraints(Constraints.ofMax(new Max(50))),
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("name"),
                    "Name of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.of(10, 15))),
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("lastName"),
                    "Lastname of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.ofMin(10))),
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("nickName"),
                    "Nickname of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofSize(Size.ofMax(50))),
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("email"),
                    "Email of this user",
                    JavaTypes.STRING.withConstraints(Constraints.ofEmail()),
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("height"),
                    "Height of this user",
                    JavaTypes.DOUBLE.withConstraints(
                        Constraints.ofDecimalMin(new DecimalMin("120.0", true))
                            .withDecimalMax(new DecimalMax("199", false))),
                    REQUIRED,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("level"),
                    "Level of this user",
                    JavaType.ofName("Long").withConstraints(Constraints.ofMin(new Min(5))),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("uppercase"),
                    "Something uppercase",
                    JavaTypes.STRING.withConstraints(
                        Constraints.ofPattern(Pattern.ofUnescapedString("^(\\d[A-Z]*)"))),
                    OPTIONAL,
                    NOT_NULLABLE),
                new PojoMember(
                    Name.ofString("anotherPojo"),
                    "Another Pojo",
                    JavaType.ofUserDefined("AnotherPojo"),
                    OPTIONAL,
                    NOT_NULLABLE)));

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(
        Resources.readString("/java/pojos/UserDtoConstraints.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_enumPojo_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(false)
            .withEnableConstraints(true);

    final Pojo pojo =
        Pojo.ofEnum(
            Name.ofString("Gender"),
            "Gender of a user",
            "Dto",
            JavaType.javaEnum(PList.of("MALE", "FEMALE")));

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(Resources.readString("/java/pojos/GenderEnumDto.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_enumPojoAndJacksonSupport_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings().withEnableSafeBuilder(false).withEnableConstraints(true);

    final Pojo pojo =
        Pojo.ofEnum(
            Name.ofString("Gender"),
            "Gender of a user",
            "Dto",
            JavaType.javaEnum(PList.of("MALE", "FEMALE")));

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(
        Resources.readString("/java/pojos/GenderEnumDtoJsonSupportJackson.jv"),
        writer.asString().trim());
  }

  @Test
  void generatePojo_when_pojoWithEnumAndEnumDescription_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(false)
            .withEnumDescriptionSettings(EnumDescriptionSettings.enabled("`__ENUM__`:", false));

    final Pojo pojo =
        Pojo.ofObject(
            Name.ofString("User"),
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.ofString("language"),
                    "Preferred language of this user\n"
                        + "* `GERMAN`: German language\n"
                        + "* `ENGLISH`: English language",
                    JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                    OPTIONAL,
                    NOT_NULLABLE)));

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(Resources.readString("/java/pojos/EnumDescription.jv"), writer.asString().trim());
  }

  @Test
  void
      generatePojo_when_pojoWithEnumAndEnumDescriptionAndJacksonSupport_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withEnableSafeBuilder(false)
            .withEnumDescriptionSettings(EnumDescriptionSettings.enabled("`__ENUM__`:", false));

    final Pojo pojo =
        Pojo.ofObject(
            Name.ofString("User"),
            "User of the Application",
            "Dto",
            PList.of(
                new PojoMember(
                    Name.ofString("language"),
                    "Preferred language of this user\n"
                        + "* `GERMAN`: German language\n"
                        + "* `ENGLISH`: English language",
                    JavaType.javaEnum(PList.of("GERMAN", "ENGLISH")),
                    OPTIONAL,
                    NOT_NULLABLE)));

    pojoGenerator.generatePojo(pojo, pojoSettings);

    assertEquals(
        Resources.readString("/java/pojos/EnumDescriptionSupportJackson.jv"),
        writer.asString().trim());
  }

  @Test
  void generatePojo_when_necessityAndNullabilityVariants_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    pojoGenerator.generatePojo(
        Pojos.allNecessityAndNullabilityVariants(),
        TestPojoSettings.defaultSettings().withEnableSafeBuilder(true));

    assertEquals(
        Resources.readString("/java/pojos/NecessityAndNullability.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_arrayPojo_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    pojoGenerator.generatePojo(Pojos.array(), TestPojoSettings.defaultSettings());

    assertEquals(Resources.readString("/java/pojos/ArrayPojo.jv"), writer.asString().trim());
  }
}
