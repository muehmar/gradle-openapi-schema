package com.github.muehmar.gradle.openapi.generator.java.generator.map;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaMapPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.pojo.MapPojo;
import com.github.muehmar.gradle.openapi.generator.model.type.IntegerType;
import com.github.muehmar.gradle.openapi.generator.model.type.NoType;
import com.github.muehmar.gradle.openapi.generator.model.type.ObjectType;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TypeMappings;
import com.github.muehmar.gradle.openapi.generator.settings.ValidationApi;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@ExtendWith(SnapshotExtension.class)
class MapPojoGeneratorTest {

  public static final MapPojo OBJECT_MAP_POJO =
      MapPojo.of(
          PojoName.ofNameAndSuffix("ObjectMap", "Dto"),
          "Object map",
          NoType.create(),
          Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(5, 10)));
  public static final MapPojo DTO_MAP_POJO =
      MapPojo.of(
          PojoName.ofNameAndSuffix("DtoMap", "Dto"),
          "DTO map",
          ObjectType.ofName(PojoName.ofNameAndSuffix("Other", "Dto")),
          Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(1, 3)));

  public static final MapPojo INTEGER_MAP_POJO =
      MapPojo.of(
          PojoName.ofNameAndSuffix("FreeForm", "Dto"),
          "Free form object",
          IntegerType.formatInteger(),
          Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(5, 10)));
  private Expect expect;

  @ParameterizedTest
  @EnumSource(ValidationApi.class)
  @SnapshotName("objectMapWithDefaultSettings")
  void generate_when_defaultSettings_then_matchSnapshot(ValidationApi validationApi) {
    final MapPojoGenerator gen = new MapPojoGenerator();

    final Writer generate =
        gen.generate(
            JavaMapPojo.wrap(OBJECT_MAP_POJO, TypeMappings.empty()),
            TestPojoSettings.defaultSettings().withValidationApi(validationApi),
            Writer.createDefault());

    expect.scenario(validationApi.getValue()).toMatchSnapshot(generate.asString());
  }

  @ParameterizedTest
  @EnumSource(ValidationApi.class)
  @SnapshotName("dtoMapWithDefaultSettings")
  void generate_when_dtoMapAndDefaultSettings_then_matchSnapshot(ValidationApi validationApi) {
    final MapPojoGenerator gen = new MapPojoGenerator();

    final Writer generate =
        gen.generate(
            JavaMapPojo.wrap(DTO_MAP_POJO, TypeMappings.empty()),
            TestPojoSettings.defaultSettings().withValidationApi(validationApi),
            Writer.createDefault());

    expect.scenario(validationApi.getValue()).toMatchSnapshot(generate.asString());
  }

  @ParameterizedTest
  @EnumSource(ValidationApi.class)
  @SnapshotName("integerMapWithDefaultSettings")
  void generate_when_integerMapAndDefaultSettings_then_matchSnapshot(ValidationApi validationApi) {
    final MapPojoGenerator gen = new MapPojoGenerator();

    final Writer generate =
        gen.generate(
            JavaMapPojo.wrap(INTEGER_MAP_POJO, TypeMappings.empty()),
            TestPojoSettings.defaultSettings().withValidationApi(validationApi),
            Writer.createDefault());

    expect.scenario(validationApi.getValue()).toMatchSnapshot(generate.asString());
  }

  @Test
  @SnapshotName("objectMapWithNoJsonSupport")
  void generate_when_noJsonSupport_then_matchSnapshot() {
    final MapPojoGenerator gen = new MapPojoGenerator();

    final Writer generate =
        gen.generate(
            JavaMapPojo.wrap(OBJECT_MAP_POJO, TypeMappings.empty()),
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    expect.toMatchSnapshot(generate.asString());
  }

  @Test
  @SnapshotName("objectMapWithValidationDisabled")
  void generate_when_validationDisabled_then_matchSnapshot() {
    final MapPojoGenerator gen = new MapPojoGenerator();

    final Writer generate =
        gen.generate(
            JavaMapPojo.wrap(OBJECT_MAP_POJO, TypeMappings.empty()),
            TestPojoSettings.defaultSettings().withEnableValidation(false),
            Writer.createDefault());

    expect.toMatchSnapshot(generate.asString());
  }
}
