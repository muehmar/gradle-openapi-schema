package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.model.Name;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.EnumDescriptionSettings;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.generator.settings.ValidationApi;
import io.github.muehmar.codegenerator.writer.Writer;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@ExtendWith(SnapshotExtension.class)
class ObjectPojoGeneratorTest {

  private Expect expect;

  private static final JavaObjectPojo SAMPLE_OBJECT_POJO =
      JavaObjectPojo.wrap(
              ObjectPojoBuilder.create()
                  .name(PojoName.ofNameAndSuffix(Name.ofString("User"), "Dto"))
                  .description(
                      "User of the Application. This description is intentionally longer to see if its wrapped to a new line.")
                  .members(
                      PList.of(
                          new PojoMember(
                              Name.ofString("id"),
                              "ID of this user",
                              IntegerType.formatLong(),
                              PropertyScope.DEFAULT,
                              REQUIRED,
                              NOT_NULLABLE),
                          new PojoMember(
                              Name.ofString("name"),
                              "Name of this user",
                              StringType.noFormat(),
                              PropertyScope.DEFAULT,
                              REQUIRED,
                              NOT_NULLABLE),
                          new PojoMember(
                              Name.ofString("language"),
                              "Preferred language of this user",
                              EnumType.ofNameAndMembers(
                                  Name.ofString("LanguageEnum"), PList.of("GERMAN", "ENGLISH")),
                              PropertyScope.DEFAULT,
                              OPTIONAL,
                              NOT_NULLABLE)))
                  .constraints(
                      Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(2, 10)))
                  .additionalProperties(anyTypeAllowed())
                  .build(),
              TypeMappings.empty())
          .head();

  @Test
  void generatePojo_when_minimalPojoSetting_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(false)
            .withEnableValidation(false);

    final String content =
        generator.generate(SAMPLE_OBJECT_POJO, pojoSettings, Writer.createDefault()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_jsonSupportJackson_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings().withEnableSafeBuilder(false).withEnableValidation(false);

    final String content =
        generator.generate(SAMPLE_OBJECT_POJO, pojoSettings, Writer.createDefault()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_enabledSafeBuilder_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(true)
            .withEnableValidation(false);

    final String content =
        generator.generate(SAMPLE_OBJECT_POJO, pojoSettings, Writer.createDefault()).asString();

    expect.toMatchSnapshot(content);
  }

  @ParameterizedTest
  @EnumSource(ValidationApi.class)
  void generatePojo_when_enableValidation_then_correctPojoGenerated(ValidationApi validationApi) {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(false)
            .withEnableValidation(true)
            .withValidationApi(validationApi);

    final JavaObjectPojo pojo =
        JavaObjectPojo.wrap(
                ObjectPojoBuilder.create()
                    .name(PojoName.ofNameAndSuffix(Name.ofString("User"), "Dto"))
                    .description("User of the Application")
                    .members(
                        PList.of(
                            new PojoMember(
                                Name.ofString("id"),
                                "ID of this user",
                                IntegerType.formatLong()
                                    .withConstraints(Constraints.ofMax(new Max(50))),
                                PropertyScope.DEFAULT,
                                REQUIRED,
                                NOT_NULLABLE),
                            new PojoMember(
                                Name.ofString("name"),
                                "Name of this user",
                                StringType.noFormat()
                                    .withConstraints(Constraints.ofSize(Size.of(10, 15))),
                                PropertyScope.DEFAULT,
                                REQUIRED,
                                NOT_NULLABLE),
                            new PojoMember(
                                Name.ofString("lastName"),
                                "Lastname of this user",
                                StringType.noFormat()
                                    .withConstraints(Constraints.ofSize(Size.ofMin(10))),
                                PropertyScope.DEFAULT,
                                REQUIRED,
                                NOT_NULLABLE),
                            new PojoMember(
                                Name.ofString("nickName"),
                                "Nickname of this user",
                                StringType.noFormat()
                                    .withConstraints(Constraints.ofSize(Size.ofMax(50))),
                                PropertyScope.DEFAULT,
                                REQUIRED,
                                NOT_NULLABLE),
                            new PojoMember(
                                Name.ofString("email"),
                                "Email of this user",
                                StringType.noFormat().withConstraints(Constraints.ofEmail()),
                                PropertyScope.DEFAULT,
                                REQUIRED,
                                NOT_NULLABLE),
                            new PojoMember(
                                Name.ofString("height"),
                                "Height of this user",
                                NumericType.formatDouble()
                                    .withConstraints(
                                        Constraints.ofDecimalMin(new DecimalMin("120.0", true))
                                            .withDecimalMax(new DecimalMax("199", false))),
                                PropertyScope.DEFAULT,
                                REQUIRED,
                                NOT_NULLABLE),
                            new PojoMember(
                                Name.ofString("level"),
                                "Level of this user",
                                IntegerType.formatLong()
                                    .withConstraints(Constraints.ofMin(new Min(5))),
                                PropertyScope.DEFAULT,
                                OPTIONAL,
                                NOT_NULLABLE),
                            new PojoMember(
                                Name.ofString("uppercase"),
                                "Something uppercase",
                                StringType.noFormat()
                                    .withConstraints(
                                        Constraints.ofPattern(
                                            Pattern.ofUnescapedString("^(\\d[A-Z]*)"))),
                                PropertyScope.DEFAULT,
                                OPTIONAL,
                                NOT_NULLABLE),
                            new PojoMember(
                                Name.ofString("multipleOfValue"),
                                "Multiple of value",
                                IntegerType.formatLong()
                                    .withConstraints(
                                        Constraints.ofMultipleOf(
                                            new MultipleOf(new BigDecimal("5")))),
                                PropertyScope.DEFAULT,
                                OPTIONAL,
                                NOT_NULLABLE),
                            new PojoMember(
                                Name.ofString("anotherPojo"),
                                "Another Pojo",
                                ObjectType.ofName(PojoName.ofName(Name.ofString("AnotherPojo"))),
                                PropertyScope.DEFAULT,
                                OPTIONAL,
                                NOT_NULLABLE)))
                    .constraints(
                        Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(5, 15)))
                    .additionalProperties(anyTypeAllowed())
                    .build(),
                TypeMappings.empty())
            .head();

    final String content =
        generator.generate(pojo, pojoSettings, Writer.createDefault()).asString();

    expect.scenario(validationApi.getValue()).toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_pojoWithEnumAndEnumDescription_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withEnableSafeBuilder(false)
            .withEnumDescriptionSettings(EnumDescriptionSettings.enabled("`__ENUM__`:", false));

    final JavaObjectPojo pojo =
        JavaObjectPojo.wrap(
                ObjectPojoBuilder.create()
                    .name(PojoName.ofNameAndSuffix(Name.ofString("User"), "Dto"))
                    .description("User of the Application")
                    .members(
                        PList.of(
                            new PojoMember(
                                Name.ofString("language"),
                                "Preferred language of this user\n"
                                    + "* `GERMAN`: German language\n"
                                    + "* `ENGLISH`: English language",
                                EnumType.ofNameAndMembers(
                                    Name.ofString("LanguageEnum"), PList.of("GERMAN", "ENGLISH")),
                                PropertyScope.DEFAULT,
                                OPTIONAL,
                                NOT_NULLABLE)))
                    .constraints(Constraints.empty())
                    .additionalProperties(anyTypeAllowed())
                    .build(),
                TypeMappings.empty())
            .head();

    final String content =
        generator.generate(pojo, pojoSettings, Writer.createDefault()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  void
      generatePojo_when_pojoWithEnumAndEnumDescriptionAndJacksonSupport_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        TestPojoSettings.defaultSettings()
            .withEnableSafeBuilder(false)
            .withEnumDescriptionSettings(EnumDescriptionSettings.enabled("`__ENUM__`:", false));

    final JavaObjectPojo pojo =
        JavaObjectPojo.wrap(
                ObjectPojoBuilder.create()
                    .name(PojoName.ofNameAndSuffix(Name.ofString("User"), "Dto"))
                    .description("User of the Application")
                    .members(
                        PList.of(
                            new PojoMember(
                                Name.ofString("language"),
                                "Preferred language of this user\n"
                                    + "* `GERMAN`: German language\n"
                                    + "* `ENGLISH`: English language",
                                EnumType.ofNameAndMembers(
                                    Name.ofString("LanguageEnum"), PList.of("GERMAN", "ENGLISH")),
                                PropertyScope.DEFAULT,
                                OPTIONAL,
                                NOT_NULLABLE)))
                    .constraints(Constraints.empty())
                    .additionalProperties(anyTypeAllowed())
                    .build(),
                TypeMappings.empty())
            .head();

    final String content =
        generator.generate(pojo, pojoSettings, Writer.createDefault()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_necessityAndNullabilityVariants_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final String content =
        generator
            .generate(
                (JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants(),
                TestPojoSettings.defaultSettings().withEnableSafeBuilder(true),
                Writer.createDefault())
            .asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_objectWithArrayWithUniqueItems_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final JavaPojoMember member =
        JavaPojoMembers.list(
            StringType.noFormat(), Constraints.ofUniqueItems(true), REQUIRED, NOT_NULLABLE);

    final String content =
        generator
            .generate(
                JavaPojos.objectPojo(PList.single(member)),
                TestPojoSettings.defaultSettings(),
                Writer.createDefault())
            .asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_objectWithMapMember_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final JavaPojoMember member =
        JavaPojoMembers.map(
            StringType.noFormat(),
            StringType.uuid(),
            REQUIRED,
            NOT_NULLABLE,
            Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(3, 8)));

    final String content =
        generator
            .generate(
                JavaPojos.objectPojo(PList.single(member)),
                TestPojoSettings.defaultSettings(),
                Writer.createDefault())
            .asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_noAdditionalPropertiesAllowed_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();
    final String content =
        generator
            .generate(
                JavaPojos.objectPojo(PList.empty(), JavaAdditionalProperties.notAllowed()),
                TestPojoSettings.defaultSettings(),
                Writer.createDefault())
            .asString();

    expect.toMatchSnapshot(content);
  }
}
