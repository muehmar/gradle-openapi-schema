package com.github.muehmar.gradle.openapi.generator.java.generator.shared.safebuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.JavaRefs;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(SnapshotExtension.class)
class FinalOptionalMemberBuilderGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctOutput() {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        FinalOptionalMemberBuilderGenerator.generator();

    final Writer writer =
        gen.generate(
            (JavaObjectPojo) JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_MAP::equals));
    assertTrue(writer.getRefs().exists(JavaRefs.JAVA_UTIL_HASH_MAP::equals));

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("noAdditionalPropertiesAllowed")
  void generate_when_noAdditionalPropertiesAllowed_then_noAdditionalPropertiesSetter() {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        FinalOptionalMemberBuilderGenerator.generator();

    final Writer writer =
        gen.generate(
            JavaPojos.objectPojo(PList.empty(), JavaAdditionalProperties.notAllowed()),
            TestPojoSettings.defaultSettings(),
            Writer.createDefault());

    assertEquals(0, writer.getRefs().size());

    expect.toMatchSnapshot(writer.asString());
  }
}
