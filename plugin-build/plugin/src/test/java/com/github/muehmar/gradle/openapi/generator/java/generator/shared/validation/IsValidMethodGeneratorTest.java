package com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.validation.IsValidMethodGenerator.isValidMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class IsValidMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("sampleObjectPojo1")
  void generate_when_sampleObjectPojo1_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = isValidMethodGenerator();

    final JavaObjectPojo pojo = sampleObjectPojo1();

    final Writer writer = generator.generate(pojo, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
