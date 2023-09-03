package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;
import static org.junit.jupiter.api.Assertions.assertEquals;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import au.com.origin.snapshots.junit5.SnapshotExtension;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaAdditionalProperties;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@ExtendWith(SnapshotExtension.class)
class FinalOptionalMemberBuilderGeneratorTest {
  private Expect expect;

  @SnapshotName("allNecessityAndNullabilityVariants")
  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  void generate_when_allNecessityAndNullabilityVariants_then_correctOutput(
      SafeBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        FinalOptionalMemberBuilderGenerator.finalOptionalMemberBuilderGenerator(variant);

    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants(),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName("noAdditionalPropertiesAllowed")
  void generate_when_noAdditionalPropertiesAllowed_then_noAdditionalPropertiesSetter(
      SafeBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        FinalOptionalMemberBuilderGenerator.finalOptionalMemberBuilderGenerator(variant);

    final Writer writer =
        gen.generate(
            JavaPojos.objectPojo(PList.empty(), JavaAdditionalProperties.notAllowed()),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    assertEquals(0, writer.getRefs().size());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }
}
