package com.github.muehmar.gradle.openapi.generator.java.generator.enumpojo;

import static com.github.muehmar.gradle.openapi.generator.model.name.ComponentNames.componentName;
import static com.github.muehmar.gradle.openapi.generator.settings.StagedBuilderSettingsBuilder.fullStagedBuilderSettingsBuilder;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaEnumPojo;
import com.github.muehmar.gradle.openapi.generator.model.pojo.EnumPojo;
import com.github.muehmar.gradle.openapi.generator.settings.JsonSupport;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import org.junit.jupiter.api.Test;

@SnapshotTest
class EnumGeneratorTest {
  private Expect expect;

  private static final JavaEnumPojo GENDER_ENUM_POJO =
      JavaEnumPojo.wrap(
          EnumPojo.of(
              componentName("Gender", "Dto"), "Gender of a user", PList.of("MALE", "FEMALE")));

  @Test
  void generatePojo_when_enumPojo_then_correctPojoGenerated() {
    final EnumGenerator generator = EnumGenerator.topLevel();

    final PojoSettings pojoSettings =
        defaultTestSettings()
            .withJsonSupport(JsonSupport.NONE)
            .withStagedBuilder(fullStagedBuilderSettingsBuilder().enabled(false).build())
            .withEnableValidation(true);

    final String content =
        generator.generate(GENDER_ENUM_POJO.asEnumContent(), pojoSettings, javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }

  @Test
  void generatePojo_when_enumPojoAndJacksonSupport_then_correctPojoGenerated() {
    final EnumGenerator generator = EnumGenerator.topLevel();

    final PojoSettings pojoSettings =
        defaultTestSettings()
            .withStagedBuilder(fullStagedBuilderSettingsBuilder().enabled(false).build())
            .withEnableValidation(true);

    final String content =
        generator.generate(GENDER_ENUM_POJO.asEnumContent(), pojoSettings, javaWriter()).asString();

    expect.toMatchSnapshot(content);
  }
}
