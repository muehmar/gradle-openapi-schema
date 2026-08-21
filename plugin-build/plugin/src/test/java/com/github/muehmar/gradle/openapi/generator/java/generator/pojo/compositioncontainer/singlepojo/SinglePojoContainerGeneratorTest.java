package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.singlepojo;

import static com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.singlepojo.SinglePojoContainerGenerator.singlePojoContainerGenerator;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaAnyOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.name.JavaPojoNames;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliary.SinglePojoContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class SinglePojoContainerGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOfComposition")
  void generate_when_oneOfComposition_then_correctOutput() {
    final Generator<SinglePojoContainer, PojoSettings> generator = singlePojoContainerGenerator();

    final JavaOneOfComposition oneOfComposition =
        JavaOneOfComposition.fromPojos(NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()));
    final SinglePojoContainer singlePojoContainer =
        new SinglePojoContainer(JavaPojoNames.invoiceName(), oneOfComposition);

    final Writer writer =
        generator.generate(singlePojoContainer, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }

  @Test
  @SnapshotName("anyOfComposition")
  void generate_when_anyOfComposition_then_correctOutput() {
    final Generator<SinglePojoContainer, PojoSettings> generator = singlePojoContainerGenerator();

    final JavaAnyOfComposition anyOfComposition =
        JavaAnyOfComposition.fromPojos(NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()));
    final SinglePojoContainer singlePojoContainer =
        new SinglePojoContainer(JavaPojoNames.invoiceName(), anyOfComposition);

    final Writer writer =
        generator.generate(singlePojoContainer, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
