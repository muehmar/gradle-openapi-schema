package com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc;

import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers.byteArrayMember;
import static com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMembers.requiredDouble;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.generator.shared.misc.HashCodeGenerator.HashCodeContent;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.TechnicalPojoMember;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class HashCodeGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctHashCodeMethod() {
    final Generator<HashCodeContent, PojoSettings> generator = HashCodeGenerator.hashCodeMethod();
    final Writer writer =
        generator.generate(
            ((JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants()).getHashCodeContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OBJECTS::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("arrayPojo")
  void generate_when_arrayPojo_then_correctHashCodeMethod() {
    final Generator<HashCodeContent, PojoSettings> generator = HashCodeGenerator.hashCodeMethod();
    final Writer writer =
        generator.generate(
            JavaPojos.arrayPojo().getHashCodeContent(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OBJECTS::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("byteArrayMember")
  void generate_when_byteArrayMember_then_correctHashCodeMethod() {
    final Generator<HashCodeContent, PojoSettings> generator = HashCodeGenerator.hashCodeMethod();

    final PList<TechnicalPojoMember> technicalMembers =
        PList.of(byteArrayMember(), requiredDouble()).flatMap(JavaPojoMember::getTechnicalMembers);
    final HashCodeContent hashCodeContent =
        HashCodeContentBuilder.create().technicalPojoMembers(technicalMembers).build();
    final Writer writer =
        generator.generate(
            hashCodeContent, TestPojoSettings.defaultSettings(), Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_ARRAYS::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_OBJECTS::equals));

    expect.toMatchSnapshot(writer.asString());
  }
}