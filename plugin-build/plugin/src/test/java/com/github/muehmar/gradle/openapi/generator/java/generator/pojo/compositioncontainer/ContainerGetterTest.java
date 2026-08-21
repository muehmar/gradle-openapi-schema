package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.ContainerGetter.containerGetter;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.allNecessityAndNullabilityVariants;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static com.github.muehmar.gradle.openapi.snapshot.SnapshotUtil.writerSnapshot;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.DiscriminatableJavaComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class ContainerGetterTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOfPojo")
  void containerGetter_when_oneOfPojo_then_correctOutput() {
    final Generator<DiscriminatableJavaComposition, PojoSettings> generator = containerGetter();
    final JavaOneOfComposition javaOneOfComposition =
        JavaOneOfComposition.fromPojos(
            NonEmptyList.of(sampleObjectPojo1(), allNecessityAndNullabilityVariants()));

    final Writer writer =
        generator.generate(javaOneOfComposition, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }

  @Test
  @SnapshotName("anyOfPojo")
  void containerGetter_when_anyOfPojo_then_correctOutput() {
    final Generator<DiscriminatableJavaComposition, PojoSettings> generator = containerGetter();

    final JavaAnyOfComposition javaAnyOfComposition =
        JavaAnyOfComposition.fromPojos(
            NonEmptyList.of(sampleObjectPojo1(), allNecessityAndNullabilityVariants()));

    final Writer writer =
        generator.generate(javaAnyOfComposition, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writerSnapshot(writer));
  }
}
