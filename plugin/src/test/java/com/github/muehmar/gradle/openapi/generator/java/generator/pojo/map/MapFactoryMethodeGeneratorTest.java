package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.map;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.simpleMapPojo;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class MapFactoryMethodeGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("simpleMapPojo")
  void generate_when_simpleMapPojo_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MapFactoryMethodeGenerator.mapFactoryMethodeGenerator();

    final Writer writer =
        generator.generate(
            simpleMapPojo(JavaAdditionalProperties.anyTypeAllowed()),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generate_when_objectPojo_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MapFactoryMethodeGenerator.mapFactoryMethodeGenerator();

    final Writer writer =
        generator.generate(
            sampleObjectPojo1(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertEquals("", writer.asString());
  }

  @Test
  @SnapshotName("mapPojoNoAdditionalProperties")
  void generate_when_mapPojoNoAdditionalProperties_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MapFactoryMethodeGenerator.mapFactoryMethodeGenerator();

    final Writer writer =
        generator.generate(
            simpleMapPojo(JavaAdditionalProperties.notAllowed()),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals("", writer.asString());
  }
}
