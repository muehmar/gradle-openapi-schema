package com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.pojo.PojoConstructorGenerator.ConstructorContent;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class PojoConstructorGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("objectPojo")
  void generator_when_objectPojo_then_correctOutput() {
    final Generator<ConstructorContent, PojoSettings> generator =
        PojoConstructorGenerator.generator();

    final Writer writer =
        generator.generate(
            ((JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants())
                .getConstructorContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("necessityAndNullabilityObjectPojo")
  void generator_when_necessityAndNullabilityObjectPojo_then_correctOutput() {
    final Generator<ConstructorContent, PojoSettings> generator =
        PojoConstructorGenerator.generator();

    final Writer writer =
        generator.generate(
            ((JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants())
                .getConstructorContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("arrayPojo")
  void generator_when_arrayPojo_then_correctOutput() {
    final Generator<ConstructorContent, PojoSettings> generator =
        PojoConstructorGenerator.generator();

    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo().getConstructorContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    expect.toMatchSnapshot(writer.asString());
  }
}
