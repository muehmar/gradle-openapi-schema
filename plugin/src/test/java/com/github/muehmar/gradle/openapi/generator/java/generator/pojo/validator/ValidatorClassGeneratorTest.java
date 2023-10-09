package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.validator.ValidatorClassGenerator.validationClassGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class ValidatorClassGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("sampleObjectPojo2")
  void generate_when_sampleObjectPojo2_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator = validationClassGenerator();

    final Writer writer =
        generator.generate(sampleObjectPojo2(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(SnapshotUtil.writerSnapshot(writer));
  }
}
