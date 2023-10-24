package com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc;

import static com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator.toStringMethod;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator.ToStringContent;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class ToStringGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctToStringMethod() {
    final Generator<ToStringContent, PojoSettings> generator = toStringMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.allNecessityAndNullabilityVariants().getToStringContent(),
            defaultTestSettings(),
            javaWriter());

    assertTrue(writer.getRefs().isEmpty());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("illegalIdentifierPojo")
  void generate_when_illegalIdentifierPojo_then_correctToStringMethod() {
    final Generator<ToStringContent, PojoSettings> generator = toStringMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.illegalIdentifierPojo().getToStringContent(),
            defaultTestSettings(),
            javaWriter());

    assertTrue(writer.getRefs().isEmpty());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("arrayPojo")
  void generate_when_arrayPojo_then_correctToStringMethod() {
    final Generator<ToStringContent, PojoSettings> generator = toStringMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo().getToStringContent(), defaultTestSettings(), javaWriter());

    assertTrue(writer.getRefs().isEmpty());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("byteArrayMember")
  void generate_when_byteArrayMember_then_correctToStringMethod() {
    final Generator<ToStringContent, PojoSettings> generator = toStringMethod();

    final JavaObjectPojo pojo =
        JavaPojos.objectPojo(
            PList.of(JavaPojoMembers.byteArrayMember(), JavaPojoMembers.requiredDouble()));
    final Writer writer =
        generator.generate(pojo.getToStringContent(), defaultTestSettings(), javaWriter());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_ARRAYS::equals));

    expect.toMatchSnapshot(writer.asString());
  }
}
