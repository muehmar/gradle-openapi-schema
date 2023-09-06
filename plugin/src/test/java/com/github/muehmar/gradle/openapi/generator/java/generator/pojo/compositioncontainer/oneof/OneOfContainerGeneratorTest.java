package com.github.muehmar.gradle.openapi.generator.java.generator.pojo.compositioncontainer.oneof;

import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo1;
import static com.github.muehmar.gradle.openapi.generator.java.model.pojo.JavaPojos.sampleObjectPojo2;
import static com.github.muehmar.gradle.openapi.generator.settings.TestPojoSettings.defaultTestSettings;
import static io.github.muehmar.codegenerator.writer.Writer.javaWriter;

import au.com.origin.snapshots.Expect;
import au.com.origin.snapshots.annotations.SnapshotName;
import ch.bluecare.commons.data.NonEmptyList;
import com.github.muehmar.gradle.openapi.generator.java.model.JavaPojoNames;
import com.github.muehmar.gradle.openapi.generator.java.model.composition.JavaOneOfComposition;
import com.github.muehmar.gradle.openapi.generator.java.model.pojo.auxiliaryy.OneOfContainer;
import com.github.muehmar.gradle.openapi.generator.settings.PojoSettings;
import com.github.muehmar.gradle.openapi.snapshot.SnapshotTest;
import io.github.muehmar.codegenerator.Generator;
import io.github.muehmar.codegenerator.writer.Writer;
import org.junit.jupiter.api.Test;

@SnapshotTest
class OneOfContainerGeneratorTest {
  private Expect expect;

  @Test
  @SnapshotName("oneOfComposition")
  void generate_when_oneOfComposition_then_correctOutput() {
    final Generator<OneOfContainer, PojoSettings> generator =
        OneOfContainerGenerator.oneOfContainerGenerator();

    final JavaOneOfComposition oneOfComposition =
        JavaOneOfComposition.fromPojos(NonEmptyList.of(sampleObjectPojo1(), sampleObjectPojo2()));
    final OneOfContainer oneOfContainer =
        new OneOfContainer(JavaPojoNames.invoiceName(), oneOfComposition);

    final Writer writer = generator.generate(oneOfContainer, defaultTestSettings(), javaWriter());

    expect.toMatchSnapshot(writer.asString());
  }
}
