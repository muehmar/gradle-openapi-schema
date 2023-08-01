package com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc;

import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.ToStringGenerator.ToStringContent;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class ToStringGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctToStringMethod() {
    final Generator<ToStringContent, PojoSettings> generator = ToStringGenerator.toStringMethod();

    final Writer writer =
        generator.generate(
            ((JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants()).getToStringContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().isEmpty());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("arrayPojo")
  void generate_when_arrayPojo_then_correctToStringMethod() {
    final Generator<ToStringContent, PojoSettings> generator = ToStringGenerator.toStringMethod();

    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo().getToStringContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().isEmpty());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("byteArrayMember")
  void generate_when_byteArrayMember_then_correctToStringMethod() {
    final Generator<ToStringContent, PojoSettings> generator = ToStringGenerator.toStringMethod();

    final JavaObjectPojo pojo =
        JavaPojos.objectPojo(
            PList.of(JavaPojoMembers.byteArrayMember(), JavaPojoMembers.requiredDouble()));
    final Writer writer =
        generator.generate(
            pojo.getToStringContent(), TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_ARRAYS::equals));

    expect.toMatchSnapshot(writer.asString());
  }
}
