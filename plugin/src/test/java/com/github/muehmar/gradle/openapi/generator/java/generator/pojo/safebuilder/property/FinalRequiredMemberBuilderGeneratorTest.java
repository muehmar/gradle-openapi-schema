package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.property;

import static com.github.muehmar.gradle.openapi.generator.java.model.type.JavaAnyType.javaAnyType;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.PList;
import com.github.muehmar.gradle.openapi.generator.java.generator.pojo.safebuilder.SafeBuilderVariant;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaObjectPojo;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaRequiredAdditionalProperty;
import com.github.muehmar.gradle.openapi.generator.model.name.Name;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

@SnapshotTest
class FinalRequiredMemberBuilderGeneratorTest {
  private Expect expect;

  @ParameterizedTest
  @EnumSource(SafeBuilderVariant.class)
  @SnapshotName("allNecessityAndNullabilityVariants")
  void generate_when_allNecessityAndNullabilityVariants_then_correctOutput(
      SafeBuilderVariant variant) {
    final Generator<JavaObjectPojo, PojoSettings> gen =
        FinalRequiredMemberBuilderGenerator.finalRequiredMemberBuilderGenerator(variant);

    final PList<JavaRequiredAdditionalProperty> requiredAdditionalProperties =
        PList.single(
            JavaRequiredAdditionalProperty.fromNameAndType(Name.ofString("prop1"), javaAnyType()));
    final Writer writer =
        gen.generate(
            JavaPojos.allNecessityAndNullabilityVariants()
                .withRequiredAdditionalProperties(requiredAdditionalProperties),
            TestPojoSettings.defaultTestSettings(),
            javaWriter());

    expect.scenario(variant.name()).toMatchSnapshot(writerSnapshot(writer));
  }
}
