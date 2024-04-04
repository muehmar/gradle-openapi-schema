package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.MemberContent;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class MemberGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_samplePojo_then_correctOutputAndRef() {
    final Generator<MemberContent, PojoSettings> gen = memberGenerator();

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants().getMemberContent(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(SnapshotUtil.writerSnapshot(writer));
  }

  @Test
  @SnapshotName("illegalIdentifierPojo")
  void generate_when_illegalIdentifierPojo_then_matchSnapshot() {
    final Generator<MemberContent, PojoSettings> gen = memberGenerator();

    final Writer writer =
        gen.generate(
            JavaPojos.illegalIdentifierPojo().getMemberContent(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(SnapshotUtil.writerSnapshot(writer));
  }

  @Test
  @SnapshotName("arrayPojo")
  void generate_when_arrayPojo_then_correctOutputAndRef() {
    final Generator<MemberContent, PojoSettings> gen = memberGenerator();

    final Writer writer =
        gen.generate(JavaPojos.arrayPojo().getMemberContent(), defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(SnapshotUtil.writerSnapshot(writer));
  }
}
