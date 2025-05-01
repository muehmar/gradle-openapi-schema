package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.optionalBirthdate;
import static com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers.requiredEmail;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.illegalIdentifierPojo;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.anyType;
import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaTypes.stringType;
import static com.github.muehmar.gradle.openapi.generator.model.AdditionalProperties.anyTypeAllowed;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.OPTIONAL;
import static com.github.muehmar.gradle.openapi.generator.model.Necessity.REQUIRED;
import static com.github.muehmar.gradle.openapi.generator.model.Nullability.NOT_NULLABLE;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredBirthdate;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredString;
import static com.github.muehmar.gradle.openapi.generator.model.PojoMembers.requiredUsername;
import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.model.name.PojoNames.pojoName;
import static com.github.muehmar.gradle.openapi.generator.settings.StagedBuilderSettingsBuilder.fullStagedBuilderSettingsBuilder;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.member.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.member.TestJavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojoXml;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaEnumType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaIntegerType;
import com.github.muehmar.gradle.openapi.generator.java.model.type.JavaObjectType;
import com.github.muehmar.gradle.openapi.generator.model.Pojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoMember;
import com.github.muehmar.gradle.openapi.generator.model.PojoXml;
import com.github.muehmar.gradle.openapi.generator.model.PropertyScope;
import com.github.muehmar.gradle.openapi.generator.model.composition.AllOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.AnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.composition.OneOfComposition;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMax;
import com.github.muehmar.gradle.openapi.generator.model.constraints.DecimalMin;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Max;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Min;
import com.github.muehmar.gradle.openapi.generator.model.constraints.MultipleOf;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Pattern;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Size;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.model.name.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.ObjectPojoBuilder;
import com.github.muehmar.gradle.openapi.generator.model.type.EnumType;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.NumericType;
import com.github.muehmar.gradle.openapi.generator.model.type.StandardObjectType;
import com.github.muehmar.gradle.openapi.generator.model.type.StringType;
import com.github.muehmar.gradle.openapi.generator.settings.*;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.writer.Writer;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.BiFunction;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class ObjectPojoGeneratorTest {

  private Expect expect;

  private static final JavaObjectPojo SAMPLE_OBJECT_POJO =
      (JavaObjectPojo)
          JavaObjectPojo.wrap(
                  ObjectPojoBuilder.create()
                      .name(componentName("User", "Dto"))
                      .description(
                          "User of the Application. This description is intentionally longer to see if its wrapped to a new line.")
                      .nullability(NOT_NULLABLE)
                      .pojoXml(PojoXml.noXmlDefinition())
                      .members(
                          PList.of(
                              new PojoMember(
                                  Name.ofString("id"),
                                  "ID of this user",
                                  IntegerType.formatLong(),
                                  PropertyScope.DEFAULT,
                                  REQUIRED),
                              new PojoMember(
                                  Name.ofString("name"),
                                  "Name of this user",
                                  StringType.noFormat(),
                                  PropertyScope.DEFAULT,
                                  REQUIRED),
                              new PojoMember(
                                  Name.ofString("language"),
                                  "Preferred language of this user",
                                  EnumType.ofNameAndMembers(
                                      Name.ofString("LanguageEnum"), PList.of("GERMAN", "ENGLISH")),
                                  PropertyScope.DEFAULT,
                                  OPTIONAL)))
                      .requiredAdditionalProperties(PList.empty())
                      .constraints(
                          Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(2, 10)))
                      .additionalProperties(anyTypeAllowed())
                      .build(),
                  TypeMappings.empty())
              .getDefaultPojo();

  @Test
  @SnapshotName("minimalPojoSetting")
  void generatePojo_when_minimalPojoSetting_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        defaultTestSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withStagedBuilder(fullStagedBuilderSettingsBuilder().enabled(false).build())
            .withEnableValidation(false);

    final String content =
        generator.generate(SAMPLE_OBJECT_POJO, pojoSettings, javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("jsonSupportJackson")
  void generatePojo_when_jsonSupportJackson_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        defaultTestSettings()
            .withStagedBuilder(fullStagedBuilderSettingsBuilder().enabled(false).build())
            .withEnableValidation(false);

    final String content =
        generator.generate(SAMPLE_OBJECT_POJO, pojoSettings, javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("xmlSupportJackson")
  void generatePojo_when_xmlSupportJackson_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        defaultTestSettings()
            .withXmlSupport(XmlSupport.JACKSON)
            .withStagedBuilder(fullStagedBuilderSettingsBuilder().enabled(false).build())
            .withEnableValidation(false);

    final String content =
        generator
            .generate(
                SAMPLE_OBJECT_POJO.withPojoXml(new JavaPojoXml(Optional.of("root-name"))),
                pojoSettings,
                javaWriter())
            .asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("enabledStagedBuilder")
  void generatePojo_when_enabledStagedBuilder_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        defaultTestSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withStagedBuilder(fullStagedBuilderSettingsBuilder().enabled(true).build())
            .withEnableValidation(false);

    final String content =
        generator.generate(SAMPLE_OBJECT_POJO, pojoSettings, javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @ParameterizedTest
  @EnumSource(ValidationApi.class)
  @SnapshotName("enableValidation")
  void generatePojo_when_enableValidation_then_correctPojoGenerated(ValidationApi validationApi) {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        defaultTestSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withStagedBuilder(fullStagedBuilderSettingsBuilder().enabled(false).build())
            .withEnableValidation(true)
            .withValidationApi(validationApi);

    final JavaObjectPojo pojo =
        (JavaObjectPojo)
            JavaObjectPojo.wrap(
                    ObjectPojoBuilder.create()
                        .name(componentName("User", "Dto"))
                        .description("User of the Application")
                        .nullability(NOT_NULLABLE)
                        .pojoXml(PojoXml.noXmlDefinition())
                        .members(
                            PList.of(
                                new PojoMember(
                                    Name.ofString("id"),
                                    "ID of this user",
                                    IntegerType.formatLong()
                                        .withConstraints(Constraints.ofMax(new Max(50))),
                                    PropertyScope.DEFAULT,
                                    REQUIRED),
                                new PojoMember(
                                    Name.ofString("name"),
                                    "Name of this user",
                                    StringType.noFormat()
                                        .withConstraints(Constraints.ofSize(Size.of(10, 15))),
                                    PropertyScope.DEFAULT,
                                    REQUIRED),
                                new PojoMember(
                                    Name.ofString("lastName"),
                                    "Lastname of this user",
                                    StringType.noFormat()
                                        .withConstraints(Constraints.ofSize(Size.ofMin(10))),
                                    PropertyScope.DEFAULT,
                                    REQUIRED),
                                new PojoMember(
                                    Name.ofString("nickName"),
                                    "Nickname of this user",
                                    StringType.noFormat()
                                        .withConstraints(Constraints.ofSize(Size.ofMax(50))),
                                    PropertyScope.DEFAULT,
                                    REQUIRED),
                                new PojoMember(
                                    Name.ofString("email"),
                                    "Email of this user",
                                    StringType.noFormat().withConstraints(Constraints.ofEmail()),
                                    PropertyScope.DEFAULT,
                                    REQUIRED),
                                new PojoMember(
                                    Name.ofString("height"),
                                    "Height of this user",
                                    NumericType.formatDouble()
                                        .withConstraints(
                                            Constraints.ofDecimalMin(new DecimalMin("120.0", true))
                                                .withDecimalMax(new DecimalMax("199", false))),
                                    PropertyScope.DEFAULT,
                                    REQUIRED),
                                new PojoMember(
                                    Name.ofString("level"),
                                    "Level of this user",
                                    IntegerType.formatLong()
                                        .withConstraints(Constraints.ofMin(new Min(5))),
                                    PropertyScope.DEFAULT,
                                    OPTIONAL),
                                new PojoMember(
                                    Name.ofString("uppercase"),
                                    "Something uppercase",
                                    StringType.noFormat()
                                        .withConstraints(
                                            Constraints.ofPattern(
                                                Pattern.ofUnescapedString("^(\\d[A-Z]*)"))),
                                    PropertyScope.DEFAULT,
                                    OPTIONAL),
                                new PojoMember(
                                    Name.ofString("multipleOfValue"),
                                    "Multiple of value",
                                    IntegerType.formatLong()
                                        .withConstraints(
                                            Constraints.ofMultipleOf(
                                                new MultipleOf(new BigDecimal("5")))),
                                    PropertyScope.DEFAULT,
                                    OPTIONAL),
                                new PojoMember(
                                    Name.ofString("anotherPojo"),
                                    "Another Pojo",
                                    StandardObjectType.ofName(
                                        PojoName.ofName(Name.ofString("AnotherPojo"))),
                                    PropertyScope.DEFAULT,
                                    OPTIONAL)))
                        .requiredAdditionalProperties(PList.empty())
                        .constraints(
                            Constraints.ofPropertiesCount(
                                PropertyCount.ofMinAndMaxProperties(5, 15)))
                        .additionalProperties(anyTypeAllowed())
                        .build(),
                    TypeMappings.empty())
                .getDefaultPojo();

    final String content = generator.generate(pojo, pojoSettings, javaWriter()).asString();

    expect.scenario(validationApi.getValue()).toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("pojoWithEnumAndEnumDescription")
  void generatePojo_when_pojoWithEnumAndEnumDescription_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        defaultTestSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withStagedBuilder(fullStagedBuilderSettingsBuilder().enabled(false).build())
            .withEnumDescriptionSettings(EnumDescriptionSettings.enabled("`__ENUM__`:", false));

    final JavaObjectPojo pojo =
        (JavaObjectPojo)
            JavaObjectPojo.wrap(
                    ObjectPojoBuilder.create()
                        .name(componentName("User", "Dto"))
                        .description("User of the Application")
                        .nullability(NOT_NULLABLE)
                        .pojoXml(PojoXml.noXmlDefinition())
                        .members(
                            PList.of(
                                new PojoMember(
                                    Name.ofString("language"),
                                    "Preferred language of this user\n"
                                        + "* `GERMAN`: German language\n"
                                        + "* `ENGLISH`: English language",
                                    EnumType.ofNameAndMembers(
                                        Name.ofString("LanguageEnum"),
                                        PList.of("GERMAN", "ENGLISH")),
                                    PropertyScope.DEFAULT,
                                    OPTIONAL)))
                        .requiredAdditionalProperties(PList.empty())
                        .constraints(Constraints.empty())
                        .additionalProperties(anyTypeAllowed())
                        .build(),
                    TypeMappings.empty())
                .getDefaultPojo();

    final String content = generator.generate(pojo, pojoSettings, javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("pojoWithEnumAndEnumDescriptionAndJacksonSupport")
  void
      generatePojo_when_pojoWithEnumAndEnumDescriptionAndJacksonSupport_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final PojoSettings pojoSettings =
        defaultTestSettings()
            .withStagedBuilder(fullStagedBuilderSettingsBuilder().enabled(false).build())
            .withEnumDescriptionSettings(EnumDescriptionSettings.enabled("`__ENUM__`:", false));

    final JavaObjectPojo pojo =
        (JavaObjectPojo)
            JavaObjectPojo.wrap(
                    ObjectPojoBuilder.create()
                        .name(componentName("User", "Dto"))
                        .description("User of the Application")
                        .nullability(NOT_NULLABLE)
                        .pojoXml(PojoXml.noXmlDefinition())
                        .members(
                            PList.of(
                                new PojoMember(
                                    Name.ofString("language"),
                                    "Preferred language of this user\n"
                                        + "* `GERMAN`: German language\n"
                                        + "* `ENGLISH`: English language",
                                    EnumType.ofNameAndMembers(
                                        Name.ofString("LanguageEnum"),
                                        PList.of("GERMAN", "ENGLISH")),
                                    PropertyScope.DEFAULT,
                                    OPTIONAL)))
                        .requiredAdditionalProperties(PList.empty())
                        .constraints(Constraints.empty())
                        .additionalProperties(anyTypeAllowed())
                        .build(),
                    TypeMappings.empty())
                .getDefaultPojo();

    final String content = generator.generate(pojo, pojoSettings, javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("necessityAndNullabilityVariants")
  void generatePojo_when_necessityAndNullabilityVariants_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final String content =
        generator
            .generate(
                JavaPojos.allNecessityAndNullabilityVariants(),
                defaultTestSettings()
                    .withStagedBuilder(fullStagedBuilderSettingsBuilder().enabled(true).build()),
                javaWriter())
            .asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("objectWithArrayWithUniqueItems")
  void generatePojo_when_objectWithArrayWithUniqueItems_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final JavaPojoMember member =
        TestJavaPojoMembers.list(
            StringType.noFormat(),
            REQUIRED,
            NOT_NULLABLE,
            Constraints.ofUniqueItems(true),
            TypeMappings.empty());

    final String content =
        generator
            .generate(
                JavaPojos.objectPojo(PList.single(member)), defaultTestSettings(), javaWriter())
            .asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("objectWithMapMember")
  void generatePojo_when_objectWithMapMember_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final JavaPojoMember member =
        TestJavaPojoMembers.map(
            StringType.noFormat(),
            StringType.uuid(),
            REQUIRED,
            NOT_NULLABLE,
            Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(3, 8)));

    final String content =
        generator
            .generate(
                JavaPojos.objectPojo(PList.single(member)), defaultTestSettings(), javaWriter())
            .asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("noAdditionalPropertiesAllowed")
  void generatePojo_when_noAdditionalPropertiesAllowed_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();
    final String content =
        generator
            .generate(
                JavaPojos.objectPojo(PList.empty(), JavaAdditionalProperties.notAllowed()),
                defaultTestSettings(),
                javaWriter())
            .asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("inlinedEnumMapObject")
  void generatePojo_when_inlinedEnumMapObject_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();
    final JavaObjectPojo objectPojo =
        JavaPojos.objectPojo(
            PList.empty(),
            JavaAdditionalProperties.allowedFor(
                JavaEnumType.wrap(
                    EnumType.ofNameAndMembers(
                        Name.ofString("ColorEnum"), PList.of("green", "yellow", "red")))));
    final String content =
        generator.generate(objectPojo, defaultTestSettings(), javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("objectMap")
  void generatePojo_when_objectMap_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();
    final JavaObjectPojo objectPojo =
        JavaPojos.objectPojo(
            PList.empty(),
            JavaAdditionalProperties.allowedFor(
                JavaObjectType.wrap(
                    StandardObjectType.ofName(pojoName("Hello", "Dto")), TypeMappings.empty())));
    final String content =
        generator.generate(objectPojo, defaultTestSettings(), javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("anyTypeMap")
  void generatePojo_when_anyTypeMap_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();
    final JavaObjectPojo objectPojo =
        JavaPojos.objectPojo(PList.empty(), JavaAdditionalProperties.allowedFor(anyType()));
    final String content =
        generator.generate(objectPojo, defaultTestSettings(), javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("integerMap")
  void generatePojo_when_integerMap_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();
    final JavaObjectPojo objectPojo =
        JavaPojos.objectPojo(
            PList.empty(),
            JavaAdditionalProperties.allowedFor(
                JavaIntegerType.wrap(IntegerType.formatInteger(), TypeMappings.empty())));
    final String content =
        generator.generate(objectPojo, defaultTestSettings(), javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("objectMapDisabledValidation")
  void generatePojo_when_objectMapDisabledValidation_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();
    final JavaObjectPojo objectPojo =
        JavaPojos.objectPojo(
            PList.empty(),
            JavaAdditionalProperties.allowedFor(
                JavaObjectType.wrap(
                    StandardObjectType.ofName(pojoName("Hello", "Dto")), TypeMappings.empty())));
    final String content =
        generator
            .generate(objectPojo, defaultTestSettings().withEnableValidation(false), javaWriter())
            .asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("objectMapNoJsonSupport")
  void generatePojo_when_objectMapNoJsonSupport_then_correctPojoGenerated() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();
    final JavaObjectPojo objectPojo =
        JavaPojos.objectPojo(
            PList.empty(),
            JavaAdditionalProperties.allowedFor(
                JavaObjectType.wrap(
                    StandardObjectType.ofName(pojoName("Hello", "Dto")), TypeMappings.empty())));
    final String content =
        generator
            .generate(
                objectPojo, defaultTestSettings().withJsonSupport(JsonSupport.NONE), javaWriter())
            .asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  @SnapshotName("allOfComposition")
  void generate_when_allOfComposition_then_correctOutput() {
    final JavaObjectPojo javaPojo =
        createCompositionPojo(
            (builder, pojos) -> builder.allOfComposition(AllOfComposition.fromPojos(pojos)));

    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final Writer writer = generator.generate(javaPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("oneOfComposition")
  void generate_when_oneOfComposition_then_correctOutput() {
    final JavaObjectPojo javaPojo =
        createCompositionPojo(
            (builder, pojos) -> builder.oneOfComposition(OneOfComposition.fromPojos(pojos)));

    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final Writer writer = generator.generate(javaPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("anyOfComposition")
  void generate_when_anyOfComposition_then_correctOutput() {
    final JavaObjectPojo javaPojo =
        createCompositionPojo(
            (builder, pojos) -> builder.anyOfComposition(AnyOfComposition.fromPojos(pojos)));

    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final Writer writer = generator.generate(javaPojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("pojoWithRequiredAdditionalProperties")
  void generate_when_pojoWithRequiredAdditionalProperties_then_correctOutput() {
    final JavaObjectPojo pojo =
        JavaPojos.objectPojo(requiredEmail(), optionalBirthdate())
            .withRequiredAdditionalProperties(
                PList.single(
                    new JavaRequiredAdditionalProperty(JavaName.fromString("name"), stringType())));

    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("illegalIdentifierPojo")
  void generate_when_illegalIdentifierPojo_then_correctOutput() {
    final ObjectPojoGenerator generator = new ObjectPojoGenerator();

    final Writer writer =
        generator.generate(illegalIdentifierPojo(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  private static JavaObjectPojo createCompositionPojo(
      BiFunction<ObjectPojoBuilder.Builder, NonEmptyList<Pojo>, ObjectPojoBuilder.Builder>
          addCompositions) {

    final ObjectPojo userPojo =
        ObjectPojoBuilder.create()
            .name(componentName("User", "Dto"))
            .description("user")
            .nullability(NOT_NULLABLE)
            .pojoXml(PojoXml.noXmlDefinition())
            .members(PList.of(requiredUsername()))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();
    final ObjectPojo adminPojo =
        ObjectPojoBuilder.create()
            .name(componentName("Admin", "Dto"))
            .description("admin")
            .nullability(NOT_NULLABLE)
            .pojoXml(PojoXml.noXmlDefinition())
            .members(PList.of(requiredBirthdate()))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .build();
    final ObjectPojoBuilder.Builder builder =
        ObjectPojoBuilder.create()
            .name(componentName("Person", "Dto"))
            .description("person")
            .nullability(NOT_NULLABLE)
            .pojoXml(PojoXml.noXmlDefinition())
            .members(PList.of(requiredString()))
            .requiredAdditionalProperties(PList.empty())
            .constraints(Constraints.empty())
            .additionalProperties(anyTypeAllowed())
            .andOptionals();

    final ObjectPojo objectPojo =
        addCompositions.apply(builder, NonEmptyList.of(adminPojo, userPojo)).build();

    return (JavaObjectPojo) JavaObjectPojo.wrap(objectPojo, TypeMappings.empty()).getDefaultPojo();
  }
}
