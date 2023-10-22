package com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator.equalsMethod;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.EqualsGenerator.EqualsContent;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class EqualsGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctEqualsMethod() {
    final Generator<EqualsContent, PojoSettings> generator = equalsMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants().getEqualsContent(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("illegalIdentifierPojo")
  void generate_when_illegalIdentifierPojo_then_correctEqualsMethod() {
    final Generator<EqualsContent, PojoSettings> generator = equalsMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.illegalIdentifierPojo().getEqualsContent(),
            defaultTestSettings(),
            javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("arrayPojo")
  void generate_when_arrayPojo_then_correctEqualsMethod() {
    final Generator<EqualsContent, PojoSettings> generator = equalsMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo().getEqualsContent(), defaultTestSettings(), javaWriter());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OBJECTS::equals));

    expect.toMatchSnapshot(writer.asString());
  }
}
