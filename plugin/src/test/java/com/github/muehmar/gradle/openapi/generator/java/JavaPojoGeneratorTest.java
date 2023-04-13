package com.github.muehmar.gradle.openapi.generator.java;

import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.*;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@ExtendWith(SnapshotExtension.class)
class JavaPojoGeneratorTest {

  private Expect expect;

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
                      IntegerType.formatLong(),
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
                      NOT_NULLABLE)),
              Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(2, 10))),
          TypeMappings.empty());

  @Test
  void generatePojo_when_minimalPojoSetting_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(false)
            .withEnableValidation(false);

    final String content =
        pojoGenerator.generatePojo(SAMPLE_OBJECT_POJO, pojoSettings).getContent();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_jsonSupportJackson_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings().withEnableSafeBuilder(false).withEnableValidation(false);

    final String content =
        pojoGenerator.generatePojo(SAMPLE_OBJECT_POJO, pojoSettings).getContent();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_enabledSafeBuilder_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(true)
            .withEnableValidation(false);

    final String content =
        pojoGenerator.generatePojo(SAMPLE_OBJECT_POJO, pojoSettings).getContent();

    expect.toMatchSnapshot(content);
  }

  @ParameterizedTest
  @EnumSource(ValidationApi.class)
  void generatePojo_when_enableValidation_then_correctPojoGenerated(ValidationApi validationApi) {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(false)
            .withEnableValidation(true)
            .withValidationApi(validationApi);

    final JavaPojo pojo =
        JavaObjectPojo.wrap(
            ObjectPojo.of(
                PojoName.ofNameAndSuffix(Name.ofString("User"), "Dto"),
                "User of the Application",
                PList.of(
                    new PojoMember(
                        Name.ofString("id"),
                        "ID of this user",
                        IntegerType.formatLong().withConstraints(Constraints.ofMax(new Max(50))),
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
                        IntegerType.formatLong().withConstraints(Constraints.ofMin(new Min(5))),
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
                        Name.ofString("multipleOfValue"),
                        "Multiple of value",
                        IntegerType.formatLong()
                            .withConstraints(
                                Constraints.ofMultipleOf(new MultipleOf(new BigDecimal("5")))),
                        OPTIONAL,
                        NOT_NULLABLE),
                    new PojoMember(
                        Name.ofString("anotherPojo"),
                        "Another Pojo",
                        ObjectType.ofName(PojoName.ofName(Name.ofString("AnotherPojo"))),
                        OPTIONAL,
                        NOT_NULLABLE)),
                Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(5, 15))),
            TypeMappings.empty());

    final String content = pojoGenerator.generatePojo(pojo, pojoSettings).getContent();

    expect.scenario(validationApi.getValue()).toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_enumPojo_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(false)
            .withEnableValidation(true);

    final String content = pojoGenerator.generatePojo(GENDER_ENUM_POJO, pojoSettings).getContent();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_enumPojoAndJacksonSupport_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings().withEnableSafeBuilder(false).withEnableValidation(true);

    final String content = pojoGenerator.generatePojo(GENDER_ENUM_POJO, pojoSettings).getContent();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_pojoWithEnumAndEnumDescription_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

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
                        NOT_NULLABLE)),
                Constraints.empty()),
            TypeMappings.empty());

    final String content = pojoGenerator.generatePojo(pojo, pojoSettings).getContent();

    expect.toMatchSnapshot(content);
  }

  @Test
  void
      generatePojo_when_pojoWithEnumAndEnumDescriptionAndJacksonSupport_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

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
                        NOT_NULLABLE)),
                Constraints.empty()),
            TypeMappings.empty());

    final String content = pojoGenerator.generatePojo(pojo, pojoSettings).getContent();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_necessityAndNullabilityVariants_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final String content =
        pojoGenerator
            .generatePojo(
                JavaPojos.allNecessityAndNullabilityVariants(),
                TestPojoSettings.defaultSettings().withEnableSafeBuilder(true))
            .getContent();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_arrayPojoWithUniqueItems_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final String content =
        pojoGenerator
            .generatePojo(
                JavaPojos.arrayPojo(Constraints.ofUniqueItems(true)),
                TestPojoSettings.defaultSettings())
            .getContent();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_objectWithArrayWithUniqueItems_then_correctPojoGenerated() {
    final JavaPojoGenerator pojoGenerator = new JavaPojoGenerator();

    final JavaPojoMember member =
        JavaPojoMembers.list(
            StringType.noFormat(), Constraints.ofUniqueItems(true), REQUIRED, NOT_NULLABLE);

    final String content =
        pojoGenerator
            .generatePojo(
                JavaPojos.objectPojo(PList.single(member)), TestPojoSettings.defaultSettings())
            .getContent();

    expect.toMatchSnapshot(content);
  }
}
