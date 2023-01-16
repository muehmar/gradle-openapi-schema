package com.github.muehmar.gradle.openapi.generator.java.generator.freeform;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaFreeFormPojo;
import com.github.muehmar.gradle.openapi.generator.model.PojoName;
import com.github.muehmar.gradle.openapi.generator.model.constraints.Constraints;
import com.github.muehmar.gradle.openapi.generator.model.constraints.PropertyCount;
import com.github.muehmar.gradle.openapi.generator.model.pojo.FreeFormPojo;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class FreeFormPojoGeneratorTest {

  public static final FreeFormPojo FREE_FORM_POJO =
      FreeFormPojo.of(
          PojoName.ofNameAndSuffix("FreeForm", "Dto"),
          "Free form object",
          Constraints.ofPropertiesCount(PropertyCount.ofMinAndMaxProperties(5, 10)));
  private Expect expect;

  @Test
  @SnapshotName("freeFormWithDefaultSettings")
  void generate_when_defaultSettings_then_matchSnapshot() {
    final FreeFormPojoGenerator gen = new FreeFormPojoGenerator();

    final Writer generate =
        gen.generate(
            JavaFreeFormPojo.wrap(FREE_FORM_POJO),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(generate.asString());
  }

  @Test
  @SnapshotName("freeFormWithNoJsonSupport")
  void generate_when_noJsonSupport_then_matchSnapshot() {
    final FreeFormPojoGenerator gen = new FreeFormPojoGenerator();

    final Writer generate =
        gen.generate(
            JavaFreeFormPojo.wrap(FREE_FORM_POJO),
            TestPojoSettings.defaultSettings().withJsonSupport(JsonSupport.NONE),
            Writer.createDefault());

    expect.toMatchSnapshot(generate.asString());
  }

  @Test
  @SnapshotName("freeFormWithValidationDisabled")
  void generate_when_validationDisabled_then_matchSnapshot() {
    final FreeFormPojoGenerator gen = new FreeFormPojoGenerator();

    final Writer generate =
        gen.generate(
            JavaFreeFormPojo.wrap(FREE_FORM_POJO),
            TestPojoSettings.defaultSettings().withEnableValidation(false),
            Writer.createDefault());

    expect.toMatchSnapshot(generate.asString());
  }
}
