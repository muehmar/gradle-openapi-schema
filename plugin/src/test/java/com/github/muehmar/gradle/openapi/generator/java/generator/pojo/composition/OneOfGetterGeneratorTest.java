package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.composition.OneOfGetterGenerator.oneOfGetterGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class OneOfGetterGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOfPojo")
  void generate_when_oneOfPojo_then_matchSnapshot() {
    final Generator<JavaObjectPojo, PojoSettings> generator = oneOfGetterGenerator();

    final Writer writer =
        generator.generate(
            JavaPojos.oneOfPojo(sampleObjectPojo1(), sampleObjectPojo2()),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(SnapshotUtil.writerSnapshot(writer));
  }

  @Test
  void generate_when_noOneOfPojo_then_noOutput() {
    final Generator<JavaObjectPojo, PojoSettings> generator = oneOfGetterGenerator();

    final Writer writer =
        generator.generate(sampleObjectPojo1(), defaultTestSettings(), javaWriter());

    assertEquals("", writer.asString());
  }
}
