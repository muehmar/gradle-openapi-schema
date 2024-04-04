package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.multipojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.multipojo.MultiPojoMergeMethodGenerator.multiPojoMergeMethodGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.allNecessityAndNullabilityVariants;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliary.MultiPojoContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class MultiPojoMergeMethodGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("anyOfContainer")
  void multiPojoMergeMethodGenerator_when_anyOfContainer_then_correctOutput() {
    final Generator<MultiPojoContainer, PojoSettings> generator = multiPojoMergeMethodGenerator();

    final MultiPojoContainer multiPojoContainer =
        JavaPojos.anyOfPojo(
                sampleObjectPojo1(), allNecessityAndNullabilityVariants(), sampleObjectPojo2())
            .getMultiPojoContainer()
            .head();

    final Writer writer =
        generator.generate(multiPojoContainer, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
