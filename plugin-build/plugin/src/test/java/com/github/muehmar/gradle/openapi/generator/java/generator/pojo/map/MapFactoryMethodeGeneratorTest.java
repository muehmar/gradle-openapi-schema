package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.map;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.simpleMapPojo;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
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
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  void generate_when_objectPojo_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator =
        MapFactoryMethodeGenerator.mapFactoryMethodeGenerator();

    final Writer writer =
        generator.generate(sampleObjectPojo1(), defaultTestSettings(), javaWriter());

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
            defaultTestSettings(),
            javaWriter());

    assertEquals("", writer.asString());
  }
}
