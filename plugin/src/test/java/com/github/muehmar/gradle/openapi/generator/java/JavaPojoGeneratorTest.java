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
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.EnumDescriptionSettings;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.writer.TestStringWriter;
import org.junit.jupiter.api.Test;

class JavaPojoGeneratorTest {

  private static final JavaPojo GENDER_ENUM_POJO =
      JavaEnumPojo.wrap(
          EnumPojo.of(
              PojoName.ofNameAndSuffix(Name.ofString("Gender"), "Dto"),
              "Gender of a user",
              PList.of("MALE", "FEMALE")));

  private static final JavaPojo SAMPLE_OBJECT_POJO =
      JavaObjectPojo.wrap(
          ObjectPojo.of(
              PojoName.ofNameAndSuffix(Name.ofString("User"), "Dto"),
              "User of the Application. This description is intentionally longer to see if its wrapped to a new line.",
              PList.of(
                  new PojoMember(
                      Name.ofString("id"),
                      "ID of this user",
                      NumericType.formatLong(),
                      REQUIRED,
                      NOT_NULLABLE),
                  new PojoMember(
                      Name.ofString("name"),
                      "Name of this user",
                      StringType.noFormat(),
                      REQUIRED,
                      NOT_NULLABLE),
                  new PojoMember(
                      Name.ofString("language"),
                      "Preferred language of this user",
                      EnumType.ofNameAndMembers(
                          Name.ofString("LanguageEnum"), PList.of("GERMAN", "ENGLISH")),
                      OPTIONAL,
                      NOT_NULLABLE))),
          TypeMappings.empty());

  @Test
  void generatePojo_when_minimalPojoSetting_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(false)
            .withEnableConstraints(false);

    pojoGenerator.generatePojo(SAMPLE_OBJECT_POJO, pojoSettings);

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

    pojoGenerator.generatePojo(SAMPLE_OBJECT_POJO, pojoSettings);

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

    pojoGenerator.generatePojo(SAMPLE_OBJECT_POJO, pojoSettings);

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

    final JavaPojo pojo =
        JavaObjectPojo.wrap(
            ObjectPojo.of(
                PojoName.ofNameAndSuffix(Name.ofString("User"), "Dto"),
                "User of the Application",
                PList.of(
                    new PojoMember(
                        Name.ofString("id"),
                        "ID of this user",
                        NumericType.formatLong().withConstraints(Constraints.ofMax(new Max(50))),
                        REQUIRED,
                        NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("name"),
                        "Name of this user",
                        StringType.noFormat().withConstraints(Constraints.ofSize(Size.of(10, 15))),
                        REQUIRED,
                        NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("lastName"),
                        "Lastname of this user",
                        StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMin(10))),
                        REQUIRED,
                        NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("nickName"),
                        "Nickname of this user",
                        StringType.noFormat().withConstraints(Constraints.ofSize(Size.ofMax(50))),
                        REQUIRED,
                        NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("email"),
                        "Email of this user",
                        StringType.noFormat().withConstraints(Constraints.ofEmail()),
                        REQUIRED,
                        NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("height"),
                        "Height of this user",
                        NumericType.formatDouble()
                            .withConstraints(
                                Constraints.ofDecimalMin(new DecimalMin("120.0", true))
                                    .withDecimalMax(new DecimalMax("199", false))),
                        REQUIRED,
                        NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("level"),
                        "Level of this user",
                        NumericType.formatLong().withConstraints(Constraints.ofMin(new Min(5))),
                        OPTIONAL,
                        NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("uppercase"),
                        "Something uppercase",
                        StringType.noFormat()
                            .withConstraints(
                                Constraints.ofPattern(Pattern.ofUnescapedString("^(\\d[A-Z]*)"))),
                        OPTIONAL,
                        NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("anotherPojo"),
                        "Another Pojo",
                        ObjectType.ofName(PojoName.ofName(Name.ofString("AnotherPojo"))),
                        OPTIONAL,
                        NOT_NULLABLE))),
            TypeMappings.empty());

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

    pojoGenerator.generatePojo(GENDER_ENUM_POJO, pojoSettings);

    assertEquals(Resources.readString("/java/pojos/GenderEnumDto.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_enumPojoAndJacksonSupport_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings().withEnableSafeBuilder(false).withEnableConstraints(true);

    pojoGenerator.generatePojo(GENDER_ENUM_POJO, pojoSettings);

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

    final JavaPojo pojo =
        JavaObjectPojo.wrap(
            ObjectPojo.of(
                PojoName.ofNameAndSuffix(Name.ofString("User"), "Dto"),
                "User of the Application",
                PList.of(
                    new PojoMember(
                        Name.ofString("language"),
                        "Preferred language of this user\n"
                            + "* `GERMAN`: German language\n"
                            + "* `ENGLISH`: English language",
                        EnumType.ofNameAndMembers(
                            Name.ofString("LanguageEnum"), PList.of("GERMAN", "ENGLISH")),
                        OPTIONAL,
                        NOT_NULLABLE))),
            TypeMappings.empty());

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

    final JavaPojo pojo =
        JavaObjectPojo.wrap(
            ObjectPojo.of(
                PojoName.ofNameAndSuffix(Name.ofString("User"), "Dto"),
                "User of the Application",
                PList.of(
                    new PojoMember(
                        Name.ofString("language"),
                        "Preferred language of this user\n"
                            + "* `GERMAN`: German language\n"
                            + "* `ENGLISH`: English language",
                        EnumType.ofNameAndMembers(
                            Name.ofString("LanguageEnum"), PList.of("GERMAN", "ENGLISH")),
                        OPTIONAL,
                        NOT_NULLABLE))),
            TypeMappings.empty());

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
        JavaPojos.allNecessityAndNullabilityVariants(),
        TestPojoSettings.defaultSettings().withEnableSafeBuilder(true));

    assertEquals(
        Resources.readString("/java/pojos/NecessityAndNullability.jv"), writer.asString().trim());
  }

  @Test
  void generatePojo_when_arrayPojo_then_correctPojoGenerated() {
    final TestStringWriter writer = new TestStringWriter();
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator(() -> writer);

    pojoGenerator.generatePojo(JavaPojos.arrayPojo(), TestPojoSettings.defaultSettings());

    assertEquals(Resources.readString("/java/pojos/ArrayPojo.jv"), writer.asString().trim());
  }
}
