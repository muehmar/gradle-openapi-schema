package com.github.muehmar.gradle.openapi.generator.java.generator.pojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.memberGenerator;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.JacksonRefs;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.MemberGenerator.MemberContent;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
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

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_MAP::equals));

    final String output = writer.asString();
    expect.toMatchSnapshot(output);
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

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_MAP::equals));

    final String output = writer.asString();
    expect.toMatchSnapshot(output);
  }

  @Test
  void generate_when_arrayPojo_then_correctOutputAndRef() {
    final Generator<MemberContent, PojoSettings> gen = memberGenerator();

    final Writer writer =
        gen.generate(JavaPojos.arrayPojo().getMemberContent(), defaultTestSettings(), javaWriter());

    assertTrue(writer.getRefs().exists(JacksonRefs.JSON_VALUE::equals));

    final String output = writer.asString();
    assertEquals("@JsonValue\n" + "private final List<Double> value;", output);
  }
}
